package edu.ncsu.csc.iTrust2.controllers.api;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.VaccineAppointmentRequestForm;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineAppointmentRequest;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.VaccineAppointmentRequestService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the VaccineAppointmentRequest
 * model. In all requests made to this controller, the {id} provided is a
 * numeric ID that is the primary key of the appointment request in question
 *
 * @author accline2, mjcheim
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIVaccineAppointmentRequestController extends APIController {

    /**
     * AppointmentRequest service
     */
    @Autowired
    private VaccineAppointmentRequestService service;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil                       loggerUtil;

    /** User service */
    @Autowired
    private UserService<User>                userService;

    /**
     * Parses a VARF and create a VaccineAppointmentRequest.
     *
     * @param varf
     *            form within JSON object to build
     * @return built appointment
     */
    @PostMapping ( BASE_PATH + "/vaccineappointments" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public ResponseEntity createVaccinationAppointmentRequest (
            @RequestBody final VaccineAppointmentRequestForm varf ) {
        try {
            final VaccineAppointmentRequest request = service.build( varf );
            if ( null != service.findById( request.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "VaccineAppointmentRequest with the id " + request.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            service.save( request );
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_SUBMITTED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( request, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse(
                    "Error occurred while validating or saving " + varf.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Retrieves the VaccineAppointmentRequest specified by the current patient
     * user.
     *
     * @return the patient's vaccine appointment requests
     */
    @GetMapping ( BASE_PATH + "/vaccineappointments/currentuser" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_VACCINATOR')" )
    public List<VaccineAppointmentRequest> getVaccineAppointmentRequestForPatient () {
        final User patient = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_VIEWED, LoggerUtil.currentUser(),
                LoggerUtil.currentUser() );
        return service.findByPatient( patient ).stream().filter( e -> e.getStatus().equals( Status.APPROVED ) )
                .collect( Collectors.toList() );
    }

    /**
     * Removes the VAR from the database with the id provided.
     *
     * @param id
     *            id of appointment to delete
     * @return if of appointment deleted with HTTP exit code
     */
    @DeleteMapping ( BASE_PATH + "/vaccineappointments/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_VACCINATOR')" )
    public ResponseEntity deleteVaccinationAppointmentRequest ( @PathVariable final Long id ) {
        final VaccineAppointmentRequest request = service.findById( id );
        if ( null == request ) {
            return new ResponseEntity( errorResponse( "No VaccineAppointmentRequest found for id " + id ),
                    HttpStatus.NOT_FOUND );
        }

        final User self = userService.findByName( LoggerUtil.currentUser() );
        if ( self.getRoles().contains( Role.ROLE_PATIENT ) && !request.getPatient().equals( self ) ) {
            return new ResponseEntity( HttpStatus.UNAUTHORIZED );
        }

        try {
            service.delete( request );
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DELETED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not delete " + request.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * View all vaccine appointment requests.
     *
     * @return list of vaccine appointment requests
     */
    @GetMapping ( BASE_PATH + "/vaccineappointments" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public List<VaccineAppointmentRequest> viewAllAppointmentRequests () {
        final List<VaccineAppointmentRequest> requests = service.findAll();

        requests.stream().map( VaccineAppointmentRequest::getPatient ).distinct().forEach( e -> loggerUtil
                .log( TransactionType.APPOINTMENT_REQUEST_VIEWED, LoggerUtil.currentUser(), e.getUsername() ) );
        return requests;
    }

    /**
     * Gets the vaccine appointment request at an ID
     *
     * @param id
     *            the id of the appointment
     * @return the vaccine appointment at the given ID
     */
    @GetMapping ( BASE_PATH + "/vaccineappointments/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_VACCINATOR')" )
    public ResponseEntity getVaccineAppointmentRequestId ( @PathVariable ( "id" ) final Long id ) {
        final VaccineAppointmentRequest request = service.findById( id );
        if ( null != request ) {
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_VIEWED, request.getPatient(), request.getHcp() );

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
     * Gets the vaccine appointments within a three hour time gap for a visit.
     *
     * @param id
     *            the id of the appointment
     * @return appointments within 3 hours of the scheduled visit time
     */
    @GetMapping ( BASE_PATH + "vaccineappointments/forvisit/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public List<VaccineAppointmentRequest> getVaccineAppointmentVisitFilter ( @PathVariable ( "id" ) final String id ) {
        final List<VaccineAppointmentRequest> returnList = new ArrayList<VaccineAppointmentRequest>();
        final User patient = userService.findById( id );
        final List<VaccineAppointmentRequest> requests = service.findByPatient( patient );
        for ( int i = 0; i < requests.size(); i++ ) {
            final ZonedDateTime currentTime = ZonedDateTime.now();
            final ZonedDateTime lowerBound = currentTime.minusHours( 3 );
            final ZonedDateTime upperBound = currentTime.plusHours( 3 );
            final ZonedDateTime requestTime = requests.get( i ).getDate();
            if ( requestTime.isAfter( lowerBound ) && requestTime.isBefore( upperBound ) ) {
                returnList.add( requests.get( i ) );
            }

        }
        return returnList;
    }

    /**
     * Updates the VaccineAppointmentRequest with the id provided by overwriting
     * it with the new VaccineAppointmentRequest that is provided. If the ID
     * provided does not match the ID set in the VaccineAppointmentRequest
     * provided, the update will not take place
     *
     * @param id
     *            The ID of the VaccineAppointmentRequest to be updated
     * @param varf
     *            The updated VaccineAppointmentRequestForm to parse, validate,
     *            and save
     * @return The VaccineAppointmentRequest that is created from the Form that
     *         is provided
     */
    @PutMapping ( BASE_PATH + "/vaccineappointments/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public ResponseEntity updateVaccineAppointmentRequest ( @PathVariable final Long id,
            @RequestBody final VaccineAppointmentRequestForm varf ) {
        try {
            final VaccineAppointmentRequest request = service.build( varf );
            request.setId( id );

            final VaccineAppointmentRequest dbRequest = service.findById( id );
            if ( null == dbRequest ) {
                return new ResponseEntity( errorResponse( "No VaccineAppointmentRequest found for id " + id ),
                        HttpStatus.NOT_FOUND );
            }

            service.save( request );
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_UPDATED, request.getPatient(), request.getHcp() );
            if ( request.getStatus().getCode() == Status.APPROVED.getCode()
                    || request.getStatus().getCode() == Status.COMPLETED.getCode() ) {
                loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_APPROVED, request.getPatient(), request.getHcp() );
            }
            else {
                loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DENIED, request.getPatient(), request.getHcp() );
            }

            return new ResponseEntity( request, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + varf.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }
}
