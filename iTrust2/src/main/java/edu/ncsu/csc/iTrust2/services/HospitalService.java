package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.repositories.HospitalRepository;

/**
 * Service class for interacting with Hospital. model, performing CRUD tasks
 * with database.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class HospitalService extends Service<Hospital, String> {

    /** Repository for CRUD tasks */
    @Autowired
    private HospitalRepository repository;

    @Override
    protected JpaRepository<Hospital, String> getRepository () {
        return repository;
    }

    /**
     * Finds a Hospital with the provided name
     * 
     * @param name
     *            Name of the hospital
     * @return Hospital, if found
     */
    public Hospital findByName ( final String name ) {
        return repository.findByName( name );
    }

}
