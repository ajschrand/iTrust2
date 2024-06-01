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
import edu.ncsu.csc.iTrust2.forms.ICDCodeForm;
import edu.ncsu.csc.iTrust2.models.ICDCode;

/**
 * Class to test that ICDCode and ICDCodeForms are created from each other
 * properly.
 *
 * @author Thomas
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class ICDCodeTest {

    @Test
    @Transactional
    public void testCodes () {
        final ICDCodeForm form = new ICDCodeForm();
        form.setId( 1L );
        form.setCode( "T11" );
        form.setDescription( "Testing" );
        final ICDCode base = new ICDCode();
        base.setCode( "T11" );
        base.setDescription( "Testing" );
        base.setId( 1L );

        final ICDCode code = new ICDCode( form );
        Assertions.assertEquals( code, base );

        final ICDCodeForm f2 = new ICDCodeForm( code );
        Assertions.assertEquals( form, f2 );

        Assertions.assertEquals( "T11", code.getCode() );
        Assertions.assertTrue( code.getId().equals( 1L ) );
        Assertions.assertEquals( "Testing", code.getDescription() );

    }

    @Test
    @Transactional
    public void testCodesOphthalmology () {
        final ICDCodeForm form = new ICDCodeForm();
        form.setId( 1L );
        form.setCode( "T11" );
        form.setDescription( "Testing" );
        form.setIsOphthalmology( true );

        final ICDCode base = new ICDCode();
        base.setCode( "T11" );
        base.setDescription( "Testing" );
        base.setId( 1L );
        base.setIsOphthalmology( true );

        final ICDCode code = new ICDCode( form );
        Assertions.assertEquals( code, base );
        Assertions.assertEquals( code.hashCode(), base.hashCode() );

        final ICDCodeForm f2 = new ICDCodeForm( code );
        Assertions.assertEquals( form, f2 );
        Assertions.assertEquals( form.hashCode(), f2.hashCode() );

        Assertions.assertEquals( "T11", code.getCode() );
        Assertions.assertTrue( code.getId().equals( 1L ) );
        Assertions.assertEquals( "Testing", code.getDescription() );
        Assertions.assertTrue( code.getIsOphthalmology() );
        Assertions.assertTrue( f2.getIsOphthalmology() );

    }

    @Test
    @Transactional
    public void testInvalidCodes () {
        final ICDCodeForm form = new ICDCodeForm();
        form.setCode( "111" );
        form.setDescription( "Invalid" );

        @SuppressWarnings ( "unused" ) // we want to do an assignment so that
                                       // the compiler doesn't optimize it away
        ICDCode code;
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Code must begin with a capital letter: 111", e.getMessage() );
        }
        form.setCode( "AA1" );
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Second character of code must be a digit: AA1", e.getMessage() );
        }
        form.setCode( "A1.A" );
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Third character of code must be alphanumeric: A1.A", e.getMessage() );
        }
        form.setCode( "A11A" );
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Fourth character of code must be decimal: A11A", e.getMessage() );
        }
        form.setCode( "A11.." );
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Characters after decimal must be alphanumeric: A11..", e.getMessage() );
        }
        form.setCode( "A1" );
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Code must be at least three characters: A1", e.getMessage() );
        }
        form.setCode( "A11.1AA1A" );
        try {
            code = new ICDCode( form );
            Assertions.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( "Code too long! Max 8 characters including decimal: A11.1AA1A", e.getMessage() );
        }
        form.setCode( "A11.111A" );
        // valid
        code = new ICDCode( form );

    }
}
