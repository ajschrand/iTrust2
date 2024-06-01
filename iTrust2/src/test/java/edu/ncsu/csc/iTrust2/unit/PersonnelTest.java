package edu.ncsu.csc.iTrust2.unit;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class PersonnelTest {

    @Autowired
    private PersonnelService    service;

    private static final String USER_1 = "demoTestUser1";

    private static final String USER_2 = "demoTestUser2";

    private static final String PW     = "123456";

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Transactional
    @Test
    public void testPersonnel () {

        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        final Personnel p1 = new Personnel( new UserForm( USER_1, PW, Role.ROLE_ADMIN, 1 ) );

        service.save( p1 );

        final List<Personnel> savedRecords = service.findAll();

        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a Personnel record should results in its creation in the DB" );

        Assertions.assertEquals( USER_1, savedRecords.get( 0 ).getUsername(),
                "Creating a Personnel record should results in its creation in the DB" );

        p1.setFirstName( "Rosa" );
        p1.setLastName( "Luxemburg" );

        service.save( p1 );

        final User userRecord = service.findByName( USER_1 );

        Assertions.assertEquals( USER_1, userRecord.getUsername() );

        Assertions.assertEquals( Personnel.class, userRecord.getClass() );

        final Personnel retrieved = (Personnel) userRecord;

        Assertions.assertEquals( "Rosa", retrieved.getFirstName() );

        try {
            final Personnel p2 = new Personnel( new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 ) );
            // Otherwise we get compilation warnings
            Assertions.assertNotNull( p2 );
            Assertions.fail( "Should not be able to create a Personnel from a Patient user" );
        }
        catch ( final Exception e ) {
            // expected
        }

    }
}
