package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
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
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.forms.VaccineVisitForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineAppointmentRequest;
import edu.ncsu.csc.iTrust2.models.VaccineType;
import edu.ncsu.csc.iTrust2.models.VaccineVisit;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.VaccineAppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.VaccineTypeService;
import edu.ncsu.csc.iTrust2.services.VaccineVisitService;

/**
 * Tests the API for vaccine visit
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIVaccineVisitTest {

    @Autowired
    private VaccineVisitService              service;

    @Autowired
    private MockMvc                          mvc;

    /** User service */
    @Autowired
    private UserService<User>                userService;

    @Autowired
    private VaccineAppointmentRequestService reqService;

    @Autowired
    private VaccineTypeService               vaccineService;

    @Autowired
    private VaccineVisitService              vvService;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        userService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_VACCINATOR, 1 ) );

        final VaccineType vaccine = new VaccineType();
        vaccine.setName( "Moderna" );
        vaccine.setNumDoses( 1 );
        vaccine.setIsAvailable( true );

        userService.saveAll( List.of( patient, hcp ) );
        vaccineService.save( vaccine );

        final VaccineAppointmentRequest app = new VaccineAppointmentRequest();

        app.setDate( ZonedDateTime.parse( "2030-11-19T04:50:00.000-05:00" ) );
        app.setType( AppointmentType.GENERAL_CHECKUP );
        app.setStatus( Status.APPROVED );
        app.setHcp( userService.findByName( "hcp" ) );
        app.setPatient( userService.findByName( "patient" ) );
        app.setComments( "Test appointment please ignore" );
        app.setVaccine( vaccineService.findByName( "Moderna" ) );

        reqService.save( app );

    }

    /**
     * Tests that getting a visit that doesn't exist returns the proper status
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "VACCINATOR" } )
    @Transactional
    public void testGetNonExistentVisit () throws Exception {
        mvc.perform( get( "/api/v1/vaccinevisits/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests that deleting a visit that doesn't exist returns the proper status.
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "VACCINATOR" } )
    @Transactional
    public void testDeleteNonExistentVisit () throws Exception {
        mvc.perform( delete( "/api/v1/vaccinevisits/-1" ).with( csrf() ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests creating an appointment request with bad data. Should return a bad
     * request.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
    public void testCreateBadVisitRequest () throws Exception {

        final VaccineVisitForm appointmentForm = new VaccineVisitForm();

        final Long id = reqService.findAll().get( 0 ).getId();
        appointmentForm.setRequestId( id + "" );
        appointmentForm.setDateTime( "0" );
        appointmentForm.setVaccinator( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setVaccine( "Moderna" );
        appointmentForm.setDose( "1" );

        mvc.perform( post( "/api/v1/vaccinevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Further tests the VaccineVisit API calls.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    @Transactional
    public void testVisitRequestAPI () throws Exception {

        final User patient = userService.findByName( "patient" );

        final VaccineVisitForm vaccineVisitForm = new VaccineVisitForm();

        final Long reqid = reqService.findAll().get( 0 ).getId();
        vaccineVisitForm.setRequestId( reqid + "" );

        vaccineVisitForm.setDateTime( "2030-11-19T04:50:00.000-05:00" ); // 2030-11-19
        // 4:50 AM
        // EST
        vaccineVisitForm.setVaccinator( "hcp" );
        vaccineVisitForm.setPatient( "patient" );
        vaccineVisitForm.setVaccine( "Moderna" );
        vaccineVisitForm.setDose( "1" );

        final VaccineVisit visit = vvService.build( vaccineVisitForm );

        vvService.save( visit );

        /*
         * We need the ID of the visit that actually got _saved_ when calling
         * the API above. This will get it
         */

        final Long id = service.findByPatient( patient ).get( 0 ).getId();

        mvc.perform( get( "/api/v1/vaccinevisits/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        List<VaccineVisit> forPatient = service.findAll();
        Assertions.assertEquals( 1, forPatient.size() );

        mvc.perform( get( "/api/v1/vaccinevisits/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        forPatient = service.findAll();
        Assertions.assertEquals( 1, forPatient.size() );

        // Viewing a nonexistent ID should not work
        mvc.perform( get( "/api/v1/vaccinevisits/-1" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vaccineVisitForm ) ) ).andExpect( status().isNotFound() );

        final List<VaccineVisit> testlist = service.findAll();
        Assertions.assertEquals( 1, testlist.size() );

    }

    /**
     * Further tests the VaccineVisit API calls (delete method).
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "VACCINATOR" } )
    @Transactional
    public void testVisitRequestAPIDelete () throws Exception {
        final User patient = userService.findByName( "patient" );

        final VaccineVisitForm appointmentForm = new VaccineVisitForm();
        appointmentForm.setDateTime( "2030-11-19T04:50:00.000-05:00" ); // 2030-11-19
        // 4:50 AM
        // EST
        appointmentForm.setVaccinator( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setVaccine( "Moderna" );
        appointmentForm.setDose( "1" );

        final Long reqid = reqService.findAll().get( 0 ).getId();
        appointmentForm.setRequestId( reqid + "" );

        /* Create the request */
        mvc.perform( post( "/api/v1/vaccinevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isOk() );
        final Long id = service.findByPatient( patient ).get( 0 ).getId();
        mvc.perform( delete( "/api/v1/vaccinevisits/" + id ).with( csrf() ) ).andExpect( status().isOk() );

    }

}
