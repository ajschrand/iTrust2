package edu.ncsu.csc.iTrust2.services;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.repositories.ICDCodeRepository;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Service class for interacting with ICDCode model, performing CRUD tasks with
 * database.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class ICDCodeService extends Service<ICDCode, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private ICDCodeRepository repository;

    /** Service for looking up Personnel */
    @Autowired
    private PersonnelService  service;

    @Override
    protected JpaRepository<ICDCode, Long> getRepository () {
        return repository;
    }

    /**
     * Finds an ICDCode object for the given Code
     * 
     * @param code
     *            Code of the ICDCode desired
     * @return ICDCode found, if any
     */
    public ICDCode findByCode ( final String code ) {
        return repository.findByCode( code );
    }

    @Override
    public List<ICDCode> findAll () {
        final User user = service.findByName( LoggerUtil.currentUser() );
        final Collection<Role> roles = user.getRoles();

        if ( roles.contains( Role.ROLE_ADMIN )
                || ( roles.contains( Role.ROLE_OPH ) && roles.contains( Role.ROLE_HCP ) ) ) {
            return repository.findAll();
        }

        if ( roles.contains( Role.ROLE_OPH ) ) {
            return repository.findByIsOphthalmologyIsTrue();
        }

        return repository.findByIsOphthalmologyIsFalseOrIsOphthalmologyIsNull();
    }

}
