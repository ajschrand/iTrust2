package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.repositories.PersonnelRepository;

/**
 * Service class for interacting with Personnel model, performing CRUD tasks
 * with database.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class PersonnelService extends UserService<Personnel> {

    /**
     * Repository for CRUD operations
     */
    @Autowired
    private PersonnelRepository<Personnel> repository;

    @Override
    protected JpaRepository<Personnel, String> getRepository () {
        return repository;
    }

}
