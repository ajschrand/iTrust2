package edu.ncsu.csc.iTrust2.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.VaccineTypeForm;
import edu.ncsu.csc.iTrust2.models.VaccineType;

/**
 * Tests the vaccine type model
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class VaccineTypeTest {

    /**
     * Tests creating multiple vaccine types
     */
    @Test
    public void testVaccineType () {
        final VaccineType vt = new VaccineType();
        vt.setName( "Moderna" );
        vt.setId( (long) 123 );
        vt.setMinAge( 5 );
        vt.setNumDoses( 2 );
        vt.setDaysBetweenDoses( 10 );
        // vt.setAvailable( true );
        vt.setInventoryAmount( 1000 );

        Assertions.assertEquals( vt.getName(), "Moderna" );
        Assertions.assertEquals( (long) vt.getId(), (long) 123 );
        Assertions.assertEquals( vt.getMinAge(), 5 );
        Assertions.assertEquals( vt.getNumDoses(), 2 );
        Assertions.assertEquals( vt.getDaysBetweenDoses(), 10 );
        // assertEquals( vt.isAvailable(), true );
        Assertions.assertEquals( vt.getInventoryAmount(), 1000 );

        final VaccineTypeForm form = new VaccineTypeForm( vt );
        Assertions.assertEquals( vt.getName(), form.getName() );
        Assertions.assertEquals( (long) vt.getId(), (long) form.getId() );
        Assertions.assertEquals( vt.getMinAge(), form.getMinAge() );
        Assertions.assertEquals( vt.getNumDoses(), form.getNumDoses() );
        Assertions.assertEquals( vt.getDaysBetweenDoses(), form.getDaysBetweenDoses() );
        // assertEquals( vt.isAvailable(), form.isAvailable() );
        Assertions.assertEquals( vt.getInventoryAmount(), form.getInventoryAmount() );

        final VaccineType vt2 = new VaccineType( form );
        Assertions.assertEquals( vt.getName(), vt2.getName() );
        Assertions.assertEquals( (long) vt.getId(), (long) vt2.getId() );
        Assertions.assertEquals( vt.getMinAge(), vt2.getMinAge() );
        Assertions.assertEquals( vt.getNumDoses(), vt2.getNumDoses() );
        Assertions.assertEquals( vt.getDaysBetweenDoses(), vt2.getDaysBetweenDoses() );
        // assertEquals( vt.isAvailable(), vt2.isAvailable() );
        Assertions.assertEquals( vt.getInventoryAmount(), vt2.getInventoryAmount() );

    }
}
