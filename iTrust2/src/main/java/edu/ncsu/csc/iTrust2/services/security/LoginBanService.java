package edu.ncsu.csc.iTrust2.services.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.security.LoginBan;
import edu.ncsu.csc.iTrust2.repositories.security.LoginBanRepository;
import edu.ncsu.csc.iTrust2.services.Service;

/**
 * Service for interacting with LoginBans
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class LoginBanService extends Service<LoginBan, Long> {

    /**
     * Repository for CRUD actions
     */
    @Autowired
    private LoginBanRepository repository;

    @Override
    protected JpaRepository<LoginBan, Long> getRepository () {
        return repository;
    }

    /**
     * Checks if an IP address is banned
     * 
     * @param ipAddress
     *            IP address to check
     * @return Ban status
     */
    public boolean isIPBanned ( final String ipAddress ) {
        return repository.existsByIp( ipAddress );
    }

    /**
     * Checks if a User is banned
     * 
     * @param user
     *            User to check
     * @return Ban status
     */
    public boolean isUserBanned ( final User user ) {
        return repository.existsByUser( user );
    }

    /**
     * Clears bans for an IP address
     * 
     * @param ipAddress
     *            IP Address to clear
     * @return Number of bans deleted
     */
    public long clearIP ( final String ipAddress ) {
        return repository.deleteByIp( ipAddress );
    }

    /**
     * Clears bans for a User
     * 
     * @param user
     *            User to clear
     * @return Number of bans deleted
     */
    public long clearUser ( final User user ) {
        return repository.deleteByUser( user );
    }
}
