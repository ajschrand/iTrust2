package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.BasicHealthMetricsRepository;

/**
 * Service class for interacting with BasicHealthMetrics model, performing CRUD
 * tasks with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class BasicHealthMetricsService extends Service<BasicHealthMetrics, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private BasicHealthMetricsRepository repository;

    /** Service for user lookups */
    @Autowired
    private UserService<User>            userService;

    @Override
    protected JpaRepository<BasicHealthMetrics, Long> getRepository () {
        return repository;
    }

    /**
     * Builds a BasicHealthMetrics object from the Form class
     *
     * @param ovf
     *            OfficeVisitForm
     * @return Built BasicHealthMetrics object
     */
    public BasicHealthMetrics build ( final OfficeVisitForm ovf ) {
        final BasicHealthMetrics bhm = new BasicHealthMetrics();
        bhm.setPatient( userService.findByName( ovf.getPatient() ) );
        bhm.setHcp( userService.findByName( ovf.getHcp() ) );

        bhm.setDiastolic( ovf.getDiastolic() );
        bhm.setHdl( ovf.getHdl() );
        bhm.setHeight( ovf.getHeight() );
        bhm.setHouseSmokingStatus( ovf.getHouseSmokingStatus() );
        bhm.setHeadCircumference( ovf.getHeadCircumference() );
        bhm.setLdl( ovf.getLdl() );
        bhm.setPatientSmokingStatus( ovf.getPatientSmokingStatus() );
        bhm.setSystolic( ovf.getSystolic() );
        bhm.setTri( ovf.getTri() );
        bhm.setWeight( ovf.getWeight() );

        return bhm;
    }
}
