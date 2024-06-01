package edu.ncsu.csc.iTrust2.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.PatientRepository;

/**
 * Service class for interacting with Patient model, performing CRUD tasks with
 * database.
 *
 * @author Kai Presler-Marshall
 * @param <T>
 *            Type of user
 *
 */
@Component
@Transactional
public class PatientService <T extends User> extends UserService<Patient> {

    /**
     * Repository for CRUD operations
     */
    @Autowired
    private PatientRepository<Patient>        repository;

    /** PatientAdvocateAssocaition service for lookups */
    @Autowired
    private PatientAdvocateAssociationService paaService;

    @Override
    protected JpaRepository<Patient, String> getRepository () {
        return repository;
    }

    /**
     * Gets the list of patients that are associated with a given advocate
     *
     * @param advocate
     *            username of the advocate
     * @return a list of Patients associated with an advocate
     */
    public List<Patient> getPatientsByAdvocate ( final String advocate ) {
        // Initialize list of patients to return
        final List<Patient> patientsByAdvocate = new ArrayList<Patient>();
        // Loop through each patient in the repository
        for ( final Patient p : findAll() ) {
            // For each patient, loop through its associations
            for ( final Long paaId : p.getAssociations() ) {
                // Grab the association via its stored Id
                final PatientAdvocateAssociation paa = paaService.findById( paaId );
                // If the association is between this patient and the requested
                // advocate...
                final String pa = paa.getPatientAdvocate();
                if ( pa.equals( advocate ) ) {
                    // Add the patient to the list to return
                    patientsByAdvocate.add( p );
                    // And skip redundant loops
                    break;
                }
            }
        }

        return patientsByAdvocate;
    }

}
