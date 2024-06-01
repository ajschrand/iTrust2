package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;

/**
 * Repository for interacting with Bill model. Method implementations generated
 * by Spring
 *
 * @author nhwiblit
 *
 */
public interface BillRepository extends JpaRepository<Bill, Long> {
    /**
     * Find bills for a given patient
     *
     * @param patient
     *            Patient to search by
     * @return Matching visits
     */
    public List<Bill> findByPatient ( User patient );
}
