package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.UserRepository;

/**
 * Service class for interacting with User model, performing CRUD tasks with
 * database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 * @param <T>
 *            Type of user
 */
@Component
@Transactional
@Primary
public class UserService <T extends User> extends Service<T, String> {

    /** Repository for CRUD tasks */
    @Autowired
    private UserRepository<User> repository;

    @Override
    @SuppressWarnings ( "unchecked" )
    protected JpaRepository<T, String> getRepository () {
        return (JpaRepository<T, String>) repository;
    }

    /**
     * Finds a User with the given username
     *
     * @param username
     *            Username to search
     * @return Matching user, if any
     */
    public User findByName ( final String username ) {
        return repository.findByUsername( username );
    }

    /**
     * Checks if a User with the provided Username exists
     *
     * @param name
     *            Username to check
     * @return Whether user exists
     */
    public boolean existsByName ( final String name ) {
        return repository.existsByUsername( name );
    }

}
