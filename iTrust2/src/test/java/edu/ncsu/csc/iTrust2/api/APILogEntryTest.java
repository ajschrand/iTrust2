package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.controllers.api.comm.LogEntryRequestBody;
import edu.ncsu.csc.iTrust2.controllers.api.comm.LogEntryTableRow;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.security.LogEntryService;

/**
 * Class for testing logs API.
 *
 * @author Bruno Volpato
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APILogEntryTest {

    @Autowired
    private MockMvc           mvc;

    @Autowired
    private LogEntryService   service;

    @Autowired
    private UserService<User> userService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();

        final User admin = new Personnel( new UserForm( "admin", "123456", Role.ROLE_ADMIN, 1 ) );
        userService.save( admin );
    }

    /**
     * Tests basic log functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testLogQueryAPI () throws UnsupportedEncodingException, Exception {

        final LogEntry logEntry = new LogEntry();
        logEntry.setLogCode( TransactionType.LOGIN_SUCCESS );
        logEntry.setPrimaryUser( "admin" );
        logEntry.setMessage( "Logged In" );
        logEntry.setTime( ZonedDateTime.now() );

        service.save( logEntry );

        Assertions.assertNotNull( logEntry.getId() );

        final LogEntryRequestBody body = new LogEntryRequestBody();
        body.setStartDate( "2020-01-01T00:00:00Z" );
        body.setEndDate( "2099-12-31T23:59:59Z" );
        body.setPageLength( 10 );
        body.setPage( 1 );

        // Fetch logs
        final String content1 = mvc
                .perform( post( "/api/v1/logentries/range" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( body ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Drug object
        final List<LogEntryTableRow> entries = TestUtils.gson().fromJson( content1,
                new TypeToken<ArrayList<LogEntryTableRow>>() {
                }.getType() );
        Assertions.assertNotNull( entries );
        Assertions.assertEquals( 1, entries.size() );
        Assertions.assertEquals( "Successful login", entries.get( 0 ).getTransactionType() );

    }

}
