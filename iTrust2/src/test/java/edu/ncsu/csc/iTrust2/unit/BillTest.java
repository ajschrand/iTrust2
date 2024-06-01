package edu.ncsu.csc.iTrust2.unit;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Class to test that Bill are created properly
 *
 * @author nhwiblit
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class BillTest {

    /**  */
    @Autowired
    private BillService       billService;

    @Autowired
    private UserService<User> userService;

    @Autowired
    private CPTCodeService    cptCodeService;

    @Test
    @Transactional
    public void testBill () {
        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );

        userService.saveAll( List.of( hcp, alice ) );

        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( 99202 );
        form.setDescription( "Testing" );
        form.setCost( 75 );
        form.setVersion( 0 );
        final CPTCode c = cptCodeService.build( form );
        System.out.println( c );
        cptCodeService.save( c );

        final Bill b = new Bill();
        b.setPatient( userService.findByName( "AliceThirteen" ) );
        b.setHcp( userService.findByName( "hcp" ) );

        final ZonedDateTime t = ZonedDateTime.now();
        b.setDate( t );

        b.setCptCodes( cptCodeService.findAll() );

        final List<Payment> p = new ArrayList<Payment>();
        b.setPaymentList( p );

        b.setAmountPaid( 0 );
        b.setRemainingCost( cptCodeService.findAll().get( 0 ).getCost() );
        b.setId( null );
        b.setStatus( BillStatus.UNPAID );

        final OfficeVisitForm ovf = new OfficeVisitForm();
        // ovf.setCPTCodes( List.of( base ) );
        ovf.setDate( t.toString() );
        ovf.setHcp( "hcp" );
        ovf.setPatient( "AliceThirteen" );
        ovf.setId( "1" );
        ovf.setCptCodes( List.of( cptCodeService.findByCode( 99202 ).getId() ) );

        final Bill b2 = billService.build( ovf );

        Assertions.assertEquals( b.getAmountPaid(), b2.getAmountPaid() );
        // assertEquals( b.getCptCodes(), b2.getCptCodes() );
        Assertions.assertEquals( b.getDate(), b2.getDate() );
        Assertions.assertEquals( b.getHcp(), b2.getHcp() );
        Assertions.assertEquals( b.getId(), b2.getId() );
        Assertions.assertEquals( b.getPatient(), b2.getPatient() );
        Assertions.assertEquals( b.getPaymentList(), b2.getPaymentList() );
        Assertions.assertEquals( b.getRemainingCost(), b2.getRemainingCost() );
        Assertions.assertEquals( b.getStatus(), b2.getStatus() );

    }

}
