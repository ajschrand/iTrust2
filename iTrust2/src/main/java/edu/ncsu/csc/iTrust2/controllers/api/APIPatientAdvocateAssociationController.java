package edu.ncsu.csc.iTrust2.controllers.api;

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

import edu.ncsu.csc.iTrust2.forms.PatientAdvocateAssociationForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * PatientAdvocateAssociation model.
 *
 * @author noahalexander
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientAdvocateAssociationController extends APIController {

    /** LoggerUtil that logs all endpoints */
    @Autowired
    private LoggerUtil                        loggerUtil;

    /** Personnel Service that stores all Patient Advocates */
    @Autowired
    private PersonnelService                  personnelService;

    /** Patient Service that stores all Patients */
    @Autowired
    private PatientService<Patient>           patientService;

    /** Patient Advocate Association Service that stores all Associations */
    @Autowired
    private PatientAdvocateAssociationService service;

    /**
     * Returns a PatientAdvocateAssociation object with the given id
     *
     * @param id
     *            the id of the PatientAdvocateAssociation to retrieve
     * @return returns the association with that id
     */
    @GetMapping ( BASE_PATH + "patientAdvocateAssociations/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT_ADVOCATE', 'ROLE_ADMIN', 'ROLE_PATIENT')" )
    public ResponseEntity getPatientAdvocateAssociation ( @PathVariable ( "id" ) final Long id ) {
        final PatientAdvocateAssociation a = service.findById( id );
        if ( null == a ) { // Make sure the association exists
            return new ResponseEntity( errorResponse( "No association found with " + id ), HttpStatus.NOT_FOUND );
        }

        loggerUtil.log( TransactionType.VIEW_ASSOCIATION, LoggerUtil.currentUser() );
        return new ResponseEntity( a, HttpStatus.OK );
    }

    /**
     * Returns a list of all Patient Advocate Associations
     *
     * @return all the associations
     */
    @GetMapping ( BASE_PATH + "patientAdvocateAssociations" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN')" )
    public ResponseEntity getAllPatientAdvocateAssociations () {
        loggerUtil.log( TransactionType.VIEW_ALL_ASSOCIATIONS, LoggerUtil.currentUser() );
        return new ResponseEntity( service.findAll(), HttpStatus.OK );
    }

    /**
     * Returns a list of all Patient Advocate Associations for a given patient
     *
     * @param patientID
     *            the id of the patient to get associations for
     * @return all the associations for a patient
     */
    @GetMapping ( BASE_PATH + "patientAdvocateAssociations/patient/{patientID}" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN')" )
    public ResponseEntity getAllPatientAdvocateAssociationsForPatient (
            @PathVariable ( "patientID" ) final String patientID ) {
        final Patient p = patientService.findById( patientID );
        if ( p == null ) { // Make sure the patient exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }

        loggerUtil.log( TransactionType.VIEW_ASSIGNED_PAS, LoggerUtil.currentUser() );
        return new ResponseEntity( service.getPatientAssociations( patientID ), HttpStatus.OK );
    }

    /**
     * Returns a list of all Patient Advocate Associations for the currently
     * logged in patient
     *
     * @return all the associations for the patient
     */
    @GetMapping ( BASE_PATH + "patientAdvocateAssociations/currentPatientAssociations" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public ResponseEntity getAllPatientAdvocateAssociationsForPatientAsPatient () {
        final Patient patient = (Patient) patientService.findByName( LoggerUtil.currentUser() );
        if ( patient == null ) { // Make sure the logged in patient exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }

        loggerUtil.log( TransactionType.VIEW_ASSIGNED_PAS, LoggerUtil.currentUser() );
        return new ResponseEntity( service.getPatientAssociations( LoggerUtil.currentUser() ), HttpStatus.OK );
    }

    /**
     * Returns a list of all Patient Advocate Associations for a given advocate
     *
     * @param patientAdvocateID
     *            the id of the patient advocate to get associations for
     * @return all the associations for a patient advocate
     */
    @GetMapping ( BASE_PATH + "patientAdvocateAssociations/patientAdvocate/{patientAdvocateID}" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN')" )
    public ResponseEntity getAllPatientAdvocateAssociationsForPatientAdvocate (
            @PathVariable ( "patientAdvocateID" ) final String patientAdvocateID ) {
        final Personnel pa = personnelService.findById( patientAdvocateID );
        if ( pa == null ) { // Make sure the patient advocate exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }

        loggerUtil.log( TransactionType.VIEW_PATIENTS_ASSIGNED, LoggerUtil.currentUser() );
        return new ResponseEntity( service.getPatientAdvocateAssociations( patientAdvocateID ), HttpStatus.OK );
    }

    /**
     * Returns a list of all Patient Advocate Associations owned by the current
     * advocate
     *
     * @return all the associations for the current patient advocate
     */
    @GetMapping ( BASE_PATH + "patientAdvocateAssociations/currentAdvocate" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getAllPatientAdvocateAssociationsForCurrentPatientAdvocate () {
        final User pa = personnelService.findByName( LoggerUtil.currentUser() );
        if ( pa == null ) { // Make sure the patient advocate exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }

        loggerUtil.log( TransactionType.VIEW_PATIENTS_ASSIGNED, LoggerUtil.currentUser() );
        return new ResponseEntity( service.getPatientAdvocateAssociations( pa.getId() ), HttpStatus.OK );
    }

    /**
     * Updates a Patient Advocate Association with new permissions
     *
     * @param id
     *            id of association to edit
     * @param paForm
     *            form that contains new association information
     * @return returns OK if the association was successfully updated
     */
    @PutMapping ( BASE_PATH + "patientAdvocateAssociations/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')" )
    public ResponseEntity updatePatientAdvocateAssociation ( @PathVariable ( "id" ) final Long id,
            @RequestBody final PatientAdvocateAssociationForm paForm ) {
        final PatientAdvocateAssociation paa = service.getAssociationById( id );
        if ( paa == null ) { // Make sure the association being updated exists
            return new ResponseEntity( errorResponse( "No association found with id " + id ), HttpStatus.NOT_FOUND );
        }

        try { // Update the association
            paa.update( paForm );
            service.save( paa );
            loggerUtil.log( TransactionType.ADJUST_PA_PERMISSIONS, LoggerUtil.currentUser() );
            return new ResponseEntity( HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Creates a new Patient Advocate Association; Assigns a Patient to a
     * Patient Advocate
     *
     * @param paForm
     *            form containing association information
     * @return returns OK if the association was successfully created
     */
    @PostMapping ( BASE_PATH + "patientAdvocateAssociations" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity createPatientAdvocateAssociation (
            @RequestBody final PatientAdvocateAssociationForm paForm ) {
        if ( paForm.getPatient() == null ) { // Make sure the patient exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }
        if ( paForm.getPatientAdvocate() == null ) { // Make sure the patient
                                                     // advocate exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }
        if ( patientService.findById( paForm.getPatient() ) == null
                || personnelService.findById( paForm.getPatientAdvocate() ) == null ) {
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }
        if ( service.getAssociation( paForm.getPatient(), paForm.getPatientAdvocate() ) != null ) { // Check
                                                                                                    // if
                                                                                                    // the
                                                                                                    // association
                                                                                                    // already
                                                                                                    // exists
            return new ResponseEntity( HttpStatus.CONFLICT );
        }

        final PatientAdvocateAssociation a = new PatientAdvocateAssociation( paForm );

        service.save( a );

        final Patient patient = (Patient) patientService.findByName( a.getPatient() );

        // Add the new association to both the patient and patient advocate

        final Long id = (Long) a.getId();
        patient.addAssociation( id );

        final PatientAdvocate advocate = (PatientAdvocate) personnelService.findById( a.getPatientAdvocate() );
        advocate.addAssociation( (Long) a.getId() );

        // Save the newly updated patient and patient advocate

        patientService.save( patient );
        personnelService.save( advocate );

        loggerUtil.log( TransactionType.ASSIGN_PA, LoggerUtil.currentUser() );
        return new ResponseEntity( HttpStatus.CREATED );

    }

    /**
     * Delete a Patient Advocate Association; Dissociate a Patient from a
     * Patient Advocate
     *
     * @param id
     *            id of association to delete
     * @return OK if the association was successfully deleted
     */
    @DeleteMapping ( BASE_PATH + "patientAdvocateAssociations/{id}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity deletePatientAdvocateAssociation ( @PathVariable ( "id" ) final Long id ) {
        final PatientAdvocateAssociation a = service.getAssociationById( id );
        if ( a == null ) { // Make sure the association being deleted exists
            return new ResponseEntity( errorResponse( "No association found with id " + id ), HttpStatus.NOT_FOUND );
        }

        final Patient patient = (Patient) patientService.findByName( a.getPatient() );

        if ( !patient.deleteAssociation( id ) ) { // Make sure the patient of
                                                  // the association exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }

        patientService.save( patient );

        final PatientAdvocate advocate = (PatientAdvocate) personnelService.findById( a.getPatientAdvocate() );
        if ( !advocate.deleteAssocation( id ) ) { // Make sure the advocate of
                                                  // the association exists
            return new ResponseEntity( HttpStatus.FAILED_DEPENDENCY );
        }

        personnelService.save( advocate );

        try {
            service.delete( a ); // Delete the association
            loggerUtil.log( TransactionType.REMOVE_PA_ASSIGNMENT, LoggerUtil.currentUser() );
            return new ResponseEntity( HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.BAD_REQUEST );
        }
    }
}
