package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.services.EmailService;

/**
 * Controller to enable retrieving the fake emails sent in the iTrust2 system
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
public class APIEmailController extends APIController {

    /** Email service */
    @Autowired
    private EmailService service;

    /**
     * Retrieves and returns all fake emails. Note that for real, this would be
     * a MASSIVE security vulnerability to not only make everything available,
     * but without authentication. Don't do this!
     *
     * @return Emails
     */
    @GetMapping ( BASE_PATH + "emails" )
    public List<Email> getEmails () {
        return service.findAll();
    }

}
