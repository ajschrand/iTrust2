package edu.ncsu.csc.iTrust2.unit;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.enums.PaymentType;

/**
 * Class to test that Payment and PaymentForms are created from each other
 * properly.
 *
 * @author yli246
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
class PaymentTest {

    @Test
    @Transactional
    public void testPayment () {
        final PaymentForm form = new PaymentForm();
        form.setAmount( 10 );
        form.setId( 1L );
        form.setType( PaymentType.CASH.toString() );

        final Payment base = new Payment();
        base.setAmount( 10 );
        base.setId( 1L );
        base.setPaymentType( PaymentType.CASH );

        final Payment payment = new Payment( form );
        payment.setId( 1L );
        Assertions.assertEquals( payment.getAmount(), base.getAmount() );

        final PaymentForm f2 = new PaymentForm( payment );
        Assertions.assertEquals( form.getAmount(), f2.getAmount() );

        Assertions.assertEquals( 10, payment.getAmount() );
        Assertions.assertTrue( payment.getId().equals( 1L ) );
        Assertions.assertEquals( PaymentType.CASH, payment.getPaymentType() );
    }

}
