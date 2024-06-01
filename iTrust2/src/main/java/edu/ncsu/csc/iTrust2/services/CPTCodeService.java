package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.repositories.CPTCodeRepository;

/**
 * Service class for interacting with CPTCode model, performing CRUD tasks with
 * database.
 *
 * @author yli246
 *
 */
@Component
@Transactional
public class CPTCodeService extends Service<CPTCode, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private CPTCodeRepository repository;

    @Override
    protected JpaRepository<CPTCode, Long> getRepository () {
        return repository;
    }

    /**
     * Finds an CPTCode object for the given Code
     *
     * @param code
     *            Code of the CPTCode desired
     * @return CPTCode found, if any
     */
    public CPTCode findByCode ( final int code ) {
        return repository.findByCode( code );
    }

    @Override
    public List<CPTCode> findAll () {
        return repository.findAll();
    }

    /**
     * Find an active CPT code by the provided code
     *
     * @param code
     *            Code to search by
     * @return Matching code, if any
     */
    public CPTCode findActiveByCode ( final int code ) {
        return repository.findByCodeAndIsActiveTrue( code );
    }

    /**
     * Finds the most recent code, active or not, that matches
     *
     * @param code
     *            Code by search by
     * @return Matching code, if any
     */
    public CPTCode findMostRecentByCode ( final int code ) {
        return repository.findFirstByCodeOrderByVersionDesc( code );
    }

    /**
     * Builds an CPTCode based on the deserialised CPTCodeForm
     *
     * @param form
     *            Form to build from
     * @return Constructed CPTCode
     */
    public CPTCode build ( final CPTCodeForm form ) {
        final CPTCode code = new CPTCode();
        code.setCode( form.getCode() );
        code.setDescription( form.getDescription() );
        code.setId( form.getId() );
        code.setCost( form.getCost() );
        code.setVersion( form.getVersion() );
        code.setisActive( form.getisActive() );

        // validate
        if ( code.getDescription().length() > 250 ) {
            throw new IllegalArgumentException( "Description too long (250 characters max): " + code.getDescription() );
        }
        if ( String.valueOf( code.getCode() ).length() != 5 ) {
            throw new IllegalArgumentException( "Code must be a 5 digit number: " + code.getCode() );
        }

        return code;
    }
}
