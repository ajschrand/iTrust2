package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineVisit;

/**
 * Repository for interacting with VaccineVisit model. Method
 * implementations generated by Spring
 * 
 * @author accline2
 * @author mjcheim
 */
public interface VaccineVisitRepository extends JpaRepository<VaccineVisit, Long> {

    /**
     * Find all VaccineVisits for provided patient
     * 
     * @param patient patient to find visits for
     * @return matching visits
     */
    List<VaccineVisit> findByPatient ( User patient );

    /**
     * Find all VaccineVisits with requested vaccinator
     * 
     * @param hcp vaccinator to search for
     * @return matching visits
     */
    List<VaccineVisit> findByVaccinator ( User hcp );

    /**
     * Find all VaccineVisits for both given vaccinators and patients
     * 
     * @param hcp vaccinator to search for
     * @param patient patient to search for
     * @return matching visits
     */
    List<VaccineVisit> findByVaccinatorAndPatient ( User hcp, User patient );

}
