package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.ICDCodeForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.ICDCodeService;
import edu.ncsu.csc.iTrust2.services.UserService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIICDCodeTest {

    @Autowired
    private MockMvc           mvc;

    @Autowired
    private ICDCodeService    service;

    @Autowired
    private UserService<User> userService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () throws Exception {
        service.deleteAll();
        userService.deleteAll();

        userService.save( new Personnel( new UserForm( "admin", "admin", Role.ROLE_ADMIN, 1 ) ) );
        userService.save( new Personnel( new UserForm( "hcp", "hcp", Role.ROLE_HCP, 1 ) ) );
        userService.save( new Personnel( new UserForm( "oph", "oph", Role.ROLE_OPH, 1 ) ) );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testCodeAPI () throws Exception {
        service.deleteAll();
        final ICDCodeForm form = new ICDCodeForm();
        form.setCode( "T12" );
        form.setDescription( "Test Code" );

        String content = mvc.perform( post( "/api/v1/icdcodes" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( form ) ) ).andReturn()
                .getResponse().getContentAsString();
        ICDCodeForm code = TestUtils.gson().fromJson( content, ICDCodeForm.class );
        form.setId( code.getId() ); // fill in the id of the code we just
                                    // created
        Assertions.assertEquals( form, code );

        content = mvc.perform( get( "/api/v1/icdcode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        code = TestUtils.gson().fromJson( content, ICDCodeForm.class );
        Assertions.assertEquals( form, code );

        // edit it
        form.setCode( "T13" );
        content = mvc
                .perform( put( "/api/v1/icdcode/" + form.getId() ).with( csrf() )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( form ) ) )
                .andReturn().getResponse().getContentAsString();
        code = TestUtils.gson().fromJson( content, ICDCodeForm.class );
        Assertions.assertEquals( form, code );
        content = mvc.perform( get( "/api/v1/icdcode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        code = TestUtils.gson().fromJson( content, ICDCodeForm.class );
        Assertions.assertEquals( form, code );

        // then delete it and check that its gone.
        mvc.perform(
                delete( "/api/v1/icdcode/" + form.getId() ).with( csrf() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        assertNull( service.findById( form.getId() ) );
        mvc.perform( get( "/api/v1/icdcode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "ADMIN", "HCP" } )
    public void testCodeAPIWithHCP () throws Exception {
        ICDCodeForm form = new ICDCodeForm();
        form.setCode( "T12" );
        form.setDescription( "Test Code" );
        form.setIsOphthalmology( false );
        String saveContent = mvc.perform( post( "/api/v1/icdcodes" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( form ) ) ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertNotNull( saveContent );

        String getContent = mvc.perform( get( "/api/v1/icdcodes" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertNotNull( getContent );

        form = new ICDCodeForm();
        form.setCode( "T13" );
        form.setDescription( "Test Code" );
        form.setIsOphthalmology( true );

        saveContent = mvc.perform( post( "/api/v1/icdcodes" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();
        Assertions.assertNotNull( saveContent );

        getContent = mvc.perform( get( "/api/v1/icdcodes" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        Assertions.assertTrue( getContent.contains( "T12" ) );
        Assertions.assertFalse( getContent.contains( "T13" ) );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "oph", roles = { "ADMIN", "OPH" } )
    public void testCodeAPIWithOPH () throws Exception {
        ICDCodeForm form = new ICDCodeForm();
        form.setCode( "B12" );
        form.setDescription( "Test Code" );
        form.setIsOphthalmology( false );
        String saveContent = mvc.perform( post( "/api/v1/icdcodes" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( form ) ) ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertNotNull( saveContent );

        String getContent = mvc.perform( get( "/api/v1/icdcodes" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertNotNull( getContent );

        form = new ICDCodeForm();
        form.setCode( "B13" );
        form.setDescription( "Test Code" );
        form.setIsOphthalmology( true );

        saveContent = mvc.perform( post( "/api/v1/icdcodes" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();
        assertNotNull( saveContent );

        getContent = mvc.perform( get( "/api/v1/icdcodes" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        Assertions.assertTrue( getContent.contains( "B13" ) );
        Assertions.assertFalse( getContent.contains( "B12" ) );
    }
}
