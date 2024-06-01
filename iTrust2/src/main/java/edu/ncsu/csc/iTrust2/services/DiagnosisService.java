package edu.ncsu.csc.iTrust2.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.DiagnosisForm;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.DiagnosisRepository;

/**
 * Service class for interacting with Diagnosis model, performing CRUD tasks
 * with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class DiagnosisService extends Service<Diagnosis, Long> {

    /** Repository for CRUD tasks */
    @Autowired
    private DiagnosisRepository repository;

    /** Office visit service for lookups */
    @Autowired
    private OfficeVisitService  service;

    /** ICDCode service for lookups */
    @Autowired
    private ICDCodeService      icdCodeService;

    @Override
    protected JpaRepository<Diagnosis, Long> getRepository () {
        return repository;
    }

    /**
     * Builds and validates a Diagnosis from the provided DiagnosisForm
     *
     * @param form
     *            Form for building persistence object
     * @return Generated Diagnosis
     */
    public Diagnosis build ( final DiagnosisForm form ) {
        final Diagnosis diag = new Diagnosis();
        diag.setVisit( service.findById( form.getVisit() ) );
        diag.setNote( form.getNote() );
        diag.setCode( icdCodeService.findByCode( form.getCode() ) );
        diag.setId( form.getId() );

        return diag;
    }

    /**
     * Finds all Diagnoses for a specified patient
     * 
     * @param patient
     *            Patient to search for
     * @return Diagnoses matched
     */
    public List<Diagnosis> findByPatient ( final User patient ) {
        return service.findByPatient( patient ).stream().map( e -> findByVisit( e ) ).flatMap( e -> e.stream() )
                .collect( Collectors.toList() );

    }

    /**
     * Finds all Diagnoses for a specified OfficeVisit
     * 
     * @param visit
     *            OfficeVisit to search by
     * @return Diagnoses matched
     */
    public List<Diagnosis> findByVisit ( final OfficeVisit visit ) {
        return repository.findByVisit( visit );
    }

}
