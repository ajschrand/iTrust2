package edu.ncsu.csc.iTrust2.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineAppointmentRequest;
import edu.ncsu.csc.iTrust2.models.VaccineType;
import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Tests the vaccine appointment request model
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class VaccineAppointmentRequestTest {
    /**
     * Tests setting up a correct vaccine appointment request
     */
    @Test
    public void testVaccineAppointmentRequest () {
        final VaccineAppointmentRequest app = new VaccineAppointmentRequest();
        final User user1 = new Personnel( new UserForm( "abc", "123", Role.ROLE_VACCINATOR, 1 ) );
        app.setHcp( user1 );
        final VaccineType vacc = new VaccineType();
        app.setVaccine( vacc );
        Assertions.assertEquals( app.getHcp(), user1 );
        Assertions.assertEquals( app.getVaccine(), vacc );
    }
}
