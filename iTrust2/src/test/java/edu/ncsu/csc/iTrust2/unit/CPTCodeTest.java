package edu.ncsu.csc.iTrust2.unit;

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
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;

/**
 * Class to test that CPTCode and CPTCodeForms are created from each other
 * properly.
 *
 * @author yli246
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
class CPTCodeTest {

    @Autowired
    private CPTCodeService CPTCodeService;

    @Test
    @Transactional
    public void testCodes () {
        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( 99202 );
        form.setDescription( "Testing" );
        form.setCost( 75 );
        form.setId( 1L );
        form.setVersion( 0 );

        final CPTCode base = new CPTCode();
        base.setCode( 99202 );
        base.setDescription( "Testing" );
        base.setCost( 75 );
        base.setId( 1L );
        base.setVersion( 0 );

        final CPTCode code = CPTCodeService.build( form );
        Assertions.assertEquals( code, base );
        Assertions.assertEquals( code.hashCode(), base.hashCode() );

        final CPTCodeForm f2 = new CPTCodeForm( code );
        Assertions.assertEquals( form, f2 );
        Assertions.assertEquals( form.hashCode(), f2.hashCode() );

        Assertions.assertEquals( 99202, code.getCode() );
        Assertions.assertTrue( code.getId().equals( 1L ) );
        Assertions.assertEquals( "Testing", code.getDescription() );
        Assertions.assertEquals( 0, code.getVersion() );
    }

    @Test
    @Transactional
    public void testCodesIsActive () {
        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( 99202 );
        form.setDescription( "Testing" );
        form.setCost( 75 );
        form.setId( 1L );
        form.setVersion( 0 );
        form.setisActive( true );

        final CPTCode base = new CPTCode();
        base.setCode( 99202 );
        base.setDescription( "Testing" );
        base.setCost( 75 );
        base.setId( 1L );
        base.setVersion( 0 );
        base.setisActive( true );

        final CPTCode code = CPTCodeService.build( form );
        Assertions.assertEquals( code, base );
        Assertions.assertEquals( code.hashCode(), base.hashCode() );

        final CPTCodeForm f2 = new CPTCodeForm( code );
        Assertions.assertEquals( form, f2 );
        Assertions.assertEquals( form.hashCode(), f2.hashCode() );

        Assertions.assertEquals( 99202, code.getCode() );
        Assertions.assertTrue( code.getId().equals( 1L ) );
        Assertions.assertEquals( "Testing", code.getDescription() );
        Assertions.assertEquals( 0, code.getVersion() );
        Assertions.assertTrue( code.getisActive() );
        Assertions.assertTrue( form.getisActive() );
    }

    @Test
    @Transactional
    public void testInvalidCodes () {
        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( 100 );
        form.setDescription( "Invalid" );
        @SuppressWarnings ( "unused" )
        CPTCode code;

        try {
            code = CPTCodeService.build( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Code must be a 5 digit number: 100", e.getMessage() );
        }

        form.setDescription(
                "InvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalid" );
        try {
            code = CPTCodeService.build( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals(
                    "Description too long (250 characters max): InvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalidInvalid",
                    e.getMessage() );
        }
    }
}
