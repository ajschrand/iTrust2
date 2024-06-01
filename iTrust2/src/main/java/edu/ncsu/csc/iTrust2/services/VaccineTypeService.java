package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.VaccineType;
import edu.ncsu.csc.iTrust2.repositories.VaccineTypeRepository;

/**
 * Service class for interacting with VaccineType model, performing CRUD tasks
 * with database and building a persistence object from a Form.
 *
 * @author mpenaga
 * @author skadhir
 *
 */
@Component
@Transactional
public class VaccineTypeService extends Service<VaccineType, Long> {
    /** Repository for CRUD tasks */
    @Autowired
    private VaccineTypeRepository repository;

    @Override
    protected JpaRepository<VaccineType, Long> getRepository () {
        return repository;
    }

    /**
     * Checks if a Vaccine with the provided code exists
     *
     * @param name
     *            name to check
     * @return If vaccine with this code exists
     */
    public boolean existsByName ( final String name ) {
        return repository.existsByName( name );
    }

    /**
     * Finds a Vaccine with the provided code
     *
     * @param name
     *            name to check
     * @return Vaccine, if found
     */
    public VaccineType findByName ( final String name ) {
        return repository.findByName( name );
    }
}
