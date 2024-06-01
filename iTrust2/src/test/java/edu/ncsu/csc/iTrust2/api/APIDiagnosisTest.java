package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.DiagnosisForm;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.DiagnosisService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.ICDCodeService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.UserService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIDiagnosisTest {

    @Autowired
    private MockMvc            mvc;

    @Autowired
    private UserService<User>  userService;

    @Autowired
    private HospitalService    hospitalService;

    @Autowired
    private DiagnosisService   diagnosisService;

    @Autowired
    private ICDCodeService     icdCodeService;

    @Autowired
    private OfficeVisitService officeVisitService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User admin = new Personnel( new UserForm( "admin", "123456", Role.ROLE_ADMIN, 1 ) );

        userService.saveAll( List.of( patient, hcp, admin ) );

        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        hospitalService.save( hospital );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testDiagnoses () throws Exception {

        String content;

        // create 2 ICDCode to use
        final ICDCode code = new ICDCode();
        code.setCode( "T10" );
        code.setDescription( "Test 10" );

        // create an ICDCode to use
        final ICDCode code2 = new ICDCode();
        code2.setCode( "T11" );
        code2.setDescription( "Test 11" );

        icdCodeService.saveAll( List.of( code, code2 ) );

        // Create an office visit with two diagnoses
        final OfficeVisitForm form = new OfficeVisitForm();
        form.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        form.setHcp( "hcp" );
        form.setPatient( "patient" );
        form.setNotes( "Test office visit" );
        form.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        form.setHospital( "iTrust Test Hospital 2" );
        form.setHdl( 1 );
        form.setHeight( 1f );
        form.setWeight( 1f );
        form.setLdl( 1 );
        form.setTri( 100 );
        form.setDiastolic( 1 );
        form.setSystolic( 1 );
        form.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        form.setPatientSmokingStatus( PatientSmokingStatus.FORMER );

        final List<Diagnosis> list = new ArrayList<Diagnosis>();
        final Diagnosis d = new Diagnosis();
        d.setCode( code );
        d.setNote( "Test diagnosis" );
        list.add( d );
        final Diagnosis d2 = new Diagnosis();
        d2.setCode( code2 );
        d2.setNote( "Second Diagnosis" );
        list.add( d2 );

        form.setDiagnoses( list.stream().map( DiagnosisForm::new ).collect( Collectors.toList() ) );

        final OfficeVisit visit = officeVisitService.build( form );

        officeVisitService.save( visit );

        final OfficeVisit retrieved = officeVisitService.findAll().get( 0 );

        final Serializable id = retrieved.getId();

        d.setVisit( retrieved );

        // get the list of diagnoses for this office visit and make sure both
        // are there
        content = mvc.perform( get( "/api/v1/diagnosesforvisit/" + id ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        List<Diagnosis> dlist = TestUtils.gson().fromJson( content, new TypeToken<ArrayList<Diagnosis>>() {
        }.getType() );
        boolean flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() ) ) {
                flag = true;
                d.setId( dd.getId() );

            }
        }
        Assertions.assertTrue( flag );
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() ) ) {
                flag = true;
                d2.setId( dd.getId() );
            }
        }
        Assertions.assertTrue( flag );

        // get the list of diagnoses for this patient and make sure both are
        // there
        final List<Diagnosis> forPatient = diagnosisService.findByPatient( userService.findByName( "patient" ) );
        flag = false;
        for ( final Diagnosis dd : forPatient ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() ) ) {
                flag = true;
            }
        }
        Assertions.assertTrue( flag );
        flag = false;
        for ( final Diagnosis dd : forPatient ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() ) ) {
                flag = true;
            }
        }
        Assertions.assertTrue( flag );

        // edit a diagnosis within the editing of office visit and check they
        // work.
        form.setId( id + "" );
        d.setNote( "Edited" );
        form.setDiagnoses( list.stream().map( DiagnosisForm::new ).collect( Collectors.toList() ) );
        content = mvc.perform( put( "/api/v1/officevisits/" + id ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( form ) ) ).andReturn()
                .getResponse().getContentAsString();

        content = mvc.perform( get( "/api/v1/diagnosesforvisit/" + id ) ).andReturn().getResponse()
                .getContentAsString();
        dlist = TestUtils.gson().fromJson( content, new TypeToken<ArrayList<Diagnosis>>() {
        }.getType() );
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() ) ) {
                flag = true;
            }
        }
        Assertions.assertTrue( flag );
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() ) ) {
                flag = true;
            }
        }
        Assertions.assertTrue( flag );

        // edit the office visit and remove a diagnosis

        list.remove( d );
        form.setDiagnoses( list.stream().map( DiagnosisForm::new ).collect( Collectors.toList() ) );
        content = mvc.perform( put( "/api/v1/officevisits/" + id ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( form ) ) ).andReturn()
                .getResponse().getContentAsString();

        // check that the removed one is gone
        content = mvc.perform( get( "/api/v1/diagnosesforvisit/" + id ) ).andReturn().getResponse()
                .getContentAsString();
        dlist = TestUtils.gson().fromJson( content, new TypeToken<ArrayList<Diagnosis>>() {
        }.getType() );
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() ) ) {
                Assertions.fail( "Was not deleted!" );
            }
        }
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() ) ) {
                flag = true;
            }
        }
        Assertions.assertTrue( flag );

        /* Make sure all the editing didn't create any duplicates */
        Assertions.assertEquals( 2, diagnosisService.count() );

    }
}
