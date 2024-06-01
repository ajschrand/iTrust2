package edu.ncsu.csc.iTrust2.controllers;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for Personnel to edit their information
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class UserController {

    /**
     * Controller for iTrust2 personnel to modify their demographics.
     * The @PreAuthorize tag will have to be extended if new classes of users
     * are added to the system
     *
     * @param model
     *            information about the vdw
     * @return response
     */
    @GetMapping ( value = "personnel/editDemographics" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN')" )
    public String viewDemographics ( final Model model ) {
        return "/personnel/editDemographics";
    }

    /**
     * Serves back our hero, Dr. Leeroy Jenkins.
     * 
     * @return Image for Dr. Jenkins
     * @throws IOException
     *             if file can't be found
     */
    @GetMapping ( value = "/DrJenkins", produces = MediaType.IMAGE_JPEG_VALUE )
    public ResponseEntity<byte[]> getImage () throws IOException {
        final ClassPathResource imgFile = new ClassPathResource( "image/DrJenkins.jpg" );
        final byte[] bytes = StreamUtils.copyToByteArray( imgFile.getInputStream() );
        return ResponseEntity.ok().contentType( MediaType.IMAGE_JPEG ).body( bytes );
    }

}
