package edu.ncsu.csc.iTrust2.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.repositories.PatientAdvocateAssociationRepository;

/**
 * Service class for interacting with PatientAdvocateAssociation model,
 * performing CRUD tasks with database.
 *
 * @author noahalexander
 *
 */
@Component
@Transactional
public class PatientAdvocateAssociationService extends Service<PatientAdvocateAssociation, Long> {

    /**
     * Repository for CRUD operations
     */
    @Autowired
    private PatientAdvocateAssociationRepository repository;

    @Override
    protected JpaRepository<PatientAdvocateAssociation, Long> getRepository () {
        // TODO Auto-generated method stub
        return repository;
    }

    /**
     * Gets the list of associations that have the Patient username in them
     *
     * @param patient
     *            username of the patient
     * @return a list of Patient Advocate Associations for a patient
     */
    public List<PatientAdvocateAssociation> getPatientAssociations ( final String patient ) {
        final List<PatientAdvocateAssociation> associations = new ArrayList<PatientAdvocateAssociation>();
        for ( final PatientAdvocateAssociation a : findAll() ) {
            if ( a.getPatient().equals( patient ) ) {
                associations.add( a );
            }
        }

        return associations;
    }

    /**
     * Gets the list of associations that have the PatientAdvocate username in
     * them
     *
     * @param advocate
     *            username of the advocate
     * @return a list of Patient Advocate Associations for a Patient Advocate
     */
    public List<PatientAdvocateAssociation> getPatientAdvocateAssociations ( final String advocate ) {
        final List<PatientAdvocateAssociation> associations = new ArrayList<PatientAdvocateAssociation>();
        for ( final PatientAdvocateAssociation a : findAll() ) {
            if ( a.getPatientAdvocate().equals( advocate ) ) {
                associations.add( a );
            }
        }

        return associations;
    }

    /**
     * Gets the association that corresponds with the Patient and
     * PatientAdvocate. If no association exists will return null.
     *
     * @param patient
     *            username of the patient
     * @param advocate
     *            username of the advocate
     * @return a specific Patient Advocate Association for a Patient and Patient
     *         Advocate
     */
    public PatientAdvocateAssociation getAssociation ( final String patient, final String advocate ) {
        for ( final PatientAdvocateAssociation a : findAll() ) {
            if ( a.getPatientAdvocate().equals( advocate ) && a.getPatient().equals( patient ) ) {
                return a;
            }
        }
        return null;
    }

    /**
     * Gets the association that matches the id. If no association exists will
     * return null.
     *
     * @param id
     *            id of the association
     * @return a specific Patient Advocate Association for a Patient and Patient
     *         Advocate
     */
    public PatientAdvocateAssociation getAssociationById ( final Long id ) {
        for ( final PatientAdvocateAssociation a : findAll() ) {
            if ( a.getId().equals( id ) ) {
                return a;
            }
        }
        return null;
    }

}
