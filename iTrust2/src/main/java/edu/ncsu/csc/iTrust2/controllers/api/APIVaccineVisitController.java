package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.VaccineVisitForm;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineVisit;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.VaccineVisitService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the VaccineVisit model. In all
 * requests made to this controller, the id provided is a numeric id that is the
 * primary key of the vaccinevisit in question.
 *
 * @author accline2
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIVaccineVisitController extends APIController {

    /** VaccineVisit service */
    @Autowired
    private VaccineVisitService service;

    /** Logger */
    @Autowired
    private LoggerUtil          loggerUtil;

    /** User service */
    @Autowired
    private UserService<User>   userService;

    /**
     * Parses a VVF and creates a VaccineVisit within the database.
     *
     * @param vvf
     *            form within JSON object to build
     * @return built visit
     */
    @PostMapping ( BASE_PATH + "/vaccinevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public ResponseEntity createVaccineVisit ( @RequestBody final VaccineVisitForm vvf ) {
        try {
            vvf.setVaccinator( LoggerUtil.currentUser() );
            final VaccineVisit request = service.build( vvf );
            if ( null != service.findById( request.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "VaccineVisit with the id " + request.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            service.save( request );
            loggerUtil.log( TransactionType.OFFICE_VISIT_CREATED, request.getPatient(), request.getVaccinator() );
            return new ResponseEntity( request, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse(
                    "Error occurred while validating or saving " + vvf.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Removes the VaccineVisit from the database with the id provided. Patients
     * are unable to delete visits.
     *
     * @param id
     *            id of appointment to delete
     * @return id of visit deleted with HTTP exit code
     */
    @DeleteMapping ( BASE_PATH + "/vaccinevisits/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public ResponseEntity deleteVaccineVisit ( @PathVariable final Long id ) {
        final VaccineVisit request = service.findById( id );
        if ( null == request ) {
            return new ResponseEntity( errorResponse( "No VaccineVisit found for id " + id ), HttpStatus.NOT_FOUND );
        }

        try {
            service.delete( request );
            loggerUtil.log( TransactionType.OFFICE_VISIT_DELETED, request.getPatient(), request.getVaccinator() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not delete " + request.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * View all Vaccine Visits. Used only by personell (hcp, vaccinators).
     *
     * @return list of visits
     */
    @GetMapping ( BASE_PATH + "vaccinevisits/all" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public List<VaccineVisit> viewAllVaccineVisits () {
        final List<VaccineVisit> requests = service.findAll();

        requests.stream().map( VaccineVisit::getPatient ).distinct().forEach(
                e -> loggerUtil.log( TransactionType.OFFICE_VISIT_VIEWED, LoggerUtil.currentUser(), e.getUsername() ) );
        return requests;
    }

    /**
     * View VaccineVisit with corresponding id.
     *
     * @param id
     *            id to search for
     * @return visit with matching id
     */
    @GetMapping ( BASE_PATH + "/vaccinevisits/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_VACCINATOR')" )
    public ResponseEntity getVaccineVisitId ( @PathVariable ( "id" ) final Long id ) {
        final VaccineVisit request = service.findById( id );
        if ( null != request ) {
            loggerUtil.log( TransactionType.OFFICE_VISIT_VIEWED, request.getPatient(), request.getVaccinator() );

            /* Patient can't look at anyone else's requests */
            final User self = userService.findByName( LoggerUtil.currentUser() );
            if ( self.getRoles().contains( Role.ROLE_PATIENT ) && !request.getPatient().equals( self ) ) {
                return new ResponseEntity( HttpStatus.UNAUTHORIZED );
            }
        }
        return null == request
                ? new ResponseEntity( errorResponse( "No VaccineAppointmentRequest found for id " + id ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( request, HttpStatus.OK );
    }

    /**
     * Retrieves the VaccineVisit specified by the current patient user.
     *
     * @param id
     *            id to search for
     * @return the patient's VaccineVisits
     */
    @GetMapping ( BASE_PATH + "/vaccinevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_VACCINATOR')" )
    public List<VaccineVisit> getVaccineVisitsForPatient ( @PathVariable final Long id ) {
        final User patient = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.OFFICE_VISIT_VIEWED, LoggerUtil.currentUser(), LoggerUtil.currentUser() );
        return service.findByPatient( patient ).stream().collect( Collectors.toList() );
    }

}
