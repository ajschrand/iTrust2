package edu.ncsu.csc.iTrust2.services.security;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.security.LoginLockout;
import edu.ncsu.csc.iTrust2.repositories.security.LoginLockoutRepository;
import edu.ncsu.csc.iTrust2.services.Service;

/**
 * Service for interacting with the LoginLockout model
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class LoginLockoutService extends Service<LoginLockout, Long> {

    /**
     * Repository for CRUD operations
     */
    @Autowired
    private LoginLockoutRepository repository;

    @Override
    protected JpaRepository<LoginLockout, Long> getRepository () {
        return repository;
    }

    /**
     * Checks if an IP address is locked out
     *
     * @param ipAddress
     *            IP address to check
     * @return Lockout status
     */
    public boolean isIPLocked ( final String ipAddress ) {
        final long now = ZonedDateTime.now().toEpochSecond();

        return repository.findByIp( ipAddress ).stream().filter( e -> ( now - e.getTime().toEpochSecond() ) < 60 * 60 )
                .collect( Collectors.toList() ).size() > 0; // locked if within
                                                            // 60 minutes
    }

    /**
     * Clears lockouts for an IP
     *
     * @param ipAddress
     *            Address to clear
     * @return Number of records deleted
     */
    public long clearIP ( final String ipAddress ) {
        return repository.deleteByIp( ipAddress );

    }

    /**
     * Gets the number of IP lockouts for an address within the past 60 minutes
     *
     * @param ipAddress
     *            IP address to check
     * @return Number of lockouts
     */
    public int getRecentIPLockouts ( final String ipAddress ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return repository.findByIp( ipAddress ).stream()
                .filter( e -> ( now - e.getTime().toEpochSecond() ) < 1440 * 60 ).collect( Collectors.toList() ).size(); // 1440
                                                                                                                         // minutes
    }

    /**
     * Gets the number of lockouts for a user within the past 60 minutes
     *
     * @param user
     *            to check
     * @return Number of lockouts
     */
    public int getRecentUserLockouts ( final User user ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return repository.findByUser( user ).stream().filter( e -> ( now - e.getTime().toEpochSecond() ) < 1440 * 60 )
                .collect( Collectors.toList() ).size(); // 1440 minutes
    }

    /**
     * Clears the lockouts for a given user
     *
     * @param user
     *            User to clear
     * @return number of entries deleted
     */
    public long clearUser ( final User user ) {
        return repository.deleteByUser( user );
    }

    /**
     * Checks if a user is locked out (do they have a lockout within the past
     * hour)
     *
     * @param user
     *            User to check
     * @return Lockout status
     */
    public boolean isUserLocked ( final User user ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return repository.findByUser( user ).stream().filter( e -> ( now - e.getTime().toEpochSecond() ) < 60 * 60 )
                .collect( Collectors.toList() ).size() > 0; // locked if within
                                                            // 60 minutes
    }

}
