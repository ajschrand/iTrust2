package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.SatisfactionSurveyForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.models.enums.WaitTime;
import edu.ncsu.csc.iTrust2.services.AppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.SatisfactionSurveyService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for the API functionality for interacting with office visits
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APISatisfactionSurveyTest {

    @Autowired
    private MockMvc                   mvc;

    @Autowired
    private OfficeVisitService        officeVisitService;

    @Autowired
    private UserService<User>         userService;

    @Autowired
    private AppointmentRequestService appointmentRequestService;

    @Autowired
    private HospitalService           hospitalService;

    @Autowired
    private SatisfactionSurveyService surveyService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        officeVisitService.deleteAll();

        appointmentRequestService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User admin = new Personnel( new UserForm( "admin", "123456", Role.ROLE_ADMIN, 1 ) );

        userService.saveAll( List.of( patient, hcp, admin ) );

        final Hospital hosp = new Hospital();
        hosp.setAddress( "123 Raleigh Road" );
        hosp.setState( State.NC );
        hosp.setZip( "27514" );
        hosp.setName( "iTrust Test Hospital 2" );

        hospitalService.save( hosp );
    }

    /**
     * Builds an OfficeVisit so that we can test the SatisfactionSurvey related
     * to that visit.
     *
     * @return ov - an office visit to attach a survey to
     * @throws Exception
     */
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    private void buildOfficeVisit () throws Exception {
        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        final OfficeVisit ov = officeVisitService.build( visit );

        officeVisitService.save( ov );
    }

    /**
     * Tests getting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetNonExistentSurveys () throws Exception {
        mvc.perform( get( "/api/v1/surveys/" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        Assertions.assertEquals( 0, surveyService.count() );
    }

    /**
     * Tests getting a non existent surveys for an HCP and ensures that the
     * correct status is returned.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetNonExistentSurveysForHCP () throws Exception {
        mvc.perform( get( "/api/v1/surveys/hcp/hcp" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );
        Assertions.assertEquals( 0, surveyService.findByHcp( "hcp" ).size() );
    }

    /**
     * Tests handling of errors when creating a visit for a pre-scheduled
     * appointment.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testPatientSubmitSurvey () throws Exception {
        Assertions.assertEquals( 0, officeVisitService.count() );
        buildOfficeVisit();
        Assertions.assertEquals( 1, officeVisitService.count() );

        final User hcp = userService.findByName( "hcp" );
        final User patient = userService.findByName( "patient" );
        final OfficeVisit ov = officeVisitService.findByHcpAndPatient( hcp, patient ).get( 0 );
        Assertions.assertEquals( ov.getPatient().getUsername(), "patient" );

        final SatisfactionSurveyForm sform = new SatisfactionSurveyForm();
        sform.setOfficeVisitId( ov.getId() );
        sform.setWaitingRoomTime( WaitTime.LESS_THAN_FIVE );
        sform.setExaminationResponseTime( WaitTime.FIFTEEN_TO_TWENTY );
        sform.setTreatmentSatisfaction( 4 );
        sform.setVisitSatisfaction( 5 );
        sform.setComments( "I really enjoyed my visit with hcp." );

        mvc.perform( post( "/api/v1//surveys" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sform ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, surveyService.count() );
    }
}
