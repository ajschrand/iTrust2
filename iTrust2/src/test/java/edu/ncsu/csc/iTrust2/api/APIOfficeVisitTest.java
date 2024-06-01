package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.AppointmentRequestForm;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.services.AppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.BasicHealthMetricsService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
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
public class APIOfficeVisitTest {

    @Autowired
    private MockMvc                           mvc;

    @Autowired
    private OfficeVisitService                officeVisitService;

    @Autowired
    private UserService<User>                 userService;

    @Autowired
    private AppointmentRequestService         appointmentRequestService;

    @Autowired
    private HospitalService                   hospitalService;

    @Autowired
    private BasicHealthMetricsService         bhmService;

    /**
     * Service for Associations
     */
    @Autowired
    private PatientAdvocateAssociationService associationService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        officeVisitService.deleteAll();

        appointmentRequestService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final Patient antti = buildPatient();

        userService.saveAll( List.of( patient, hcp, antti ) );

        final Hospital hosp = new Hospital();
        hosp.setAddress( "123 Raleigh Road" );
        hosp.setState( State.NC );
        hosp.setZip( "27514" );
        hosp.setName( "iTrust Test Hospital 2" );

        hospitalService.save( hosp );
    }

    private Patient buildPatient () {
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        antti.setAddress1( "1 Test Street" );
        antti.setAddress2( "Some Location" );
        antti.setBloodType( BloodType.APos );
        antti.setCity( "Viipuri" );
        final LocalDate date = LocalDate.of( 1977, 6, 15 );
        antti.setDateOfBirth( date );
        antti.setEmail( "antti@itrust.fi" );
        antti.setEthnicity( Ethnicity.Caucasian );
        antti.setFirstName( "Antti" );
        antti.setGender( Gender.Male );
        antti.setLastName( "Walhelm" );
        antti.setPhone( "123-456-7890" );
        antti.setState( State.NC );
        antti.setZip( "27514" );

        return antti;
    }

    /**
     * Tests getting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     *             exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentOfficeVisit () throws Exception {
        mvc.perform( get( "/api/v1/officevisits/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests getting a list of a Patient's office visits as an advocate that
     * both is associated to that patient and has permission to get the list of
     * office visits
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "advocate1", roles = { "PATIENTADVOCATE" } )
    @Transactional
    public void testGetOfficeVisitsByPatientValid () throws Exception {
        // Get the users made in the setup method
        final User patient = userService.findByName( "patient" );
        final User hcp = userService.findByName( "hcp" );

        // Make an office visit and assign it to the users we just got
        OfficeVisitForm visitForm = new OfficeVisitForm();
        visitForm.setDate( "2030-11-19T04:50:00.000-05:00" );
        visitForm.setHcp( "hcp" );
        visitForm.setPatient( "patient" );
        visitForm.setNotes( "Test 0" );
        visitForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visitForm.setHospital( "iTrust Test Hospital 2" );

        final OfficeVisit visit0 = officeVisitService.build( visitForm );
        officeVisitService.save( visit0 );

        // Make an advocate to be the MockUser
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( "advocate1", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        userService.save( advocate );

        // Associate the advocate to the patient.
        final PatientAdvocateAssociation association = new PatientAdvocateAssociation( patient.getUsername(),
                advocate.getUsername() );
        association.setVisit( true );
        associationService.save( association );

        // Ensure that the endpoint correctly returns the office visit
        String response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( response.contains( "\"notes\":\"Test 0\"" ) );
        Assertions.assertFalse( response.contains( "\"notes\":\"Test 1\"" ) );

        // Make a second office visit and assign it to the users
        visitForm = new OfficeVisitForm();
        visitForm.setDate( "2030-11-19T04:50:00.000-05:00" );
        visitForm.setHcp( "hcp" );
        visitForm.setPatient( "patient" );
        visitForm.setNotes( "Test 1" );
        visitForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visitForm.setHospital( "iTrust Test Hospital 2" );

        final OfficeVisit visit1 = officeVisitService.build( visitForm );
        officeVisitService.save( visit1 );

        // Ensure that the endpoint correctly returns both office visits
        response = mvc.perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( response.contains( "\"notes\":\"Test 0\"" ) );
        Assertions.assertTrue( response.contains( "\"notes\":\"Test 1\"" ) );

        // Ensure that the endpoint correctly updates
        officeVisitService.delete( officeVisitService.findByHcpAndPatient( hcp, patient ).get( 0 ) );
        response = mvc.perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertFalse( response.contains( "\"notes\":\"Test 0\"" ) );
        Assertions.assertTrue( response.contains( "\"notes\":\"Test 1\"" ) );
    }

    /**
     * Tests getting a list of a Patient's office visits as an advocate that
     * either is not associated to that patient or does not have permission to
     * get the list of office visits
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "advocate2", roles = { "PATIENTADVOCATE" } )
    @Transactional
    public void testGetOfficeVisitsByPatientInvalid () throws Exception {
        // Get the users made in the setup method
        final User patient = userService.findByName( "patient" );

        // Make an office visit and assign it to the users we just got
        final OfficeVisitForm visitForm = new OfficeVisitForm();
        visitForm.setDate( "2030-11-19T04:50:00.000-05:00" );
        visitForm.setHcp( "hcp" );
        visitForm.setPatient( "patient" );
        visitForm.setNotes( "Test 0" );
        visitForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visitForm.setHospital( "iTrust Test Hospital 2" );

        final OfficeVisit visit0 = officeVisitService.build( visitForm );
        officeVisitService.save( visit0 );

        // Make an advocate to be the MockUser
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( "advocate2", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        userService.save( advocate );

        // Ensure that the advocate cannot get the office visit we just made
        // He can't do this because he is not associated with the office visit's
        // patient
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isForbidden() );

        final PatientAdvocateAssociation association = new PatientAdvocateAssociation( patient.getUsername(),
                advocate.getUsername() );
        associationService.save( association );

        // Ensure that the advocate cannot get the office visit
        // He can't do this because although he is associated with the office
        // visit's
        // patient, he does not have the permission
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isForbidden() );

        association.setVisit( true );
        associationService.save( association );

        // Ensure that the advocate can get the office visit now that he has
        // permission
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        // Ensure that the endpoint correctly returns a 404 when the patient
        // does not exist
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/officevisits/patient/" + "non-existent" ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );
    }

    /**
     * Tests handling of errors when creating a visit for a pre-scheduled
     * appointment.
     *
     * @throws Exception
     *             exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPreScheduledOfficeVisit () throws Exception {

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();

        // 2030-11-19 4:50 AM EST
        appointmentForm.setDate( "2030-11-19T04:50:00.000-05:00" );

        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.APPROVED.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );

        appointmentRequestService.save( appointmentRequestService.build( appointmentForm ) );

        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setPreScheduled( "yes" );
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        mvc.perform( post( "/api/v1/officevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, officeVisitService.count() );

        officeVisitService.deleteAll();

        Assertions.assertEquals( 0, officeVisitService.count() );

        visit.setDate( "2030-12-19T04:50:00.000-05:00" );
        // setting a pre-scheduled appointment that doesn't match should not
        // work.
        mvc.perform( post( "/api/v1/officevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isBadRequest() );

    }

    /**
     * Tests OfficeVisitAPI
     *
     * @throws Exception
     *             exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testOfficeVisitAPI () throws Exception {

        Assertions.assertEquals( 0, officeVisitService.count() );

        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        /* Create the Office Visit */
        mvc.perform( post( "/api/v1/officevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, officeVisitService.count() );

        mvc.perform( get( "/api/v1/officevisits" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        /* Test getForHCP and getForHCPAndPatient */
        OfficeVisit v = officeVisitService.build( visit );
        List<OfficeVisit> vList = officeVisitService.findByHcp( v.getHcp() );
        Assertions.assertEquals( vList.get( 0 ).getHcp(), v.getHcp() );
        vList = officeVisitService.findByHcpAndPatient( v.getHcp(), v.getPatient() );
        Assertions.assertEquals( vList.get( 0 ).getHcp(), v.getHcp() );
        Assertions.assertEquals( vList.get( 0 ).getPatient(), v.getPatient() );

        visit.setPatient( "antti" );
        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        v = officeVisitService.build( visit );

        /* Test that all fields have been filled successfully */
        Assertions.assertNotNull( v );
        Assertions.assertEquals( "antti", visit.getPatient() );
        Assertions.assertEquals( Integer.valueOf( 83 ), visit.getDiastolic() );
        Assertions.assertEquals( Integer.valueOf( 70 ), visit.getHdl() );
        Assertions.assertEquals( Float.valueOf( 69.1f ), visit.getHeight() );
        Assertions.assertEquals( HouseholdSmokingStatus.INDOOR, visit.getHouseSmokingStatus() );
        Assertions.assertEquals( Integer.valueOf( 30 ), visit.getLdl() );
        Assertions.assertEquals( PatientSmokingStatus.FORMER, visit.getPatientSmokingStatus() );
        Assertions.assertEquals( Integer.valueOf( 102 ), visit.getSystolic() );
        Assertions.assertEquals( Integer.valueOf( 150 ), visit.getTri() );
        Assertions.assertEquals( Float.valueOf( 175.2f ), visit.getWeight() );

        /* Create new BasicHealthMetrics for testing */
        final BasicHealthMetrics bhm1 = bhmService.build( visit );
        final BasicHealthMetrics bhm2 = bhmService.build( visit );
        Assertions.assertTrue( bhm1.equals( bhm1 ) );
        Assertions.assertTrue( bhm1.equals( bhm2 ) );
        Assertions.assertTrue( bhm2.equals( bhm1 ) );

        /* Hash codes are the same for equal objects */
        Assertions.assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        Assertions.assertTrue( bhm1.equals( bhm2 ) );
        Assertions.assertTrue( bhm2.equals( bhm1 ) );

        /* Weights are different */
        bhm2.setWeight( 172.3f );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One weight is null */
        bhm2.setWeight( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setWeight( 175.2f );

        /* Tri is different */
        bhm2.setTri( 140 );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One tri is null */
        bhm2.setTri( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setTri( 150 );

        /* Systolics are different */
        bhm2.setSystolic( 100 );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One systolic is null */
        bhm2.setSystolic( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setSystolic( 102 );

        /* Patient smoking statuses are different */
        bhm2.setPatientSmokingStatus( PatientSmokingStatus.NEVER );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setPatientSmokingStatus( PatientSmokingStatus.FORMER );

        /* One patient is null */
        bhm2.setPatient( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setPatient( userService.findByName( "antti" ) );

        /* LDL's are different */
        bhm2.setLdl( 40 );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One LDL is null */
        bhm2.setLdl( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setLdl( 30 );

        /* Household smoking statuses are different */
        bhm2.setHouseSmokingStatus( HouseholdSmokingStatus.OUTDOOR );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );

        /* Heights are different */
        bhm2.setHeight( 60.2f );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One height is null */
        bhm2.setHeight( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setHeight( 69.1f );

        /* Different head circumferences */
        bhm2.setHeadCircumference( 8.7f );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm1.setHeadCircumference( 8.7f );

        /* HDL's are different */
        bhm2.setHdl( 80 );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One HDL is null */
        bhm2.setHdl( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setHdl( 70 );

        /* Diastolics are different */
        bhm2.setDiastolic( 85 );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );

        /* One diastolic is null */
        bhm2.setDiastolic( null );
        Assertions.assertFalse( bhm1.equals( bhm2 ) );
        Assertions.assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setDiastolic( 83 );
        Assertions.assertTrue( bhm1.equals( bhm2 ) );
        Assertions.assertTrue( bhm2.equals( bhm1 ) );

        /* Create appointment with patient younger than 12 years old */

        final Patient patient2 = buildPatient();
        userService.save( patient2 );
        patient2.setDateOfBirth( LocalDate.of( 2040, 6, 15 ) );
        visit.setPatient( patient2.getUsername() );
        v = officeVisitService.build( visit );
        Assertions.assertNotNull( v );

        /* Create appointment with patient younger than 3 years old */
        final Patient patient3 = buildPatient();
        patient3.setDateOfBirth( LocalDate.of( 2046, 6, 15 ) );
        userService.save( patient3 );
        visit.setHeadCircumference( 20.0f );
        visit.setPatient( patient3.getUsername() );
        v = officeVisitService.build( visit );
        Assertions.assertNotNull( v );

        /*
         * We need the ID of the office visit that actually got _saved_ when
         * calling the API above. This will get it
         */
        final Long id = officeVisitService.findByPatient( userService.findByName( "patient" ) ).get( 0 ).getId();

        visit.setId( id.toString() );

        // Second post should fail with a conflict since it already exists
        mvc.perform( post( "/api/v1/officevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isConflict() );

        mvc.perform( get( "/api/v1/officevisits/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        visit.setDate( "2030-11-19T09:45:00.000-05:00" );

        mvc.perform( put( "/api/v1/officevisits/" + id ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // PUT with ID not in database should fail
        final long tempId = 101;
        visit.setId( "101" );
        mvc.perform( put( "/api/v1/officevisits/" + tempId ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isNotFound() );

    }

}
