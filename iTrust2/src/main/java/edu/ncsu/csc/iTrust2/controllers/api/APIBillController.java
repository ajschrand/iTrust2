package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.forms.VaccineVisitForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PaymentService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Bill API Controller
 *
 * @author bswalia and mhyun
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIBillController extends APIController {

    /**
     * LoggerUtil
     */
    @Autowired
    private LoggerUtil                        loggerUtil;

    /**
     * Service for Bills
     */
    @Autowired
    private BillService                       billService;

    /**
     * Service for User
     */
    @Autowired
    private UserService                       userService;

    /**
     * Service for Associations
     */
    @Autowired
    private PatientAdvocateAssociationService associationService;

    /**
     * Service for Payments
     */
    @Autowired
    PaymentService                            paymentService;

    /**
     * Retrieves a list of all the Bills in the database
     *
     * @param username
     *            Username of the patient
     * @return list of bills
     */
    @GetMapping ( BASE_PATH + "/bills/{username}" )
    @PreAuthorize ( "hasAnyRole('ROLE_BSM')" )
    public List<Bill> getBills ( @PathVariable final String username ) {
        final User patient = userService.findByName( username );
        loggerUtil.log( TransactionType.BILLS_VIEWED, username );
        return billService.findByPatient( patient );
    }

    /**
     * Retrieves a list of all OfficeVisits in the database for the current
     * patient
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/bills/mybills" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public List<Bill> getMyBills () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.BILLS_VIEWED, self );
        return billService.findByPatient( self );
    }

    /**
     * Gets a Patient's bills for a PatientAdvocate. Can only be called by an
     * advocate. Ensures that the advocate has the required permissions
     *
     * @param username
     *            the username of the patient whose bills you are viewing
     * @return a ResponseEntity with the status and the list of bills, if the
     *         get was successful
     */
    @GetMapping ( BASE_PATH + "/bills/patient/{username}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getBillsByPatient ( @PathVariable final String username ) {
        final User patient = userService.findByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No patient found with username: " + username ),
                    HttpStatus.NOT_FOUND );
        }

        final User advocate = userService.findByName( LoggerUtil.currentUser() );
        final PatientAdvocateAssociation association = associationService.getAssociation( patient.getUsername(),
                advocate.getUsername() );
        if ( association == null || !association.getBilling() ) {
            return new ResponseEntity( errorResponse( "You do not have permission to view this information." ),
                    HttpStatus.FORBIDDEN );
        }

        loggerUtil.log( TransactionType.PA_VIEW_BILLING, username );
        return new ResponseEntity( billService.findByPatient( patient ), HttpStatus.OK );
    }

    /**
     * Edits a Bill with the passed in PaymentForm
     *
     * @param id
     *            The id of the Bill that is being edited
     * @param pay
     *            The Payment that was made to the Bill
     * @return ResponseEntity OK if the Bill was edited, NOT_FOUND if there was
     *         no Bill with that id, and BAD_REQUEST if there was an error
     */
    @PutMapping ( BASE_PATH + "/bills/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_BSM')" )
    public ResponseEntity editBills ( @PathVariable final Long id, @RequestBody final PaymentForm pay ) {
        try {
            final Bill b = billService.findById( id );
            final Payment p = new Payment( pay );

            paymentService.save( p );

            if ( b == null ) {
                return new ResponseEntity( errorResponse( "No Bill found" ), HttpStatus.NOT_FOUND );
            }
            b.updateBill( p );
            billService.save( b );
            loggerUtil.log( TransactionType.BILL_PAID, LoggerUtil.currentUser() );
            return new ResponseEntity( b, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the Bill provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Creates a Bill from an office visit form and then saves that Bill to a
     * Patient
     *
     * @param form
     *            The office visit form that contains all the necessary
     *            information to make a Bill
     * @return ResponseEntity 200 If the Bill was created, Bad_Request if there
     *         was an error
     */
    @PostMapping ( BASE_PATH + "bills/ov" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity createBillOfficeVisit ( @RequestBody final OfficeVisitForm form ) {
        try {
            form.setHcp( LoggerUtil.currentUser() );
            final Bill b = billService.build( form );

            billService.save( b );
            loggerUtil.log( TransactionType.BILL_CREATED_OV, LoggerUtil.currentUser() );
            return new ResponseEntity( b, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the Bill provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Creates a Bill from an vaccine visit form and then saves that Bill to a
     * Patient
     *
     * @param form
     *            The vaccine visit form that contains all the necessary
     *            information to make a Bill
     * @return ResponseEntity 200 If the Bill was created, Bad_Request if there
     *         was an error
     */
    @PostMapping ( BASE_PATH + "bills/vv" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_VACCINATOR')" )
    public ResponseEntity createBillVaccineVisit ( @RequestBody final VaccineVisitForm form ) {
        try {
            form.setVaccinator( LoggerUtil.currentUser() );
            final Bill b = billService.build( form );
            billService.save( b );
            loggerUtil.log( TransactionType.BILL_CREATED_VV, LoggerUtil.currentUser() );
            return new ResponseEntity( b, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the Bill provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
