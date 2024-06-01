package edu.ncsu.csc.iTrust2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateAssociationForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Test for API functionality for interacting with Patient Advocate Associations
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIPatientAdvocateAssociationTest {

    /** Used to test the API calls */
    @Autowired
    private MockMvc                           mvc;

    /**
     * Service for interacting with PatientAdvocateAssociations in the database
     */
    @Autowired
    private PatientAdvocateAssociationService associationService;

    /** Service for interacting with Personnel in the database */
    @Autowired
    private PersonnelService                  personnelService;

    /** Service for interacting with Patient in the database */
    @Autowired
    private PatientService<Patient>           patientService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {
        associationService.deleteAll();
        personnelService.deleteAll();
        patientService.deleteAll();
    }

    /**
     * Tests creating an assoication between a Patient and PatientAdvocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreateAssociation () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );

        final UserForm u = new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 );
        final UserForm u2 = new UserForm( "Yuki", "patient", Role.ROLE_PATIENTADVOCATE, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final PatientAdvocateAssociationForm association = new PatientAdvocateAssociationForm();
        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u2.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        final PatientAdvocate adv = (PatientAdvocate) personnelService.findById( "Yuki" );
        final Patient pat = patientService.findById( "Nico" );

        assertEquals( associationService.findAll().size(), 1 );
        assertEquals( adv.getAssociations().get( 0 ), associationService.getAssociation( "Nico", "Yuki" ).getId() );
        assertEquals( pat.getAssociations().get( 0 ), associationService.getAssociation( "Nico", "Yuki" ).getId() );

    }

    /**
     * Tests creating invalid assoication between a Patient and PatientAdvocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreateInvalidAssociation () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );

        final UserForm u = new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 );
        final UserForm u2 = new UserForm( "Yuki", "patient", Role.ROLE_PATIENTADVOCATE, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final PatientAdvocateAssociationForm association = new PatientAdvocateAssociationForm();
        // Should fail with null values for patient username and advocate
        // username
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().isFailedDependency() );

        association.setPatient( u.getUsername() );

        // Should fail even with only advocate having null value
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().isFailedDependency() );

        association.setPatient( "NotReal" );
        association.setPatientAdvocate( "NotReal" );

        // Should fail if Patient or Advocate aren't actually in the system
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().isFailedDependency() );

        association.setPatient( u.getUsername() );

        // Should fail with only one of them being in the system
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().isFailedDependency() );

        association.setPatient( "NotReal" );
        association.setPatientAdvocate( u2.getUsername() );

        // Should fail with only one of them being in the system
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().isFailedDependency() );

    }

    /**
     * Tests deleting an association and making sure they were deleted from the
     * the list of association id's in a Patient and PatientAdvocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testDeleteAssociation () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );
        // Patient pat = new Patient();
        // pat.setUsername( "Nico" );
        final UserForm u = new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 );
        // final PatientAdvocate adv = new PatientAdvocate();
        // adv.setUsername( "Yuki" );
        final UserForm u2 = new UserForm( "Yuki", "patient", Role.ROLE_PATIENTADVOCATE, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        // perService.save( adv );
        // patService.save( pat );

        final PatientAdvocateAssociationForm a = new PatientAdvocateAssociationForm();
        a.setPatient( u.getUsername() );
        a.setPatientAdvocate( u2.getUsername() );
        a.setVisit( true );

        // final String j = TestUtils.asJsonString( association );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( a ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        assertEquals( associationService.findAll().size(), 1 );

        PatientAdvocate adv = (PatientAdvocate) personnelService.findById( "Yuki" );
        Patient pat = patientService.findById( "Nico" );

        assertEquals( adv.getAssociations().get( 0 ), associationService.getAssociation( "Nico", "Yuki" ).getId() );
        assertEquals( pat.getAssociations().get( 0 ), associationService.getAssociation( "Nico", "Yuki" ).getId() );

        final PatientAdvocateAssociation asc = associationService.getAssociation( pat.getUsername(),
                adv.getUsername() );

        mvc.perform(
                MockMvcRequestBuilders.delete( "/api/v1/patientAdvocateAssociations/" + asc.getId() ).with( csrf() )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( asc.getId() ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        assertEquals( associationService.findAll().size(), 0 );

        adv = (PatientAdvocate) personnelService.findById( "Yuki" );
        pat = patientService.findById( "Nico" );

        assertEquals( adv.getAssociations().size(), 0 );
        assertEquals( pat.getAssociations().size(), 0 );

        assertEquals( null, associationService.getAssociation( pat.getUsername(), adv.getUsername() ) );

        // Delete an association that doesn't exist

        mvc.perform(
                MockMvcRequestBuilders.delete( "/api/v1/patientAdvocateAssociations/" + asc.getId() ).with( csrf() )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( asc.getId() ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

    }

    /**
     * Tests creating an association and retrieving it to make sure the
     * information was retrieved properly
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetAssociation () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );
        final Patient pat = new Patient( new UserForm( "Nico", "Password", Role.ROLE_PATIENT, 1 ) );
        // pat.setUsername( "Nico" );
        final PatientAdvocate adv = new PatientAdvocate(
                new UserForm( "Yuki", "Password", Role.ROLE_PATIENTADVOCATE, 1 ) );
        // adv.setUsername( "Yuki" );
        final PatientAdvocateAssociationForm a = new PatientAdvocateAssociationForm();
        a.setPatient( pat.getUsername() );
        a.setPatientAdvocate( adv.getUsername() );
        a.setBilling( false );
        a.setVisit( true );
        a.setPrescription( false );
        personnelService.save( adv );
        patientService.save( pat );

        assertEquals( personnelService.findAll().size(), 1 );
        assertEquals( personnelService.findByName( adv.getUsername() ), adv );
        assertEquals( patientService.findAll().size(), 1 );
        assertEquals( patientService.findByName( pat.getUsername() ), pat );

        // service.save( new PatientAdvocateAssociation( a ) );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations/" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( a ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        assertEquals( associationService.findAll().size(), 1 );

        final PatientAdvocateAssociation asc = associationService.getAssociation( pat.getUsername(),
                adv.getUsername() );

        final String a2 = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/" + asc.getId() )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( a2.contains( pat.getUsername() ) );
        assertTrue( a2.contains( adv.getUsername() ) );
        assertTrue( a2.contains( "\"visit\":true" ) );

        // Get an association that doesn't exist
        // pat.setUsername( "You" );
        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/12334" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isNotFound() );
    }

    /**
     * Tests creating an association and editing the privileges.
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testEditAssociation () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );
        final Patient pat = new Patient( new UserForm( "Nico", "Password", Role.ROLE_PATIENT, 1 ) );
        // pat.setUsername( "Nico" );
        final PatientAdvocate adv = new PatientAdvocate(
                new UserForm( "Yuki", "Password", Role.ROLE_PATIENTADVOCATE, 1 ) );
        // adv.setUsername( "Yuki" );
        final PatientAdvocateAssociationForm association = new PatientAdvocateAssociationForm();
        personnelService.save( adv );
        patientService.save( pat );

        association.setPatient( pat.getUsername() );
        association.setPatientAdvocate( adv.getUsername() );
        association.setVisit( true );
        // service.save( new PatientAdvocateAssociation( association ) );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations/" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        assertEquals( associationService.findAll().size(), 1 );
        PatientAdvocateAssociation a = associationService.getAssociation( pat.getUsername(), adv.getUsername() );
        assertTrue( a.getVisit() );

        association.setVisit( false );
        mvc.perform( MockMvcRequestBuilders.put( "/api/v1/patientAdvocateAssociations/" + a.getId() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        assertEquals( associationService.findAll().size(), 1 );
        a = associationService.getAssociation( pat.getUsername(), adv.getUsername() );
        assertNotNull( a );
        a = associationService.getAssociation( pat.getUsername(), adv.getUsername() );
        assertFalse( a.getVisit() );

    }

    /**
     * Tests getting all associations
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetAllAssociations () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );

        final UserForm u = new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 );
        final UserForm u2 = new UserForm( "Yuki", "patient", Role.ROLE_PATIENTADVOCATE, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final PatientAdvocateAssociationForm association = new PatientAdvocateAssociationForm();
        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u2.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        final PatientAdvocate u3 = new PatientAdvocate(
                new UserForm( "Yukino", "patient", Role.ROLE_PATIENTADVOCATE, 1 ) );
        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u3.getUsername() );
        association.setVisit( false );
        association.setBilling( true );

        personnelService.save( u3 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        final String associations = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/" ).with( csrf() )
                        .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Yuki\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Yukino\"" ) );

    }

    /**
     * Tests getting all associations for a certain patient
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetAllAssociationsForPatient () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );

        final Patient u = new Patient( new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate u2 = new PatientAdvocate(
                new UserForm( "Yuki", "patient", Role.ROLE_PATIENTADVOCATE, 1 ) );
        final Patient u3 = new Patient( new UserForm( "Arthur", "patient", Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate u4 = new PatientAdvocate(
                new UserForm( "Merlin", "patient", Role.ROLE_PATIENTADVOCATE, 1 ) );
        patientService.save( u );
        patientService.save( u3 );
        personnelService.save( u2 );
        personnelService.save( u4 );

        final PatientAdvocateAssociationForm association = new PatientAdvocateAssociationForm();
        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u2.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u4.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        association.setPatient( u3.getUsername() );
        association.setPatientAdvocate( u4.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        String associations = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/patient/" + u.getUsername() )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Yuki\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Merlin\"" ) );
        Assertions.assertFalse( associations.contains( "\"patient\":\"Arthur\"" ) );

        associations = mvc
                .perform(
                        MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/patient/" + u3.getUsername() )
                                .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertFalse( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertFalse( associations.contains( "\"patientAdvocate\":\"Yuki\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Merlin\"" ) );
        Assertions.assertTrue( associations.contains( "\"patient\":\"Arthur\"" ) );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/patient/" + "doesn't exist" )
                .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );
    }

    /**
     * Tests getting all associations for a certain PatientAdvocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testGetAllAssociationsForPatientAdvocate () throws Exception {
        assertEquals( associationService.findAll().size(), 0 );

        final Patient u = new Patient( new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate u2 = new PatientAdvocate(
                new UserForm( "Yuki", "patient", Role.ROLE_PATIENTADVOCATE, 1 ) );
        final Patient u3 = new Patient( new UserForm( "Arthur", "patient", Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate u4 = new PatientAdvocate(
                new UserForm( "Merlin", "patient", Role.ROLE_PATIENTADVOCATE, 1 ) );

        patientService.save( u );
        patientService.save( u3 );

        personnelService.save( u2 );
        personnelService.save( u4 );

        final PatientAdvocateAssociationForm association = new PatientAdvocateAssociationForm();
        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u2.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        association.setPatient( u.getUsername() );
        association.setPatientAdvocate( u4.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        association.setPatient( u3.getUsername() );
        association.setPatientAdvocate( u4.getUsername() );
        association.setVisit( true );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/patientAdvocateAssociations" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( association ) ) )
                .andExpect( MockMvcResultMatchers.status().is2xxSuccessful() );

        String associations = mvc
                .perform( MockMvcRequestBuilders
                        .get( "/api/v1/patientAdvocateAssociations/patientAdvocate/" + u2.getUsername() ).with( csrf() )
                        .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Yuki\"" ) );
        Assertions.assertFalse( associations.contains( "\"patientAdvocate\":\"Merlin\"" ) );
        Assertions.assertFalse( associations.contains( "\"patient\":\"Arthur\"" ) );

        associations = mvc
                .perform( MockMvcRequestBuilders
                        .get( "/api/v1/patientAdvocateAssociations/patientAdvocate/" + u4.getUsername() ).with( csrf() )
                        .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertFalse( associations.contains( "\"patientAdvocate\":\"Yuki\"" ) );
        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"Merlin\"" ) );
        Assertions.assertTrue( associations.contains( "\"patient\":\"Arthur\"" ) );

        mvc.perform(
                MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/patientAdvocate/" + "doesn't exist" )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

    }

    /**
     * Tests getting all associations for a certain PatientAdvocate
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "advocate", roles = { "PATIENTADVOCATE" } )
    public void testGetAllAssociationsForCurrentAdvocate () throws Exception {
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( "advocate", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );

        final Patient p0 = new Patient( new UserForm( "Nico", "patient", Role.ROLE_PATIENT, 1 ) );
        final Patient p1 = new Patient( new UserForm( "Arthur", "patient", Role.ROLE_PATIENT, 1 ) );

        personnelService.save( advocate );

        patientService.save( p0 );
        patientService.save( p1 );

        String associations = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/currentAdvocate/" )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertFalse( associations.contains( "\"patientAdvocate\":\"advocate\"" ) );
        Assertions.assertFalse( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertFalse( associations.contains( "\"patient\":\"Arthur\"" ) );

        final PatientAdvocateAssociation association0 = new PatientAdvocateAssociation( p0.getId(), advocate.getId() );
        associationService.save( association0 );

        associations = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/currentAdvocate/" )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"advocate\"" ) );
        Assertions.assertTrue( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertFalse( associations.contains( "\"patient\":\"Arthur\"" ) );

        final PatientAdvocateAssociation association1 = new PatientAdvocateAssociation( p1.getId(), advocate.getId() );
        associationService.save( association1 );

        associations = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/currentAdvocate/" )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( associations.contains( "\"patientAdvocate\":\"advocate\"" ) );
        Assertions.assertTrue( associations.contains( "\"patient\":\"Nico\"" ) );
        Assertions.assertTrue( associations.contains( "\"patient\":\"Arthur\"" ) );

        mvc.perform(
                MockMvcRequestBuilders.get( "/api/v1/patientAdvocateAssociations/patientAdvocate/" + "doesn't exist" )
                        .with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

    }
}
