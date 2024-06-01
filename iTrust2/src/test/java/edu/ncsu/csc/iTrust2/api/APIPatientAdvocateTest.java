package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateAssociationForm;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateForm;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateUserForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
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
public class APIPatientAdvocateTest {

    /** Used to test the API calls */
    @Autowired
    private MockMvc                           mvc;

    /** Service for interacting with Personnel in the database */
    @Autowired
    private PersonnelService                  service;

    /** Service for interacting with Patient in the database */
    @Autowired
    private PatientService<Patient>           pService;

    /**
     * Service for interacting with PatientAdvocateAssociations in the database
     */
    @Autowired
    private PatientAdvocateAssociationService aService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        service.deleteAll();
        pService.deleteAll();
        aService.deleteAll();
    }

    /**
     * Tests creating a new Patient Advocate on the Manage screen.
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testCreatePatientAdvocate () throws Exception {

        assertEquals( service.findAll().size(), 0 );

        final PatientAdvocateUserForm patientAdvocate = new PatientAdvocateUserForm();
        patientAdvocate.setAddress1( "1 Test Street" );
        patientAdvocate.setAddress2( "Some Location" );
        patientAdvocate.setCity( "Viipuri" );
        patientAdvocate.setEmail( "antti@itrust.fi" );
        patientAdvocate.setFirstName( "Antti" );
        patientAdvocate.setLastName( "Walhelm" );
        patientAdvocate.setPhone( "123-456-7890" );
        patientAdvocate.setUsername( "antti" );
        patientAdvocate.setState( State.NC.toString() );
        patientAdvocate.setZip( "27514" );
        patientAdvocate.setMiddleName( "Nokiyu" );
        patientAdvocate.setNickname( "Yukino" );
        patientAdvocate.setPassword( "password" );
        patientAdvocate.setEnabled( "yes" );
        final Set<String> roles = new HashSet<String>();
        roles.add( Role.ROLE_PATIENTADVOCATE.toString() );
        patientAdvocate.setRoles( roles );

        mvc.perform( post( "/api/v1/patientAdvocates" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientAdvocate ) ) ).andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 1 );

        final PatientAdvocate a = (PatientAdvocate) service.findById( patientAdvocate.getUsername() );

        Assertions.assertNotNull( a, "The created user should be retrievable from the database" );

        Assertions.assertEquals( PatientAdvocate.class, a.getClass() );

        assertEquals( a.getAddress2(), patientAdvocate.getAddress2() );
        assertEquals( a.getNickName(), patientAdvocate.getNickname() );

        Assertions.assertEquals( PatientAdvocate.class, a.getClass(),
                "The retrieved user should be a Patient Advocate" );

        Assertions.assertEquals( 1, a.getRoles().size(), "The retrieved user should have 1 role" );

    }

    /**
     * Tests creating an invalid Patient Advocate on the Manage screen.
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testCreateInvalidPatientAdvocate () throws Exception {

        assertEquals( service.findAll().size(), 0 );

        final PatientAdvocateUserForm patientAdvocate = new PatientAdvocateUserForm();
        patientAdvocate.setAddress1( "1 Test Street" );
        patientAdvocate.setAddress2( "Some Location" );
        patientAdvocate.setCity( "Viipuri" );
        patientAdvocate.setEmail( "antti@itrust.fi" );
        patientAdvocate.setFirstName( "Antti" );
        patientAdvocate.setLastName( "Walhelm" );
        patientAdvocate.setPhone( "123-456-7890" );
        patientAdvocate.setUsername( "antti" );
        patientAdvocate.setState( State.NC.toString() );
        patientAdvocate.setZip( "27514" );
        patientAdvocate.setMiddleName( "Nokiyu" );
        patientAdvocate.setNickname( "Yukino" );
        patientAdvocate.setPassword( "password" );
        patientAdvocate.setEnabled( "yes" );
        final Set<String> roles = new HashSet<String>();
        roles.add( Role.ROLE_PATIENTADVOCATE.toString() );
        patientAdvocate.setRoles( roles );
        // Puts an advocate in the system
        mvc.perform( post( "/api/v1/patientAdvocates" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientAdvocate ) ) ).andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 1 );

        // Try to create a PatientAdvocate with the same name
        mvc.perform( post( "/api/v1/patientAdvocates" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientAdvocate ) ) ).andExpect( status().isConflict() );

        assertEquals( service.findAll().size(), 1 );

        // Try to create a PatientAdvocate with multiple roles
        patientAdvocate.setUsername( "Test" );
        roles.add( Role.ROLE_HCP.toString() );
        patientAdvocate.setRoles( roles );

        mvc.perform( post( "/api/v1/patientAdvocates" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientAdvocate ) ) ).andExpect( status().isBadRequest() );

        assertEquals( service.findAll().size(), 1 );

    }

    /**
     * Tests creating a new Patient Advocate on the Manage screen.
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testEditPatientAdvocateAdmin () throws Exception {
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        final PatientAdvocateUserForm patientAdvocate = new PatientAdvocateUserForm();
        patientAdvocate.setAddress1( "1 Test Street" );
        patientAdvocate.setAddress2( "Some Location" );
        patientAdvocate.setCity( "Viipuri" );
        patientAdvocate.setEmail( "antti@itrust.fi" );
        patientAdvocate.setFirstName( "Antti" );
        patientAdvocate.setLastName( "Walhelm" );
        patientAdvocate.setPhone( "123-456-7890" );
        patientAdvocate.setUsername( "antti" );
        patientAdvocate.setState( State.NC.toString() );
        patientAdvocate.setZip( "27514" );
        patientAdvocate.setMiddleName( "Nokiyu" );
        patientAdvocate.setNickname( "Yukino" );
        patientAdvocate.setPassword( "password" );
        patientAdvocate.setEnabled( "yes" );
        final Set<String> roles = new HashSet<String>();
        roles.add( Role.ROLE_PATIENTADVOCATE.toString() );
        patientAdvocate.setRoles( roles );

        mvc.perform( post( "/api/v1/patientAdvocates" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientAdvocate ) ) ).andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 1 );

        PatientAdvocate a = (PatientAdvocate) service.findById( patientAdvocate.getUsername() );
        assertEquals( a.getLastName(), patientAdvocate.getLastName() );
        assertEquals( true, pe.matches( patientAdvocate.getPassword(), a.getPassword() ) );

        patientAdvocate.setLastName( "Gahama" );
        patientAdvocate.setPassword( "password" );

        mvc.perform( put( "/api/v1/patientAdvocates/" + patientAdvocate.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patientAdvocate ) ) )
                .andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 1 );

        a = (PatientAdvocate) service.findById( patientAdvocate.getUsername() );
        assertNotNull( a );
        assertEquals( a.getLastName(), patientAdvocate.getLastName() );
        assertEquals( true, pe.matches( patientAdvocate.getPassword(), a.getPassword() ) );
    }

    /**
     * Tests editing a new Patient Advocate on the Manage screen INVALID.
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testEditInvalidPatientAdvocateAdmin () throws Exception {
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        final PatientAdvocateUserForm patientAdvocate = new PatientAdvocateUserForm();
        patientAdvocate.setAddress1( "1 Test Street" );
        patientAdvocate.setAddress2( "Some Location" );
        patientAdvocate.setCity( "Viipuri" );
        patientAdvocate.setEmail( "antti@itrust.fi" );
        patientAdvocate.setFirstName( "Antti" );
        patientAdvocate.setLastName( "Walhelm" );
        patientAdvocate.setPhone( "123-456-7890" );
        patientAdvocate.setUsername( "antti" );
        patientAdvocate.setState( State.NC.toString() );
        patientAdvocate.setZip( "27514" );
        patientAdvocate.setMiddleName( "Nokiyu" );
        patientAdvocate.setNickname( "Yukino" );
        patientAdvocate.setPassword( "password" );
        patientAdvocate.setEnabled( "yes" );
        final Set<String> roles = new HashSet<String>();
        roles.add( Role.ROLE_PATIENTADVOCATE.toString() );
        patientAdvocate.setRoles( roles );

        mvc.perform( post( "/api/v1/patientAdvocates" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientAdvocate ) ) ).andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 1 );

        // Try to edit a PatientAdvocate that isn't in the system
        patientAdvocate.setUsername( "Test" );
        patientAdvocate.setLastName( "Test1" );
        mvc.perform( put( "/api/v1/patientAdvocates/" + patientAdvocate.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patientAdvocate ) ) )
                .andExpect( status().isConflict() );

        assertEquals( service.findAll().size(), 1 );

        final PatientAdvocate advocate = (PatientAdvocate) service.findById( "antti" );
        assertEquals( advocate.getLastName(), "Walhelm" );

        assertEquals( true, pe.matches( "password", advocate.getPassword() ) );

    }

    /**
     * Tests a patient advocate editing their own demographics
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "PATIENTADVOCATE" } )
    public void testEditPatientAdvocateDemographics () throws Exception {
        // setup initial PatientAdvocate
        final PatientAdvocate adv = new PatientAdvocate( new UserForm( "antti", "pw", Role.ROLE_PATIENTADVOCATE, 1 ) );
        service.save( adv );
        assertEquals( service.findAll().size(), 1 );

        PatientAdvocate a = (PatientAdvocate) service.findById( adv.getUsername() );
        assertEquals( a.getLastName(), adv.getLastName() );
        assertEquals( a.getMiddleName(), adv.getMiddleName() );

        // Create the demographics update form
        final PatientAdvocateForm patientAdvocateDemo = new PatientAdvocateForm();
        patientAdvocateDemo.setAddress1( "1 Test Street" );
        patientAdvocateDemo.setAddress2( "Some Location" );
        patientAdvocateDemo.setCity( "Viipuri" );
        patientAdvocateDemo.setEmail( "antti@itrust.fi" );
        patientAdvocateDemo.setFirstName( "Antti" );
        patientAdvocateDemo.setLastName( "Gahama" );
        patientAdvocateDemo.setPhone( "123-456-7890" );
        patientAdvocateDemo.setUsername( "antti" );
        patientAdvocateDemo.setState( State.NC.toString() );
        patientAdvocateDemo.setZip( "27514" );
        patientAdvocateDemo.setMiddleName( "Chris" );
        patientAdvocateDemo.setNickname( "Yukino" );

        mvc.perform( put( "/api/v1/patientAdvocates/demographics" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patientAdvocateDemo ) ) )
                .andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 1 );

        a = (PatientAdvocate) service.findById( adv.getUsername() );
        assertNotNull( a );
        assertEquals( a.getLastName(), patientAdvocateDemo.getLastName() );
        assertEquals( a.getMiddleName(), patientAdvocateDemo.getMiddleName() );
        assertEquals( a.getPassword(), adv.getPassword() );

    }

    /**
     * Tests a patient advocate editing their own demographics, invalid cases
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "PATIENTADVOCATE" } )
    public void testEditPatientAdvocateDemographicsInvalid () throws Exception {

        // Create the demographics update form
        final PatientAdvocateForm patientAdvocateDemo = new PatientAdvocateForm();
        patientAdvocateDemo.setAddress1( "1 Test Street" );
        patientAdvocateDemo.setAddress2( "Some Location" );
        patientAdvocateDemo.setCity( "Viipuri" );
        patientAdvocateDemo.setEmail( "antti@itrust.fi" );
        patientAdvocateDemo.setFirstName( "Antti" );
        patientAdvocateDemo.setLastName( "Gahama" );
        patientAdvocateDemo.setPhone( "123-456-7890" );
        patientAdvocateDemo.setUsername( "antti" );
        patientAdvocateDemo.setState( State.NC.toString() );
        patientAdvocateDemo.setZip( "27514" );
        patientAdvocateDemo.setMiddleName( "Chris" );
        patientAdvocateDemo.setNickname( "Yukino" );

        // attempt to perform the PUT with no advocate in the repository
        mvc.perform( put( "/api/v1/patientAdvocates/demographics" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patientAdvocateDemo ) ) )
                .andExpect( status().isNotFound() );

        // setup initial PatientAdvocate
        final PatientAdvocate adv = new PatientAdvocate( new UserForm( "antti", "pw", Role.ROLE_PATIENTADVOCATE, 1 ) );
        service.save( adv );
        assertEquals( service.findAll().size(), 1 );

        final PatientAdvocate a = (PatientAdvocate) service.findById( adv.getUsername() );
        assertEquals( a.getLastName(), adv.getLastName() );
        assertEquals( a.getMiddleName(), adv.getMiddleName() );

        // remove it from the repository
        service.delete( adv );

        // attempt to perform the PUT with a deleted advocate
        mvc.perform( put( "/api/v1/patientAdvocates/demographics" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patientAdvocateDemo ) ) )
                .andExpect( status().isNotFound() );

        // Re-save the advocate to the repository
        service.save( adv );

        // Set the username in the form to NOT match the current user
        patientAdvocateDemo.setUsername( "NOTAntti" );

        // attempt to perform the PUT with an invalid demographics form
        mvc.perform( put( "/api/v1/patientAdvocates/demographics" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patientAdvocateDemo ) ) )
                .andExpect( status().isConflict() );

    }

    /**
     * Test accessing the PatientAdvocate DELETE request
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testDeletePatientAdvocate () throws Exception {
        final PatientAdvocate antti = new PatientAdvocate(
                new UserForm( "antti", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );

        service.save( antti );

        assertEquals( service.findAll().size(), 1 );

        mvc.perform( delete( "/api/v1/patientAdvocates/" + antti.getId() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( ( antti.getId() ) ) ).andExpect( status().isOk() );

        assertEquals( service.findAll().size(), 0 );

    }

    /**
     * Test accessing the PatientAdvocate DELETE request for invalid delete
     *
     * @throws Exception
     *             if problem with service saves, finds, and deletes.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "ADMIN" } )
    public void testDeleteInvalidPatientAdvocate () throws Exception {
        final PatientAdvocate antti = new PatientAdvocate(
                new UserForm( "antti", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );

        service.save( antti );

        assertEquals( service.findAll().size(), 1 );

        mvc.perform( delete( "/api/v1/patientAdvocates/anti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
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
    public void testDeletePatientAdvocateWithAssociation () throws Exception {
        PatientAdvocate antti = new PatientAdvocate( new UserForm( "antti", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        service.save( antti );
        assertEquals( service.findAll().size(), 1 );

        Patient p = new Patient( new UserForm( "yukino", "pw", Role.ROLE_PATIENT, 1 ) );
        pService.save( p );
        assertEquals( pService.findAll().size(), 1 );

        final PatientAdvocateAssociationForm a = new PatientAdvocateAssociationForm();
        a.setPatient( p.getUsername() );
        a.setPatientAdvocate( antti.getUsername() );
        final PatientAdvocateAssociation f = new PatientAdvocateAssociation( a );
        aService.save( f );

        antti.addAssociation( (Long) f.getId() );
        service.save( antti );

        p.addAssociation( (Long) f.getId() );
        pService.save( p );

        assertEquals( aService.findAll().size(), 1 );

        p = pService.findById( p.getUsername() );
        assertEquals( p.getAssociations().size(), 1 );

        antti = (PatientAdvocate) service.findById( antti.getUsername() );
        assertEquals( antti.getAssociations().size(), 1 );

        mvc.perform( delete( "/api/v1/patientAdvocates/" + antti.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( antti.getId() ) ) )
                .andExpect( status().isOk() );

        p = pService.findById( p.getUsername() );
        assertEquals( p.getAssociations().size(), 0 );

        assertEquals( service.findAll().size(), 0 );

        assertEquals( aService.findAll().size(), 0 );

    }

}
