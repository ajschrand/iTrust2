package edu.ncsu.csc.iTrust2.services.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.security.LoginAttempt;
import edu.ncsu.csc.iTrust2.repositories.security.LoginAttemptRepository;
import edu.ncsu.csc.iTrust2.services.Service;

/**
 * Service for interacting with LoginAttempts.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class LoginAttemptService extends Service<LoginAttempt, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private LoginAttemptRepository repository;

    @Override
    protected JpaRepository<LoginAttempt, Long> getRepository () {
        return repository;
    }

    /**
     * Reports the number of login attempts for a given IP address
     *
     * @param ipAddress
     *            IP address to check
     * @return Number of login attempts
     */
    public long countByIP ( final String ipAddress ) {
        return repository.countByIp( ipAddress );
    }

    /**
     * Clears the login attempts for a given IP address
     *
     * @param ipAddress
     *            IP address to clear
     * @return Number of records deleted
     */
    public long clearIP ( final String ipAddress ) {
        return repository.deleteByIp( ipAddress );
    }

    /**
     * Reports the number of login attempts for a given user
     *
     * @param user
     *            User to check
     * @return Number of login attempts
     */
    public long countByUser ( final User user ) {
        return repository.countByUser( user );
    }

    /**
     * Clears the login attempts for a given user
     *
     * @param user
     *            User to clear
     * @return Number of records deleted
     */
    public long clearUser ( final User user ) {
        return repository.deleteByUser( user );
    }

}
