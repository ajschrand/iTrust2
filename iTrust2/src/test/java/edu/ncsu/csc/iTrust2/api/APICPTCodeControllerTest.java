package edu.ncsu.csc.iTrust2.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
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
import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;

/**
 * Testing APICPTCCodeController endpoints
 *
 * @author mhyun, bswalia
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APICPTCodeControllerTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    @Autowired
    private MockMvc        mvc;

    /**
     * CPTCodeService service
     */
    @Autowired
    private CPTCodeService cptService;

    /**
     * Testing editing a cptCode
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "billingstaffmember", roles = { "BSM" } )
    @Transactional
    public void testEditCPTCode () throws Exception {
        final CPTCode cpt = new CPTCode();
        // Create CPTCode and save it
        cpt.setCode( 99202 );
        cpt.setCost( 50 );
        cpt.setDescription( "General Check up" );
        cpt.setisActive( true );
        cpt.setVersion( 1 );
        cptService.save( cpt );
        // Create a form with the new information and call the endpoint
        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( 99202 );
        form.setCost( 80 );
        form.setisActive( true );
        form.setDescription( "General check up" );
        mvc.perform( put( "/api/v1/cptcode/" + cpt.getId() ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );
        // Check to see that the changes were made
        final List<CPTCode> list = cptService.findAll();
        for ( int i = 0; i < list.size(); i++ ) {
            if ( list.get( i ).getisActive() ) {
                assertTrue( list.get( i ).getCode() == 99202 );
                assertTrue( list.get( i ).getCost() == 80 );
                assertTrue( list.get( i ).getDescription().equals( "General check up" ) );
                assertTrue( list.get( i ).getVersion() == 2 );
            }
            else {
                assertTrue( list.get( i ).getCode() == 99202 );
                assertTrue( list.get( i ).getCost() == 50 );
                assertTrue( list.get( i ).getVersion() == 1 );
                assertTrue( list.get( i ).getDescription().equals( "General Check up" ) );
            }
        }
        final CPTCodeForm form2 = new CPTCodeForm();
        form2.setCode( 99202 );
        form2.setCost( 80 );
        form2.setDescription( "General check up" );
        // Check to see that it does not update a code if it cannot find the
        // right code
        mvc.perform( put( "/api/v1/cptcode" + 0 ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form2 ) ) ).andExpect( status().isNotFound() );

        // testing for editing with new invalid code with 6 digit code
        final CPTCode cpt2 = new CPTCode();
        cpt2.setCode( 992021 );
        cpt2.setCost( 50 );
        cpt2.setDescription( "General Check up" );
        cpt2.setisActive( true );
        cpt2.setVersion( 1 );
        cptService.save( cpt2 );
        final CPTCodeForm form3 = new CPTCodeForm();
        form3.setCode( 992021 );
        form3.setCost( 80 );
        form3.setisActive( true );
        form3.setDescription( "General check up" );
        mvc.perform( put( "/api/v1/cptcode/" + cpt2.getId() ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form3 ) ) ).andExpect( status().isBadRequest() );

    }

    /**
     * Testing deleting a cptCode
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "billingstaffmember", roles = { "BSM" } )
    @Transactional
    public void testDeleteCPTCode () throws Exception {
        final CPTCode cpt = new CPTCode();
        // Create CPTCode and save it, then call the endpoint to delete it
        cpt.setCode( 99202 );
        cpt.setCost( 50 );
        cpt.setDescription( "General Check up" );
        cpt.setisActive( true );
        cpt.setVersion( 1 );
        cptService.save( cpt );

        mvc.perform( delete( "/api/v1/cptcode/" + cpt.getId() ).with( csrf() ) ).andExpect( status().isOk() );
        // Check to see that the changes were made
        final List<CPTCode> list = cptService.findAll();
        final CPTCode retrieved = list.get( 0 );
        Assertions.assertEquals( 99202, retrieved.getCode() );
        Assertions.assertEquals( 50, retrieved.getCost() );
        Assertions.assertEquals( "General Check up", retrieved.getDescription() );
        Assertions.assertFalse( retrieved.getisActive() );
        // Check to see that it does not delete a code if it does not exist
        mvc.perform( delete( "/api/v1/cptcode/99203" ).with( csrf() ) ).andExpect( status().isNotFound() );
        final CPTCodeForm cpt2 = new CPTCodeForm();
        // Create CPTCode and save it, then call the endpoint to delete it
        cpt2.setCode( 99202 );
        cpt2.setCost( 50 );
        cpt2.setDescription( "General Check up" );
        cpt2.setisActive( true );
        cpt2.setVersion( 1 );
        mvc.perform( post( "/api/v1/cptcode" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cpt2 ) ) ).andExpect( status().isOk() );
        final List<CPTCode> list2 = cptService.findAll();
        Assertions.assertEquals( 2, list2.get( 1 ).getVersion() );

    }

    /**
     * Testing creating a cptCode
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "billingstaffmember", roles = { "BSM" } )
    @Transactional
    public void testCreateCPTCode () throws Exception {
        final CPTCodeForm cpt = new CPTCodeForm();
        // Create CPTCode and save it, then call the endpoint to delete it
        cpt.setCode( 99202 );
        cpt.setCost( 50 );
        cpt.setDescription( "General Check up" );
        cpt.setisActive( true );
        cpt.setVersion( 1 );
        final CPTCodeForm cpt2 = new CPTCodeForm();
        // Create CPTCode and save it, then call the endpoint to delete it
        cpt2.setCode( 99203 );
        cpt2.setCost( 50 );
        cpt2.setDescription( "Special Check up" );
        cpt2.setisActive( true );
        cpt2.setVersion( 1 );
        // cptService.save( cpt );

        mvc.perform( post( "/api/v1/cptcode" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cpt ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cptcode" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cpt2 ) ) ).andExpect( status().isOk() );
        // list full of cpt codes as a string
        final String strList = mvc.perform( get( "/api/v1/cptcodes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( strList.contains( "99202" ) );
        Assertions.assertTrue( strList.contains( "50" ) );
        Assertions.assertTrue( strList.contains( "General Check up" ) );
        Assertions.assertTrue( strList.contains( "99203" ) );
        Assertions.assertTrue( strList.contains( "50" ) );
        Assertions.assertTrue( strList.contains( "Special Check up" ) );
        final List<CPTCode> list = cptService.findAll();
        // list full of all cpt codes
        final String cptList = mvc
                .perform( get( "/api/v1/cptcode/" + list.get( 0 ).getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        // check to see if the list of cpt codes have the newly added cpt Code
        Assertions.assertTrue( cptList.contains( "99202" ) );
        Assertions.assertTrue( cptList.contains( "50" ) );
        Assertions.assertTrue( cptList.contains( "General Check up" ) );

        // testing for creating an invalid cptCode with 6 digit code
        final CPTCode cpt3 = new CPTCode();
        cpt3.setCode( 992025 );
        cpt3.setCost( 50 );
        cpt3.setDescription( "General Check up" );
        cpt3.setisActive( true );
        cpt3.setVersion( 1 );
        // cptService.save( cpt3 );
        mvc.perform( post( "/api/v1/cptcode" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cpt3 ) ) ).andExpect( status().isBadRequest() );
    }
}
