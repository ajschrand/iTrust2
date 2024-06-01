package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.PatientForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineType;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.enums.VaccinationStatus;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.VaccineTypeService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Patient model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientController extends APIController {

    /**
     * Patient service
     */
    @Autowired
    private PatientService                    patientService;

    /**
     * User Service
     */
    @Autowired
    private UserService                       userService;

    /**
     * Vaccine Service
     */
    @Autowired
    private VaccineTypeService                vaccService;

    /**
     * PatientAdvocateAssociation Service
     */
    @Autowired
    private PatientAdvocateAssociationService aService;

    /**
     * LoggerUtil
     */
    @Autowired
    private LoggerUtil                        loggerUtil;

    /**
     * Retrieves and returns a list of all Patients stored in the system
     *
     * @return list of patients
     */
    @GetMapping ( BASE_PATH + "/patients" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP','ROLE_ADMIN','ROLE_VACCINATOR','ROLE_BSM','ROLE_VIROLOGIST','ROLE_OD','ROLE_OPH','ROLE_LABTECH','ROLE_ER')" )
    public List<Patient> getPatients () {
        final List<Patient> patients = patientService.findAll();
        return patients;
    }

    /**
     * Retrieves and returns a list of all Patients stored in the system
     *
     * @return list of patients
     */
    @GetMapping ( BASE_PATH + "/patients/advocate" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public List<Patient> getPatientsAsAdvocate () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        final List<Patient> patients = patientService.getPatientsByAdvocate( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.GET_PATIENTS_FOR_ADVOCATE, self );
        return patients;
    }

    /**
     * If you are logged in as a patient, then you can use this convenience
     * lookup to find your own information without remembering your id. This
     * allows you the shorthand of not having to look up the id in between.
     *
     * @return The patient object for the currently authenticated user.
     */
    @GetMapping ( BASE_PATH + "/patient" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getPatient () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        final Patient patient = (Patient) patientService.findByName( self.getUsername() );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for you, " + self.getUsername() ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, self );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Retrieves and returns the Patient with the username provided
     *
     * @param username
     *            The username of the Patient to be retrieved, as stored in the
     *            Users table
     * @return response
     */
    @GetMapping ( BASE_PATH + "/patients/{username}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity getPatient ( @PathVariable ( "username" ) final String username ) {
        final Patient patient = (Patient) patientService.findByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No Patient found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser(), username,
                    "HCP retrieved demographics for patient with username " + username );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Updates the Patient with the id provided by overwriting it with the new
     * Patient record that is provided. If the ID provided does not match the ID
     * set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Patient to be updated
     * @param patientF
     *            The updated Patient to save
     * @return response
     */

    @PutMapping ( BASE_PATH + "/patients/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP','ROLE_ADMIN','ROLE_PATIENT')" )
    public ResponseEntity updatePatient ( @PathVariable final String id, @RequestBody final PatientForm patientF ) {
        // check that the user is an HCP or a patient with username equal to id
        boolean userEdit = false; // true if user edits his or her own
                                  // demographics, false if hcp edits them
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final SimpleGrantedAuthority hcp = new SimpleGrantedAuthority( "ROLE_HCP" );
        try {
            userEdit = !auth.getAuthorities().contains( hcp );
            if ( !auth.getName().equals( id ) && userEdit ) {
                return new ResponseEntity( errorResponse( "You do not have permission to edit this record" ),
                        HttpStatus.UNAUTHORIZED );
            }

        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.UNAUTHORIZED );
        }

        try {
            final Patient patient = (Patient) patientService.findByName( id );

            // Shouldn't be possible but let's check anyways
            if ( null == patient ) {
                return new ResponseEntity( errorResponse( "Patient not found" ), HttpStatus.NOT_FOUND );
            }

            patient.update( patientF );

            final User dbPatient = patientService.findByName( id );
            if ( null == dbPatient ) {
                return new ResponseEntity( errorResponse( "No Patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            patientService.save( patient );

            // Log based on whether user or hcp edited demographics
            if ( userEdit ) {
                loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser(),
                        "User with username " + patient.getUsername() + "updated their demographics" );
            }
            else {
                loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser(), patient.getUsername(),
                        "HCP edited demographics for patient with username " + patient.getUsername() );
            }
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Delete operation for deleting a Patient and all of it's associations
     *
     * @param id
     *            username of the patient
     * @return returns a response
     */
    @DeleteMapping ( BASE_PATH + "/patients/{id}" )
    @PreAuthorize ( "hasAnyRole('ADMIN')" )
    public ResponseEntity deletePatient ( @PathVariable final String id ) {
        final User user = patientService.findByName( id );
        try {
            if ( null == user ) {
                return new ResponseEntity( errorResponse( "No patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            for ( final PatientAdvocateAssociation a : aService.findAll() ) {
                if ( a.getPatient().equals( id ) ) {
                    final PatientAdvocate adv = (PatientAdvocate) userService.findByName( a.getPatientAdvocate() );
                    adv.deleteAssocation( (Long) a.getId() );
                    userService.save( adv );
                    aService.delete( a );
                }
            }
            patientService.delete( user );

            loggerUtil.log( TransactionType.DELETE_USER, LoggerUtil.currentUser() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not delete " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Get the patient's zip code to autofill Find Expert Form
     *
     * @return Response entity with status and response data (zip or error
     *         message)
     *
     */
    @GetMapping ( BASE_PATH + "/patient/findexperts/getzip" )
    @PreAuthorize ( "hasRole( 'ROLE_PATIENT')" )
    public ResponseEntity getPatientZip () {
        final String user = LoggerUtil.currentUser();
        if ( user == null ) {
            return new ResponseEntity( errorResponse( "Patient not found" ), HttpStatus.NOT_FOUND );
        }
        final String zip = ( (Patient) patientService.findByName( user ) ).getZip();
        if ( zip == null ) {
            return new ResponseEntity( errorResponse( "Patient does not have zip stored" ), HttpStatus.NO_CONTENT );
        }
        else {
            final String[] zipParts = zip.split( "-" );
            return new ResponseEntity( zipParts, HttpStatus.OK );

        }

    }

    /**
     * Updates the Patient with the id provided by overwriting it with the new
     * Patient record that is provided. If the ID provided does not match the ID
     * set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Patient to be updated
     * @param dose
     *            The dose to update
     * @param vaccine
     *            The vaccine type to update
     * @return response
     */
    @PutMapping ( BASE_PATH + "/patients/updateVaccination/{id}/{dose}/{vaccine}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP','ROLE_ADMIN','ROLE_VACCINATOR')" )
    public ResponseEntity updatePatientVaccination ( @PathVariable final String id, @PathVariable final String dose,
            @PathVariable final String vaccine ) {

        try {
            final Patient patient = (Patient) patientService.findByName( id );

            // Shouldn't be possible but let's check anyways
            if ( null == patient ) {
                return new ResponseEntity( errorResponse( "Patient not found" ), HttpStatus.NOT_FOUND );
            }

            final VaccineType vaccineType = vaccService.findByName( vaccine );
            patient.setDoses( dose );
            patient.setVaccineType( vaccineType );
            if ( Integer.parseInt( dose ) < vaccineType.getNumDoses() ) {
                patient.setVaccinationStatus( VaccinationStatus.FIRST_DOSE );
            }
            else {
                patient.setVaccinationStatus( VaccinationStatus.FULLY_VACCINATED );
            }

            final User dbPatient = patientService.findByName( id );
            if ( null == dbPatient ) {
                return new ResponseEntity( errorResponse( "No Patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            patientService.save( patient );

            loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser(), patient.getUsername(),
                    "HCP edited vaccination demographics for patient with username " + patient.getUsername() );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
