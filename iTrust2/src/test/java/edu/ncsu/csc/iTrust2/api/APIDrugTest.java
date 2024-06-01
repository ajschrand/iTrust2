package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
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
import edu.ncsu.csc.iTrust2.forms.DrugForm;
import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.services.DrugService;

/**
 * Class for testing drug API.
 *
 * @author Connor
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIDrugTest {

    @Autowired
    private MockMvc     mvc;

    @Autowired
    private DrugService service;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Tests basic drug API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testDrugAPI () throws UnsupportedEncodingException, Exception {
        // Create drugs for testing
        final DrugForm form1 = new DrugForm();
        form1.setCode( "0000-0000-00" );
        form1.setName( "TEST1" );
        form1.setDescription( "DESC1" );

        final DrugForm form2 = new DrugForm();
        form2.setCode( "0000-0000-01" );
        form2.setName( "TEST2" );
        form2.setDescription( "Desc2" );

        // Add drug1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Drug object
        final Drug drug1 = TestUtils.gson().fromJson( content1, Drug.class );
        Assertions.assertEquals( form1.getCode(), drug1.getCode() );
        Assertions.assertEquals( form1.getName(), drug1.getName() );
        Assertions.assertEquals( form1.getDescription(), drug1.getDescription() );

        // Attempt to add same drug twice
        mvc.perform( post( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );

        // Add drug2 to system
        final String content2 = mvc
                .perform( post( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Drug drug2 = TestUtils.gson().fromJson( content2, Drug.class );
        Assertions.assertEquals( form2.getCode(), drug2.getCode() );
        Assertions.assertEquals( form2.getName(), drug2.getName() );
        Assertions.assertEquals( form2.getDescription(), drug2.getDescription() );

        // Verify drugs have been added
        mvc.perform( get( "/api/v1/drugs" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( form1.getCode() ) ) )
                .andExpect( content().string( Matchers.containsString( form2.getCode() ) ) );

        // Edit first drug's description
        drug1.setDescription( "This is a better description" );
        final String editContent = mvc
                .perform( put( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( drug1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Drug editedDrug = TestUtils.gson().fromJson( editContent, Drug.class );
        Assertions.assertEquals( drug1.getId(), editedDrug.getId() );
        Assertions.assertEquals( drug1.getCode(), editedDrug.getCode() );
        Assertions.assertEquals( drug1.getName(), editedDrug.getName() );
        Assertions.assertEquals( "This is a better description", editedDrug.getDescription() );

        // Attempt invalid edit
        drug2.setCode( "0000-0000-00" );
        mvc.perform( put( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug2 ) ) ).andExpect( status().isConflict() );

        // Follow up with valid edit
        drug2.setCode( "0000-0000-03" );
        mvc.perform( put( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug2 ) ) ).andExpect( status().isOk() );

    }

}
