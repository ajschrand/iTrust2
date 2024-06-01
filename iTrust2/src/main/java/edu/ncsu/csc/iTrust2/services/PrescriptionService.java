package edu.ncsu.csc.iTrust2.services;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.PrescriptionRepository;

/**
 * Service class for interacting with Prescription model, performing CRUD tasks
 * with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class PrescriptionService extends Service<Prescription, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private PrescriptionRepository repository;

    /** Drug service */
    @Autowired
    private DrugService            drugService;

    /** User service */
    @Autowired
    private UserService<User>      userService;

    @Override
    protected JpaRepository<Prescription, Long> getRepository () {
        return repository;
    }

    /**
     * Builds a Prescription from the deserialised PrescriptionForm
     *
     * @param form
     *            Form to build a Prescription from
     * @return Build Prescription
     */
    public Prescription build ( final PrescriptionForm form ) {
        final Prescription pr = new Prescription();

        pr.setDrug( drugService.findByCode( form.getDrug() ) );
        pr.setDosage( form.getDosage() );
        pr.setRenewals( form.getRenewals() );
        pr.setPatient( userService.findByName( form.getPatient() ) );

        if ( form.getId() != null ) {
            pr.setId( form.getId() );
        }

        pr.setStartDate( LocalDate.parse( form.getStartDate() ) );
        pr.setEndDate( LocalDate.parse( form.getEndDate() ) );

        return pr;
    }

    /**
     * Find all Prescriptions for a given Patient
     * 
     * @param patient
     *            Patient to search by
     * @return Matched prescriptions
     */
    public List<Prescription> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

}
