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

import edu.ncsu.csc.iTrust2.forms.VaccineTypeForm;
import edu.ncsu.csc.iTrust2.models.VaccineType;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.VaccineTypeService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the VaccineType model.
 *
 * @author skadhir, mpenaga
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIVaccineTypeController extends APIController {

    /** Vaccine service */
    @Autowired
    private VaccineTypeService service;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil         loggerUtil;

    /**
     * Adds a new vaccine to the system. Requires admin permissions. Returns an
     * error message if something goes wrong.
     *
     * @param form
     *            the vaccine form
     * @return the created vaccine
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( BASE_PATH + "/vaccines" )
    public ResponseEntity createVaccineType ( @RequestBody final VaccineTypeForm form ) {
        try {
            final VaccineType vaccine = new VaccineType( form );

            // Make sure vaccine does not conflict with existing vaccines
            if ( service.existsByName( vaccine.getName() ) ) {
                loggerUtil.log( TransactionType.VACCINETYPE_CREATE, LoggerUtil.currentUser(),
                        "Conflict: vaccine with name " + vaccine.getName() + " already exists" );
                return new ResponseEntity(
                        errorResponse( "Vaccine with name " + vaccine.getName() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            service.save( vaccine );
            loggerUtil.log( TransactionType.VACCINETYPE_CREATE, LoggerUtil.currentUser(),
                    "Vaccine " + vaccine.getName() + " created" );
            return new ResponseEntity( vaccine, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.VACCINETYPE_CREATE, LoggerUtil.currentUser(), "Failed to create vaccine" );
            return new ResponseEntity( errorResponse( "Could not add vaccine: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a vaccine in the system. The id stored in the form must match an
     * existing vaccine, and changes to NDCs cannot conflict with existing NDCs.
     * Requires admin permissions.
     *
     * @param form
     *            the edited vaccine form
     * @return the edited vaccine or an error message
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/vaccines" )
    public ResponseEntity editVaccineType ( @RequestBody final VaccineTypeForm form ) {
        try {
            // Check for existing vaccine in database
            final VaccineType savedVaccine = service.findById( form.getId() );
            if ( savedVaccine == null ) {
                loggerUtil.log( TransactionType.VACCINETYPE_EDIT, LoggerUtil.currentUser(),
                        "No vaccine found with id " + form.getId() );
                return new ResponseEntity( errorResponse( "No vaccine found with id " + form.getId() ),
                        HttpStatus.NOT_FOUND );
            }

            // Check for duplicate name
            final VaccineType vaccine = new VaccineType( form );
            final VaccineType sameName = service.findByName( vaccine.getName() );
            if ( sameName != null && !sameName.getId().equals( savedVaccine.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Vaccine with name " + vaccine.getName() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            service.save( vaccine ); /* Overwrite existing vaccine */
            loggerUtil.log( TransactionType.VACCINETYPE_EDIT, LoggerUtil.currentUser(),
                    "Edited vaccine with id " + vaccine.getId() );
            return new ResponseEntity( vaccine, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.VACCINETYPE_EDIT, LoggerUtil.currentUser(), "Failed to edit vaccine" );
            return new ResponseEntity( errorResponse( "Failed to edit vaccine: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the vaccine with the id matching the given id. Requires admin
     * permissions.
     *
     * @param id
     *            the id of the vaccine to delete
     * @return the id of the deleted vaccine
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/vaccines/{id}" )
    public ResponseEntity deleteVaccineType ( @PathVariable final String id ) {
        try {
            final VaccineType vaccine = service.findById( Long.parseLong( id ) );
            if ( vaccine == null ) {
                loggerUtil.log( TransactionType.VACCINETYPE_DELETE, LoggerUtil.currentUser(),
                        "Could not find vaccine with id " + id );
                return new ResponseEntity( errorResponse( "No vaccine found with id " + id ), HttpStatus.NOT_FOUND );
            }
            service.delete( vaccine );
            loggerUtil.log( TransactionType.VACCINETYPE_DELETE, LoggerUtil.currentUser(),
                    "Deleted vaccine with id " + vaccine.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.VACCINETYPE_DELETE, LoggerUtil.currentUser(), "Failed to delete vaccine" );
            return new ResponseEntity( errorResponse( "Could not delete vaccine: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a collection of all the vaccines in the system.
     *
     * @return a list of all vaccines in the system.
     */
    @GetMapping ( BASE_PATH + "/vaccines" )
    public List<VaccineType> getVaccines () {
        loggerUtil.log( TransactionType.VACCINETYPE_VIEW, LoggerUtil.currentUser(), "Viewed a list of all vaccines" );
        return service.findAll();
    }

    /**
     * Returns a single vaccine using the given id.
     *
     * @param id
     *            the id of the desired vaccine
     * @return the requested vaccine
     */
    @GetMapping ( BASE_PATH + "/vaccines/{id}" )
    public ResponseEntity getVaccineType ( @PathVariable final Long id ) {
        final VaccineType v = service.findById( id );
        if ( v == null ) {
            loggerUtil.log( TransactionType.VACCINETYPE_VIEW, LoggerUtil.currentUser(),
                    "Failed to find vaccine with id " + id );
            return new ResponseEntity( errorResponse( "No vaccinie found for " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(), "Viewed vaccine  " + id );
            return new ResponseEntity( v, HttpStatus.OK );
        }
    }

}
