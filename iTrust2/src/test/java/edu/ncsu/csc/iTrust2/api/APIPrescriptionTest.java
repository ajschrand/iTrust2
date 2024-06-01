package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.DrugForm;
import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.DrugService;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PrescriptionService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Class for testing prescription API.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIPrescriptionTest {
    @Autowired
    private MockMvc                           mvc;

    private DrugForm                          drugForm;

    @Autowired
    private UserService<User>                 userService;

    /** Prescription service */
    @Autowired
    private PrescriptionService               prescriptionService;

    /**
     * Service for Associations
     */
    @Autowired
    private DrugService                       drugService;

    /**
     * Service for Associations
     */
    @Autowired
    private PatientAdvocateAssociationService associationService;

    /**
     * Performs setup operations for the tests.
     *
     * @throws Exception
     */
    @BeforeEach
    public void setup () throws Exception {
        final UserForm pTestUser = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        final UserForm pTestUser2 = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );

        final Personnel p = new Personnel( pTestUser );
        final Patient p2 = new Patient( pTestUser2 );

        userService.save( p );

        userService.save( p2 );

        // Create drug for testing
        drugForm = new DrugForm();
        drugForm.setCode( "0000-0000-20" );
        drugForm.setName( "TEST" );
        drugForm.setDescription( "DESC" );
        mvc.perform( post( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drugForm ) ) );
    }

    /**
     * Tests getting a list of a Patient's prescriptions as an advocate that
     * both is associated to that patient and has permission to get the list of
     * prescriptions
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "advocate1", roles = { "PATIENTADVOCATE" } )
    @Transactional
    public void testGetPrescriptionsByPatientValid () throws Exception {
        // Get the users made in the setup method
        final User patient = userService.findByName( "patient" );

        // Make a prescription and assign it to the patient we just got
        final Drug drug0 = new Drug( drugForm );
        drugService.save( drug0 );

        final PrescriptionForm form0 = new PrescriptionForm();
        form0.setDrug( drugForm.getCode() );
        form0.setDosage( 100 );
        form0.setRenewals( 12 );
        form0.setPatient( "patient" );
        form0.setStartDate( "2009-10-10" );
        form0.setEndDate( "2010-10-10" );

        final Prescription p0 = prescriptionService.build( form0 );
        prescriptionService.save( p0 );

        // Make an advocate to be the MockUser
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( "advocate1", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        userService.save( advocate );

        // Associate the advocate to the patient.
        final PatientAdvocateAssociation association = new PatientAdvocateAssociation( patient.getUsername(),
                advocate.getUsername() );
        association.setPrescription( true );
        associationService.save( association );

        // Ensure that the endpoint correctly returns the prescription
        String response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( response.contains( "\"renewals\":12" ) );
        Assertions.assertFalse( response.contains( "\"renewals\":34" ) );

        // Make a second prescription and assign it to the users
        final PrescriptionForm form1 = new PrescriptionForm();
        form1.setDrug( drugForm.getCode() );
        form1.setDosage( 100 );
        form1.setRenewals( 34 );
        form1.setPatient( "patient" );
        form1.setStartDate( "2009-10-10" );
        form1.setEndDate( "2010-10-10" );

        final Prescription p1 = prescriptionService.build( form1 );
        prescriptionService.save( p1 );

        // Ensure that the endpoint correctly returns both prescriptions
        response = mvc.perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( response.contains( "\"renewals\":12" ) );
        Assertions.assertTrue( response.contains( "\"renewals\":34" ) );

        // Ensure that the endpoint correctly updates
        prescriptionService.delete( p0 );
        response = mvc.perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertFalse( response.contains( "\"renewals\":12" ) );
        Assertions.assertTrue( response.contains( "\"renewals\":34" ) );
    }

    /**
     * Tests getting a list of a Patient's prescriptions as an advocate that
     * either is not associated to that patient or does not have permission to
     * get the list of prescriptions
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "advocate2", roles = { "PATIENTADVOCATE" } )
    @Transactional
    public void testGetPrescriptionsByPatientInvalid () throws Exception {
        // Get the users made in the setup method
        final User patient = userService.findByName( "patient" );

        // Make a prescription and assign it to the patient we just got
        final Drug drug0 = new Drug( drugForm );
        drugService.save( drug0 );

        final PrescriptionForm form0 = new PrescriptionForm();
        form0.setDrug( drugForm.getCode() );
        form0.setDosage( 100 );
        form0.setRenewals( 12 );
        form0.setPatient( "patient" );
        form0.setStartDate( "2009-10-10" );
        form0.setEndDate( "2010-10-10" );

        final Prescription p0 = prescriptionService.build( form0 );
        prescriptionService.save( p0 );

        // Make an advocate to be the MockUser
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( "advocate2", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        userService.save( advocate );

        // Ensure that the advocate cannot get the prescription we just made
        // He can't do this because he is not associated with the prescription's
        // patient
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isForbidden() );

        final PatientAdvocateAssociation association = new PatientAdvocateAssociation( patient.getUsername(),
                advocate.getUsername() );
        associationService.save( association );

        // Ensure that the advocate cannot get the prescription
        // He can't do this because although he is associated with the
        // prescription's patient, he does not have the permission
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isForbidden() );

        association.setPrescription( true );
        associationService.save( association );

        // Ensure that the advocate can get the prescription now that he has
        // permission
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + patient.getUsername() ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        // Ensure that the endpoint correctly returns a 404 when the patient
        // does not exist
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/prescriptions/patient/" + "non-existent" ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );
    }

    /**
     * Tests basic prescription APIs.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP", "ADMIN" } )
    @Transactional
    public void testPrescriptionAPI () throws Exception {

        // Create two prescription forms for testing
        final PrescriptionForm form1 = new PrescriptionForm();
        form1.setDrug( drugForm.getCode() );
        form1.setDosage( 100 );
        form1.setRenewals( 12 );
        form1.setPatient( "patient" );
        form1.setStartDate( "2009-10-10" ); // 10/10/2009
        form1.setEndDate( "2010-10-10" ); // 10/10/2010

        final PrescriptionForm form2 = new PrescriptionForm();
        form2.setDrug( drugForm.getCode() );
        form2.setDosage( 200 );
        form2.setRenewals( 3 );
        form2.setPatient( "patient" );
        form2.setStartDate( "2020-10-10" ); // 10/10/2020
        form2.setEndDate( "2020-11-10" ); // 11/10/2020

        // Add first prescription to system
        final String content1 = mvc
                .perform( post( "/api/v1/prescriptions" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Prescription
        final Prescription p1 = TestUtils.gson().fromJson( content1, Prescription.class );
        final PrescriptionForm p1Form = new PrescriptionForm( p1 );
        Assertions.assertEquals( form1.getDrug(), p1Form.getDrug() );
        Assertions.assertEquals( form1.getDosage(), p1Form.getDosage() );
        Assertions.assertEquals( form1.getRenewals(), p1Form.getRenewals() );
        Assertions.assertEquals( form1.getPatient(), p1Form.getPatient() );
        Assertions.assertEquals( form1.getStartDate(), p1Form.getStartDate() );
        Assertions.assertEquals( form1.getEndDate(), p1Form.getEndDate() );

        // Add second prescription to system
        final String content2 = mvc
                .perform( post( "/api/v1/prescriptions" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Prescription p2 = TestUtils.gson().fromJson( content2, Prescription.class );
        final PrescriptionForm p2Form = new PrescriptionForm( p2 );
        Assertions.assertEquals( form1.getDrug(), p2Form.getDrug() );
        Assertions.assertNotEquals( form1.getDosage(), p2Form.getDosage() );
        Assertions.assertNotEquals( form1.getRenewals(), p2Form.getRenewals() );
        Assertions.assertEquals( form1.getPatient(), p2Form.getPatient() );
        Assertions.assertNotEquals( form1.getStartDate(), p2Form.getStartDate() );
        Assertions.assertNotEquals( form1.getEndDate(), p2Form.getEndDate() );

        // Verify prescriptions have been added
        final String allPrescriptionContent = mvc.perform( get( "/api/v1/prescriptions" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<Prescription> allPrescriptions = TestUtils.gson().fromJson( allPrescriptionContent,
                new TypeToken<List<Prescription>>() {
                }.getType() );
        Assertions.assertTrue( allPrescriptions.size() >= 2 );

        // Edit first prescription
        p1.setDosage( 500 );
        final String editContent = mvc
                .perform( put( "/api/v1/prescriptions" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new PrescriptionForm( p1 ) ) ) )
                .andReturn().getResponse().getContentAsString();
        final Prescription edited = TestUtils.gson().fromJson( editContent, Prescription.class );
        Assertions.assertEquals( p1.getId(), edited.getId() );
        Assertions.assertEquals( p1.getDosage(), edited.getDosage() );

        // Get single prescription
        final String getContent = mvc
                .perform( put( "/api/v1/prescriptions" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new PrescriptionForm( p1 ) ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Prescription fetched = TestUtils.gson().fromJson( getContent, Prescription.class );
        Assertions.assertEquals( p1.getId(), fetched.getId() );

        // Make sure we can delete one of our objects (the other will be trashed
        // in the transaction rollback)
        mvc.perform( delete( "/api/v1/prescriptions/" + p1.getId() ).with( csrf() ) ).andExpect( status().isOk() )
                .andExpect( content().string( p1.getId().toString() ) );
    }

}
