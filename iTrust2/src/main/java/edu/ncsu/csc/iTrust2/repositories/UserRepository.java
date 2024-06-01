package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.User;

/**
 * Repository for interacting with User model. Method implementations generated
 * by Spring
 *
 * @author Kai Presler-Marshall
 * @param <T>
 *            Type of User
 *
 */
public interface UserRepository <T extends User> extends JpaRepository<T, String> {

    /**
     * Checks if a User with the given username exists
     *
     * @param name
     *            Username to check
     * @return if user exists
     */
    public boolean existsByUsername ( String name );

    /**
     * Finds a User with the given Username
     *
     * @param username
     *            Username to check
     * @return Matching user, if any
     */
    public User findByUsername ( String username );

}
