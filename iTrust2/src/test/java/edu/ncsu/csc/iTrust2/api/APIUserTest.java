package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for API functionality for interacting with Users.
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIUserTest {

    private static final String USER_1 = "API_USER_1";

    private static final String USER_2 = "API_USER_2";

    private static final String USER_3 = "API_USER_3";

    private static final String PW     = "123456";

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    @Autowired
    private MockMvc             mvc;

    @Autowired
    private UserService<User>   service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Tests creating users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreateUsers () throws Exception {

        Assertions.assertEquals( 0, service.count(), "There should be no Users in the system" );

        final UserForm u = new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 1, service.count(), "There should be one user in the system after creating a User" );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_HCP, 1 );

        u2.addRole( Role.ROLE_VIROLOGIST.toString() );
        u2.addRole( Role.ROLE_OPH.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 2, service.count(), "It should be possible to create a user with multiple roles" );

        final User retrieved = service.findByName( USER_2 );

        Assertions.assertNotNull( retrieved, "The created user should be retrievable from the database" );

        Assertions.assertEquals( Personnel.class, retrieved.getClass(), "The retrieved user should be a Personnel" );

        Assertions.assertEquals( 3, retrieved.getRoles().size(), "The retrieved user should have 3 roles" );

        final UserForm u3 = new UserForm( USER_3, PW, Role.ROLE_VACCINATOR, 1 );
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u3 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final User vaccinator = service.findByName( USER_3 );
        Assertions.assertNotNull( vaccinator, "The created user should be retrievable from the database" );

        Assertions.assertEquals( Personnel.class, vaccinator.getClass(), "The retrieved user should be a Personnel" );

        Assertions.assertEquals( 1, vaccinator.getRoles().size(), "The retrieved user should have 1 role" );

    }

    /**
     * Tests creating invalid users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testCreateInvalidUsers () throws Exception {

        final UserForm u1 = new UserForm( USER_1, PW, Role.ROLE_ADMIN, 1 );

        u1.addRole( Role.ROLE_ER.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u1 ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assertions.assertEquals( 0, service.count(),
                "Trying to create an invalid user should not create any User record" );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 );

        u2.addRole( Role.ROLE_HCP.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assertions.assertEquals( 0, service.count(),
                "Trying to create an invalid user should not create any User record" );

    }

    /**
     * Tests updating users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateUsers () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );

        final User u1 = new Personnel( uf );

        service.save( u1 );

        Assertions.assertEquals( u1.getUsername(), service.findByName( USER_1 ).getUsername() );

        uf.addRole( Role.ROLE_ER.toString() );
        uf.addRole( Role.ROLE_VACCINATOR.toString() );

        mvc.perform( MockMvcRequestBuilders.put( "/api/v1/users/" + uf.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( uf ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final User retrieved = service.findByName( USER_1 );

        Assertions.assertEquals( 3, retrieved.getRoles().size(), "Updating a user should give them additional Roles" );

    }

    /**
     * Gets deleted users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testRetrieveDeleteUsers () throws Exception {
        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );

        final User u1 = new Personnel( uf );

        service.save( u1 );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users/Lodewijk" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users/" + USER_1 ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/Lodewijk" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isNotFound() );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/" + USER_1 ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 0, service.count(), "Deleting a user should really delete them" );

    }

    /**
     * Tests the roles
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testRole () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "ROLE_ADMIN" ) );

    }

    /**
     * Tests unauthroized users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testRoleUnauthorised () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isUnauthorized() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "UNAUTHORIZED" ) );

    }

}
