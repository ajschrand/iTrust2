package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.OphthalmologyMetrics;

/**
 * Repository for Ophthalmology metrics
 *
 * @author Bruno Volpato
 *
 */
public interface OphthalmologyMetricsRepository extends JpaRepository<OphthalmologyMetrics, Long> {

}
