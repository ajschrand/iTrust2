package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Drug;

/**
 * Repository for interacting with Drug model. Method implementations generated
 * by Spring
 *
 * @author Kai Presler-Marshall
 *
 */
public interface DrugRepository extends JpaRepository<Drug, Long> {

    /**
     * Check if a drug exists with the given code
     * 
     * @param code
     *            Code to search by
     * @return If the drug exists
     */
    public boolean existsByCode ( String code );

    /**
     * Find a drug by the given code
     * 
     * @param code
     *            Code to search by
     * @return The drug, if it exists
     */
    public Drug findByCode ( String code );

}
