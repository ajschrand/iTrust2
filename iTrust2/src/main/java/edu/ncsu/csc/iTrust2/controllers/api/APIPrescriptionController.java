package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PrescriptionService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with prescriptions. Exposes functionality
 * to add, edit, fetch, and delete prescriptions.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPrescriptionController extends APIController {

    /** LoggerUtil */
    @Autowired
    private LoggerUtil                        loggerUtil;

    /** Prescription service */
    @Autowired
    private PrescriptionService               prescriptionService;

    /** User service */
    @Autowired
    private UserService                       userService;

    /**
     * Service for Associations
     */
    @Autowired
    private PatientAdvocateAssociationService associationService;

    /**
     * Adds a new prescription to the system. Requires HCP permissions.
     *
     * @param form
     *            details of the new prescription
     * @return the created prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity addPrescription ( @RequestBody final PrescriptionForm form ) {
        try {
            final Prescription p = prescriptionService.build( form );
            prescriptionService.save( p );
            loggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Created prescription with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(),
                    "Failed to create prescription" );
            return new ResponseEntity( errorResponse( "Could not save the prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits an existing prescription in the system. Matches prescriptions by
     * ids. Requires HCP permissions.
     *
     * @param form
     *            the form containing the details of the new prescription
     * @return the edited prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VIROLOGIST')" )
    @PutMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity editPrescription ( @RequestBody final PrescriptionForm form ) {
        try {
            final Prescription p = prescriptionService.build( form );
            final Prescription saved = prescriptionService.findById( p.getId() );
            if ( saved == null ) {
                loggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(),
                        "No prescription found with id " + p.getId() );
                return new ResponseEntity( errorResponse( "No prescription found with id " + p.getId() ),
                        HttpStatus.NOT_FOUND );
            }
            prescriptionService.save( p );
            loggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Edited prescription with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Failed to update prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the prescription with the given id.
     *
     * @param id
     *            the id
     * @return the id of the deleted prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VIROLOGIST')" )
    @DeleteMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity deletePrescription ( @PathVariable final Long id ) {
        final Prescription p = prescriptionService.findById( id );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "No prescription found with id " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            prescriptionService.delete( p );
            loggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Deleted prescription with id " + p.getId() );
            return new ResponseEntity( p.getId(), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Failed to delete prescription" );
            return new ResponseEntity( errorResponse( "Failed to delete prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a collection of all the prescriptions in the system.
     *
     * @return all saved prescriptions
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VIROLOGIST', 'ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/prescriptions" )
    public List<Prescription> getPrescriptions () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        if ( self.isDoctor() ) {
            // Return all prescriptions in system
            loggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "HCP viewed a list of all prescriptions" );
            return prescriptionService.findAll();
        }
        else {
            // Issue #106
            // Return only prescriptions assigned to the patient
            loggerUtil.log( TransactionType.PATIENT_PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "Patient viewed a list of their prescriptions" );
            return prescriptionService.findByPatient( self );
        }
    }

    /**
     * Returns a single prescription using the given id.
     *
     * @param id
     *            the id of the desired prescription
     * @return the requested prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VIROLOGIST')" )
    @GetMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity getPrescription ( @PathVariable final Long id ) {
        final Prescription p = prescriptionService.findById( id );
        if ( p == null ) {
            loggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "Failed to find prescription with id " + id );
            return new ResponseEntity( errorResponse( "No prescription found for " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(), "Viewed prescription  " + id );
            return new ResponseEntity( p, HttpStatus.OK );
        }
    }

    /**
     * Gets a Patient's prescriptions for a PatientAdvocate. Can only be called
     * by an advocate. Ensures that the advocate has the required permissions
     *
     * @param username
     *            the username of the patient whose prescriptions you are
     *            viewing
     * @return a ResponseEntity with the status and the list of prescriptions,
     *         if the get was successful
     */
    @GetMapping ( BASE_PATH + "/prescriptions/patient/{username}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getPrescriptionsByPatient ( @PathVariable final String username ) {
        final User patient = userService.findByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No patient found with username: " + username ),
                    HttpStatus.NOT_FOUND );
        }

        final User advocate = userService.findByName( LoggerUtil.currentUser() );
        final PatientAdvocateAssociation association = associationService.getAssociation( patient.getUsername(),
                advocate.getUsername() );
        if ( association == null || !association.getPrescription() ) {
            return new ResponseEntity( errorResponse( "You do not have permission to view this information." ),
                    HttpStatus.FORBIDDEN );
        }

        loggerUtil.log( TransactionType.PA_VIEW_PRESCRIPTIONS, username );
        return new ResponseEntity( prescriptionService.findByPatient( patient ), HttpStatus.OK );
    }
}
