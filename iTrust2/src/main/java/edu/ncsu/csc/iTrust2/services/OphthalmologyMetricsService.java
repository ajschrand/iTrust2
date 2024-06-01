package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.models.OphthalmologyMetrics;
import edu.ncsu.csc.iTrust2.repositories.OphthalmologyMetricsRepository;

/**
 * Service class for interacting with OphthalmologyMetrics model, performing
 * CRUD tasks with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 * @author Bruno Volpato
 *
 */
@Component
@Transactional
public class OphthalmologyMetricsService extends Service<OphthalmologyMetrics, Long> {
    /** Repository for CRUD operations */
    @Autowired
    private OphthalmologyMetricsRepository repository;

    @Override
    protected JpaRepository<OphthalmologyMetrics, Long> getRepository () {
        return repository;
    }

    /**
     * Builds an OphthalmologyMetrics object from information stored in an
     * OfficeVisitForm
     *
     * @param ovf
     *            OfficeVisitForm
     * @return Built metrics, or none if the OVF didn't have ophthalmology
     *         metrics
     */
    public OphthalmologyMetrics build ( final OfficeVisitForm ovf ) {

        if ( ovf.getOphthalmologyVisitForm() == null ) {
            return null;
        }

        final OphthalmologyMetrics om = new OphthalmologyMetrics();
        om.setAxisLeft( ovf.getOphthalmologyVisitForm().getAxisLeft() );
        om.setAxisRight( ovf.getOphthalmologyVisitForm().getAxisRight() );
        om.setCylinderLeft( ovf.getOphthalmologyVisitForm().getCylinderLeft() );
        om.setCylinderRight( ovf.getOphthalmologyVisitForm().getCylinderRight() );
        om.setSphereLeft( ovf.getOphthalmologyVisitForm().getSphereLeft() );
        om.setSphereRight( ovf.getOphthalmologyVisitForm().getSphereRight() );
        om.setVisualAcuityLeft( ovf.getOphthalmologyVisitForm().getVisualAcuityLeft() );
        om.setVisualAcuityRight( ovf.getOphthalmologyVisitForm().getVisualAcuityRight() );
        return om;
    }
}
