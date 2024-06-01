package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.EmailRepository;

/**
 * Service class for interacting with Email model, performing CRUD tasks with
 * database.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class EmailService extends Service<Email, Long> {

    /**
     * Repository for CRUD tasks
     */
    @Autowired
    private EmailRepository repository;

    @Override
    protected JpaRepository<Email, Long> getRepository () {
        return repository;
    }

    /**
     * Finds emails sent to a specific User
     * 
     * @param receiver
     *            Receiver of the email
     * @return Matching emails
     */
    public List<Email> findByReceiver ( final User receiver ) {
        return repository.findByReceiver( receiver );
    }
}
