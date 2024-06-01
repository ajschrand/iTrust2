package edu.ncsu.csc.iTrust2.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.VaccineVisitForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.repositories.BillRepository;

/**
 * Service class for interacting with Bill model, performing CRUD tasks with
 * database and building a persistence object from a Form.
 *
 * @author nhwiblit
 *
 */
@Component
@Transactional
public class BillService extends Service<Bill, Long> {
    /**
     * Repository for CRUD operations
     */
    @Autowired
    private BillRepository    repository;

    /**
     * User service
     */
    @Autowired
    private UserService<User> userService;

    /**
     * User service
     */
    @Autowired
    private CPTCodeService    cptService;

    @Override
    protected JpaRepository<Bill, Long> getRepository () {
        return repository;
    }

    /**
     * Finds all Bills for the specified Patient
     *
     * @param patient
     *            Patient to search for
     * @return Matching Bills
     */
    public List<Bill> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

    @Override
    public List<Bill> findAll () {
        return repository.findAll();
    }

    /**
     * Builds a Bill based on the deserialized OfficeVisitForm
     *
     * @param ovf
     *            The office visit that the bill is being created for
     * @return Constructed Bill
     */
    public Bill build ( final OfficeVisitForm ovf ) {
        final Bill b = new Bill();
        b.setPatient( userService.findByName( ovf.getPatient() ) );
        b.setHcp( userService.findByName( ovf.getHcp() ) );

        final ZonedDateTime date = ZonedDateTime.parse( ovf.getDate() );
        b.setDate( date );

        b.setStatus( BillStatus.UNPAID );
        b.setAmountPaid( 0 );

        double cost = 0;
        final List<CPTCode> cptCodes = new ArrayList<CPTCode>();
        final List<Long> cptStrings = ovf.getCptCodes();
        for ( int i = 0; i < cptStrings.size(); i++ ) {
            cptCodes.add( cptService.findById( cptStrings.get( i ) ) );
        }
        for ( final CPTCode c : cptCodes ) {
            cost += c.getCost();
        }
        b.setRemainingCost( cost );
        b.setCptCodes( cptCodes );

        final List<Payment> paymentList = new ArrayList<Payment>();
        b.setPaymentList( paymentList );

        return b;
    }

    /**
     * Builds a Bill based on the deserialized vaccineVisitForm
     *
     * @param vv
     *            The vaccine visit that the bill is being created for
     * @return Constructed Bill
     */
    public Bill build ( final VaccineVisitForm vv ) {
        final Bill b = new Bill();

        b.setPatient( userService.findByName( vv.getPatient() ) );
        b.setHcp( userService.findByName( vv.getVaccinator() ) );

        final ZonedDateTime date = ZonedDateTime.parse( vv.getDateTime() );
        b.setDate( date );

        b.setStatus( BillStatus.UNPAID );
        b.setAmountPaid( 0 );

        double cost = 0;
        final List<CPTCode> cptCodes = new ArrayList<CPTCode>();
        final List<Long> cptStrings = vv.getCptCodes();
        for ( int i = 0; i < cptStrings.size(); i++ ) {
            cptCodes.add( cptService.findById( cptStrings.get( i ) ) );
        }
        for ( final CPTCode c : cptCodes ) {
            cost += c.getCost();
        }
        b.setRemainingCost( cost );
        b.setCptCodes( cptCodes );

        final List<Payment> paymentList = new ArrayList<Payment>();
        b.setPaymentList( paymentList );

        return b;
    }
}
