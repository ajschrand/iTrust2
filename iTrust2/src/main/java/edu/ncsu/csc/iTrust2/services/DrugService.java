package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.repositories.DrugRepository;

/**
 * Service class for interacting with Drug model, performing CRUD tasks with
 * database.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class DrugService extends Service<Drug, Long> {

    /** Repository for CRUD tasks */
    @Autowired
    private DrugRepository repository;

    @Override
    protected JpaRepository<Drug, Long> getRepository () {
        return repository;
    }

    /**
     * Checks if a Drug with the provided code exists
     * 
     * @param code
     *            Code to check
     * @return If drug with this code exists
     */
    public boolean existsByCode ( final String code ) {
        return repository.existsByCode( code );
    }

    /**
     * Finds a Drug with the provided code
     * 
     * @param code
     *            Code to check
     * @return Drug, if found
     */
    public Drug findByCode ( final String code ) {
        return repository.findByCode( code );
    }
}
