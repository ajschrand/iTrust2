package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateAssociationForm;
import edu.ncsu.csc.iTrust2.forms.PatientForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.models.enums.VaccinationStatus;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Test for API functionality for interacting with Patients
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIPatientTest {

    /** Used to test the API calls */
    @Autowired
    private MockMvc                           mvc;

    /** Service for interacting with Patient in the database */
    @Autowired
    private PatientService<Patient>           service;

    /**
     * Service for interacting with PatientAdvocateAssociations in the database
     */
    @Autowired
    private PatientAdvocateAssociationService aService;

    /** Service for interacting with Personnel in the database */
    @Autowired
    private PersonnelService                  pService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        service.deleteAll();
        aService.deleteAll();
        pService.deleteAll();
    }

    /**
     * Tests that getting a patient that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentPatient () throws Exception {
        mvc.perform( get( "/api/v1/patients/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PatientAPI
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
    public void testPatientAPI () throws Exception {

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setUsername( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setVaccinationStatus( VaccinationStatus.FIRST_DOSE.toString() );

        // Editing the patient before they exist should fail
        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isNotFound() );

        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        // Creating a User should create the Patient record automatically
        mvc.perform( get( "/api/v1/patients/antti" ) ).andExpect( status().isOk() );

        // Should also now be able to edit existing record with new information
        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        Patient anttiRetrieved = (Patient) service.findByName( "antti" );

        // Test a few fields to be reasonably confident things are working
        Assertions.assertEquals( "Walhelm", anttiRetrieved.getLastName() );
        Assertions.assertEquals( Gender.Male, anttiRetrieved.getGender() );
        Assertions.assertEquals( "Viipuri", anttiRetrieved.getCity() );

        // Also test a field we haven't set yet
        Assertions.assertNull( anttiRetrieved.getPreferredName() );

        patient.setPreferredName( "Antti Walhelm" );

        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        anttiRetrieved = (Patient) service.findByName( "antti" );

        Assertions.assertNotNull( anttiRetrieved.getPreferredName() );

        // Editing with the wrong username should fail.
        mvc.perform( put( "/api/v1/patients/badusername" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().is4xxClientError() );

    }

    /**
     * Test accessing the patient GET request for all Patients as an Admin
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetAllPatients1 () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size() );
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( antti );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        final String list = mvc.perform( get( "/api/v1/patients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( list.contains( "antti" ) );
        Assertions.assertTrue( list.contains( "second" ) );
        Assertions.assertTrue( list.contains( "third" ) );

    }

    /**
     * Test accessing the patient GET request for all Patients as a HCP NOTE:
     * the full list of roles that SHOULD be able to access this endpoint are:
     * ADMIN, HCP, VACCINATOR, BSM, VIROLOGIST, OD, OPH, LABTECH, & ER.
     * Essentially, the only two roles (As of iTrust2 Iteration 2B) that do NOT
     * have access to this endpoint (for security reasons) are: PATIENT &
     * PATIENTADVOCATE
     *
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetAllPatients2 () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size() );
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( antti );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        final String list = mvc.perform( get( "/api/v1/patients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( list.contains( "antti" ) );
        Assertions.assertTrue( list.contains( "second" ) );
        Assertions.assertTrue( list.contains( "third" ) );

    }

    /**
     * Test accessing the patient GET request for all Patients as an
     * unauthorized user type
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testGetAllPatientsForbidden1 () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size() );
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( antti );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        mvc.perform( get( "/api/v1/patients" ) ).andDo( print() ).andExpect( status().isForbidden() ).andReturn()
                .getResponse().getContentAsString();

    }

    /**
     * Test accessing the patient GET request for all Patients as an
     * unauthorized user type
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "patientAdvocate", roles = { "PATIENTADVOCATE" } )
    public void testGetAllPatientsForbidden2 () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size() );
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( antti );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        mvc.perform( get( "/api/v1/patients" ) ).andDo( print() ).andExpect( status().isForbidden() ).andReturn()
                .getResponse().getContentAsString();

    }

    /**
     * Test accessing the patient GET request for all Patients associated with
     * the signed in advocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "patientAdvocate", roles = { "PATIENTADVOCATE" } )
    public void testGetPatientsAsAdvocate () throws Exception {
        // Ensure clean slate
        Assertions.assertEquals( 0, service.findAll().size() );

        // Create Patients
        final Patient first = new Patient( new UserForm( "first", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( first );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        // Create Advocates
        final PatientAdvocate adv = new PatientAdvocate(
                new UserForm( "patientAdvocate", "111111", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv );
        assertEquals( 1, pService.findAll().size() );
        final PatientAdvocate adv2 = new PatientAdvocate(
                new UserForm( "advocate2", "222222", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv2 );
        assertEquals( 2, pService.findAll().size() );

        // Assign Advocate to patients
        final PatientAdvocateAssociationForm paaf1 = new PatientAdvocateAssociationForm();
        paaf1.setPatient( first.getUsername() );
        paaf1.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation paa1 = new PatientAdvocateAssociation( paaf1 );
        aService.save( paa1 );

        first.addAssociation( (Long) paa1.getId() );
        service.save( first );

        adv.addAssociation( (Long) paa1.getId() );
        pService.save( adv );

        final PatientAdvocateAssociationForm paaf2 = new PatientAdvocateAssociationForm();
        paaf2.setPatient( second.getUsername() );
        paaf2.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation paa2 = new PatientAdvocateAssociation( paaf2 );
        aService.save( paa2 );

        second.addAssociation( (Long) paa2.getId() );
        service.save( second );

        adv.addAssociation( (Long) paa2.getId() );
        pService.save( adv );

        // ensure the associations were created properly
        assertEquals( 2, aService.findAll().size() );
        final String list = mvc.perform( get( "/api/v1/patients/advocate" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( list.contains( "first" ) );
        Assertions.assertTrue( list.contains( "second" ) );
        Assertions.assertFalse( list.contains( "third" ) );

    }

    /**
     * Test accessing the patient GET request for all Patients associated with
     * the signed in advocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "advocate2", roles = { "PATIENTADVOCATE" } )
    public void testGetPatientsAsAdvocateEmpty () throws Exception {
        // Ensure clean slate
        Assertions.assertEquals( 0, service.findAll().size() );

        // Create Patients
        final Patient first = new Patient( new UserForm( "first", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( first );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        // Create Advocates
        final PatientAdvocate adv = new PatientAdvocate(
                new UserForm( "patientAdvocate", "111111", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv );
        assertEquals( 1, pService.findAll().size() );
        final PatientAdvocate adv2 = new PatientAdvocate(
                new UserForm( "advocate2", "222222", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv2 );
        assertEquals( 2, pService.findAll().size() );

        // Assign Advocate to patients
        final PatientAdvocateAssociationForm paaf1 = new PatientAdvocateAssociationForm();
        paaf1.setPatient( first.getUsername() );
        paaf1.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation paa1 = new PatientAdvocateAssociation( paaf1 );
        aService.save( paa1 );

        first.addAssociation( (Long) paa1.getId() );
        service.save( first );

        adv.addAssociation( (Long) paa1.getId() );
        pService.save( adv );

        final PatientAdvocateAssociationForm paaf2 = new PatientAdvocateAssociationForm();
        paaf2.setPatient( second.getUsername() );
        paaf2.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation paa2 = new PatientAdvocateAssociation( paaf2 );
        aService.save( paa2 );

        second.addAssociation( (Long) paa2.getId() );
        service.save( second );

        adv.addAssociation( (Long) paa2.getId() );
        pService.save( adv );

        // ensure the associations were created properly
        assertEquals( 2, aService.findAll().size() );

        final String list = mvc.perform( get( "/api/v1/patients/advocate" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertFalse( list.contains( "first" ) );
        Assertions.assertFalse( list.contains( "second" ) );
        Assertions.assertFalse( list.contains( "third" ) );

    }

    /**
     * Test accessing the patient GET request for all Patients associated with
     * the signed in advocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testGetPatientsAsAdvocateNotAdvocate () throws Exception {
        // Ensure clean slate
        Assertions.assertEquals( 0, service.findAll().size() );

        // Create Patients
        final Patient first = new Patient( new UserForm( "first", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( first );
        Assertions.assertEquals( 1, service.findAll().size() );

        final Patient second = new Patient( new UserForm( "second", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( second );
        Assertions.assertEquals( 2, service.findAll().size() );

        final Patient third = new Patient( new UserForm( "third", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( third );
        Assertions.assertEquals( 3, service.findAll().size() );

        // Create Advocates
        final PatientAdvocate adv = new PatientAdvocate(
                new UserForm( "patientAdvocate", "111111", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv );
        assertEquals( 1, pService.findAll().size() );
        final PatientAdvocate adv2 = new PatientAdvocate(
                new UserForm( "advocate2", "222222", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv2 );
        assertEquals( 2, pService.findAll().size() );

        // Assign Advocate to patients
        final PatientAdvocateAssociationForm paaf1 = new PatientAdvocateAssociationForm();
        paaf1.setPatient( first.getUsername() );
        paaf1.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation paa1 = new PatientAdvocateAssociation( paaf1 );
        aService.save( paa1 );

        first.addAssociation( (Long) paa1.getId() );
        service.save( first );

        adv.addAssociation( (Long) paa1.getId() );
        pService.save( adv );

        final PatientAdvocateAssociationForm paaf2 = new PatientAdvocateAssociationForm();
        paaf2.setPatient( second.getUsername() );
        paaf2.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation paa2 = new PatientAdvocateAssociation( paaf2 );
        aService.save( paa2 );

        second.addAssociation( (Long) paa2.getId() );
        service.save( second );

        adv.addAssociation( (Long) paa2.getId() );
        pService.save( adv );

        // ensure the associations were created properly
        assertEquals( 2, aService.findAll().size() );

        mvc.perform( get( "/api/v1/patients/advocate" ) ).andDo( print() ).andExpect( status().isForbidden() )
                .andReturn().getResponse().getContentAsString();

    }

    /**
     * Test accessing the patient PUT request unauthenticated
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testPatientUnauthenticated () throws Exception {
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setUsername( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setVaccinationStatus( VaccinationStatus.FIRST_DOSE.toString() );

        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isForbidden() );
    }

    /**
     * Test accessing the patient PUT request as a patient
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    public void testPatientAsPatient () throws Exception {
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setUsername( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setVaccinationStatus( VaccinationStatus.FULLY_VACCINATED.toString() );

        // a patient can edit their own info
        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        final Patient anttiRetrieved = (Patient) service.findByName( "antti" );

        // Test a few fields to be reasonably confident things are working
        Assertions.assertEquals( "Walhelm", anttiRetrieved.getLastName() );
        Assertions.assertEquals( Gender.Male, anttiRetrieved.getGender() );
        Assertions.assertEquals( "Viipuri", anttiRetrieved.getCity() );

        // Also test a field we haven't set yet
        Assertions.assertNull( anttiRetrieved.getPreferredName() );

        // but they can't edit someone else's
        patient.setUsername( "patient" );
        mvc.perform( put( "/api/v1/patients/patient" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );

        // we should be able to update our record too
        mvc.perform( get( "/api/v1/patient/" ) ).andExpect( status().isOk() );
    }

    /**
     * Test accessing the patient DELETE request as a patient
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testDeletePatient () throws Exception {
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        assertEquals( service.findAll().size(), 1 );

        mvc.perform( delete( "/api/v1/patients/" + antti.getId() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( ( antti.getId() ) ) ).andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 0 );

    }

    /**
     * Test accessing the patient DELETE request with invalid Patient
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testDeletePatientInvalid () throws Exception {
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        assertEquals( service.findAll().size(), 1 );

        mvc.perform( delete( "/api/v1/patients/anti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( ( "anti" ) ) ).andExpect( status().isNotFound() );

        assertEquals( service.findAll().size(), 1 );

    }

    /**
     * Test accessing the patient DELETE request as a patient
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testDeletePatientWithAssociation () throws Exception {
        Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );
        service.save( antti );
        assertEquals( service.findAll().size(), 1 );

        PatientAdvocate adv = new PatientAdvocate( new UserForm( "yukino", "pw", Role.ROLE_PATIENTADVOCATE, 1 ) );
        pService.save( adv );
        assertEquals( service.findAll().size(), 1 );

        final PatientAdvocateAssociationForm a = new PatientAdvocateAssociationForm();
        a.setPatient( antti.getUsername() );
        a.setPatientAdvocate( adv.getUsername() );

        final PatientAdvocateAssociation f = new PatientAdvocateAssociation( a );
        aService.save( f );

        antti.addAssociation( (Long) f.getId() );
        service.save( antti );

        adv.addAssociation( (Long) f.getId() );
        pService.save( adv );

        assertEquals( aService.findAll().size(), 1 );

        adv = (PatientAdvocate) pService.findById( adv.getUsername() );
        assertEquals( adv.getAssociations().size(), 1 );

        antti = service.findById( antti.getUsername() );
        assertEquals( antti.getAssociations().size(), 1 );

        mvc.perform( delete( "/api/v1/patients/" + antti.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( antti.getId() ) ) )
                .andExpect( status().isOk() );

        adv = (PatientAdvocate) pService.findById( adv.getUsername() );
        assertEquals( adv.getAssociations().size(), 0 );

        assertEquals( pService.findAll().size(), 1 );

        assertEquals( aService.findAll().size(), 0 );

    }

}
