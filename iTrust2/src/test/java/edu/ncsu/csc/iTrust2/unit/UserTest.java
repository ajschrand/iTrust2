package edu.ncsu.csc.iTrust2.unit;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for user model
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class UserTest {

    @Autowired
    private UserService<User>   service;

    private static final String USER_1 = "testUser1";

    private static final String USER_2 = "testUser2";

    private static final String USER_3 = "testUser3";

    private static final String PW     = "123456";

    /**
     * Set up
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * tests the user roles
     */
    @Test
    @Transactional
    public void testUserRoles () {

        Assertions.assertEquals( 0, service.count(), "There should be no users in the system" );

        final User user1 = new Personnel( new UserForm( USER_1, PW, Role.ROLE_HCP, 1 ) );

        service.save( user1 );

        Assertions.assertEquals( 1, service.count(), "Creating a user should result in its presence in the database" );

        user1.addRole( Role.ROLE_ER );

        service.save( user1 );

        Assertions.assertEquals( 1, service.count(),
                "Giving a user a second role should still result in just a single user" );

        Assertions.assertEquals( 2, service.findByName( USER_1 ).getRoles().size(),
                "A user with two roles should be retrieved with two roles" );

        final User user2 = new Patient( new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 ) );

        User user3 = new Personnel( new UserForm( USER_3, PW, Role.ROLE_LABTECH, 1 ) );
        service.saveAll( List.of( user2, user3 ) );

        Assertions.assertEquals( 3, service.count(), "Creating multiple users should save them as expected" );

        Assertions.assertFalse( user3.isDoctor(), "A LabTech should not be a Doctor by default" );

        user3 = service.findByName( USER_3 );

        user3.addRole( Role.ROLE_HCP );

        Assertions.assertTrue( user3.isDoctor(), "A user with multiple roles should identify as a Doctor properly" );

    }

    /**
     * Tests illegal role combinations (ie. admin and any other role)
     */
    @Test
    @Transactional
    public void testIllegalRoleCombinations () {
        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_ADMIN, 1 );
            uf.addRole( Role.ROLE_LABTECH.toString() );

            final User user2 = new Personnel( uf );

            // Otherwise we get compilation warnings
            Assertions.assertNotNull( user2 );

            Assertions.fail( "It should not be possible to create an Admin with a secondary role!" );
        }
        catch ( final Exception e ) {
            // expected
        }

        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_ADMIN, 1 );
            final User user2 = new Personnel( uf );

            user2.addRole( Role.ROLE_ER );

            Assertions.fail( "It should not be possible to add another Role to an Admin user!" );
        }
        catch ( final Exception e ) {
            // expected
        }

        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_ER, 1 );
            final User user2 = new Personnel( uf );

            user2.addRole( Role.ROLE_ADMIN );

            Assertions.fail( "It should not be possible to add the Admin role to any user!" );
        }
        catch ( final Exception e ) {
            // expected
        }

        // added test
        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_PATIENTADVOCATE, 1 );
            final User user2 = new Personnel( uf );

            user2.addRole( Role.ROLE_HCP );

            Assertions.fail( "It should not be possible to add the HCP role to a Patient Advocate!" );
        }
        catch ( final Exception e ) {
            // expected
        }
    }
}
