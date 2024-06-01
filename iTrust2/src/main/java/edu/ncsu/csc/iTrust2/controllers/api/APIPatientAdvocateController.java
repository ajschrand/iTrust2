package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.PatientAdvocateForm;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateUserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * PatientAdvocate model.
 *
 * @author noahalexander
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientAdvocateController extends APIController {

    /** LoggerUtil */
    @Autowired
    private LoggerUtil                        loggerUtil;

    /** Personnel Service */
    @Autowired
    private PersonnelService                  service;

    /**
     * PatientAdvocateAssociation Service
     */
    @Autowired
    private PatientAdvocateAssociationService aService;

    /**
     * User Service
     */
    @Autowired
    private UserService                       userService;

    /**
     * Post operation for creating a PatientAdvocate with their demographics
     *
     * @param form
     *            PatientAdvocateUserForm to create a PatientAdvocate with
     *            demographics
     * @return returns the PatientAdvocate and response
     */
    @PostMapping ( BASE_PATH + "/patientAdvocates" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN') " )
    public ResponseEntity createPatientAdvocate ( @RequestBody final PatientAdvocateUserForm form ) {
        if ( null != service.findByName( form.getUsername() ) ) {
            return new ResponseEntity(
                    errorResponse( "Advocate with the id " + form.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        final List<Role> rolesOnUser = form.getRoles().stream().map( Role::valueOf ).collect( Collectors.toList() );
        if ( rolesOnUser.size() != 1 || !rolesOnUser.contains( Role.ROLE_PATIENTADVOCATE ) ) {
            return new ResponseEntity( errorResponse( "Patient Advocate cannot be another role" ),
                    HttpStatus.BAD_REQUEST );
        }

        final PatientAdvocate adv = new PatientAdvocate();
        adv.setUsername( form.getUsername() );
        adv.updateAll( form );
        try {
            service.save( adv );
            loggerUtil.log( TransactionType.CREATE_PATIENT_ADVOCATE, LoggerUtil.currentUser(), adv.getUsername(),
                    null );
            return new ResponseEntity( adv, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            System.out.println( "PRINTT" + e.getMessage() );
            return new ResponseEntity(
                    errorResponse( "Could not create " + form.getUsername() + " because of" + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Put operation for an admin editing a PatientAdvocates login information
     * and demographics
     *
     * @param id
     *            username of the PatientAdvocate
     * @param form
     *            PatientAdvocateUserForm to edit a PatientAdvocate login
     *            credentials and demographics
     * @return returns the advocate and response
     */
    @PutMapping ( BASE_PATH + "/patientAdvocates/{id}" )
    @PreAuthorize ( "hasAnyRole('ADMIN')" )
    public ResponseEntity editPatientAdvocateAdmin ( @PathVariable ( "id" ) final String id,
            @RequestBody final PatientAdvocateUserForm form ) {

        final PatientAdvocate adv = (PatientAdvocate) service.findByName( form.getUsername() );
        if ( null == adv ) {
            return new ResponseEntity(
                    errorResponse( "Advocate with the id " + form.getUsername() + " does not already exist" ),
                    HttpStatus.CONFLICT );
        }

        adv.updateAll( form );
        try {
            service.save( adv );
            loggerUtil.log( TransactionType.EDIT_PATIENT_ADVOCATE, LoggerUtil.currentUser(), adv.getUsername(), null );
            return new ResponseEntity( adv, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not edit " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Put operation for an advocate editing their own demographic information
     *
     * @param form
     *            PatientAdvocateForm to edit a PatientAdvocate's demographics
     * @return returns the advocate and response
     */
    @PutMapping ( BASE_PATH + "/patientAdvocates/demographics" )
    @PreAuthorize ( "hasAnyRole('PATIENTADVOCATE')" )
    public ResponseEntity editPatientAdvocateDemographics ( @RequestBody final PatientAdvocateForm form ) {

        // Check the form username against the current user
        if ( ( null != form.getUsername() && !LoggerUtil.currentUser().equals( form.getUsername() ) ) ) {
            return new ResponseEntity( errorResponse( "The ID provided does not match the ID of the current user" ),
                    HttpStatus.CONFLICT );
        }

        // check that the username provided matches to an existing advocate
        final PatientAdvocate adv = (PatientAdvocate) service.findByName( form.getUsername() );
        if ( null == adv ) {
            return new ResponseEntity(
                    errorResponse( "Advocate with the id " + form.getUsername() + " does not already exist" ),
                    HttpStatus.NOT_FOUND );
        }

        // perform the state update
        adv.update( form );

        if ( ( null != adv.getUsername() && !LoggerUtil.currentUser().equals( adv.getUsername() ) ) ) {
            return new ResponseEntity(
                    errorResponse( "The ID provided does not match the ID of the Advocate provided" ),
                    HttpStatus.CONFLICT );
        }

        try {
            service.save( adv );
            loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( adv, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not edit " + LoggerUtil.currentUser() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Delete operation for deleting a PatientAdvocate from the system and all
     * of it's associations
     *
     * @param id
     *            username of the PatientAdvocate
     * @return returns a response
     */
    @DeleteMapping ( BASE_PATH + "/patientAdvocates/{id}" )
    @PreAuthorize ( "hasAnyRole('ADMIN')" )
    public ResponseEntity deletePatientAdvocate ( @PathVariable final String id ) {
        final Personnel advocate = (Personnel) service.findByName( id );
        try {
            if ( null == advocate ) {
                return new ResponseEntity( errorResponse( "No patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            for ( final PatientAdvocateAssociation a : aService.findAll() ) {
                if ( a.getPatientAdvocate().equals( id ) ) {
                    final Patient p = (Patient) userService.findByName( a.getPatient() );
                    p.deleteAssociation( (Long) a.getId() );
                    userService.save( p );
                    aService.delete( a );
                }
            }
            service.delete( advocate );
            loggerUtil.log( TransactionType.DELETE_PATIENT_ADVOCATE, LoggerUtil.currentUser() );
            return new ResponseEntity( "{\"id\":\"" + id + "\"}", HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not delete " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
