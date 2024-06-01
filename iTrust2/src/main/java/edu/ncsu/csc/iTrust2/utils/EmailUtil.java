package edu.ncsu.csc.iTrust2.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.services.EmailService;

/**
 * Class for sending iTrust2-internal emails to other users in the system
 * 
 * @author Kai
 *
 */
@Component
public class EmailUtil {

    /** EmailService, for saving/retrieving emails */
    @Autowired
    private EmailService service;

    /**
     * Send an Email to a user in the system
     * 
     * @param receiver
     *            User the email should be sent to
     * @param subject
     *            Subject of the email
     * @param messageBody
     *            Contents of the email
     */
    public void sendEmail ( final User receiver, final String subject, final String messageBody ) {
        sendEmail( "iTrust2 System", receiver, subject, messageBody );
    }

    /**
     * Send an email to a user in the system
     * 
     * @param sender
     *            User who is sending the email
     * @param receiver
     *            User the email should be sent to
     * @param subject
     *            Subject of the email
     * @param messageBody
     *            Contents of the email
     */
    public void sendEmail ( final String sender, final User receiver, final String subject, final String messageBody ) {
        final Email email = new Email( sender, receiver, subject, messageBody );
        service.save( email );
    }
}
