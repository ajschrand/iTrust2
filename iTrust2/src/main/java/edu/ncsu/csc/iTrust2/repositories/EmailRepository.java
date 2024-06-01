package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.models.User;

/**
 * Repository for interacting with Email model. Method implementations generated
 * by Spring
 * 
 * @author Kai Presler-Marshall
 *
 */
public interface EmailRepository extends JpaRepository<Email, Long> {

    /**
     * Find emails sent to the given User
     * 
     * @param receiver
     *            Receiver of the emails
     * @return Matching emails
     */
    public List<Email> findByReceiver ( User receiver );

}
