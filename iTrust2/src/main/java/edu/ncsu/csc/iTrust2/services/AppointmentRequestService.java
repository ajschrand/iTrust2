package edu.ncsu.csc.iTrust2.services;

import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.AppointmentRequestForm;
import edu.ncsu.csc.iTrust2.models.AppointmentRequest;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.repositories.AppointmentRequestRepository;

/**
 * Service class for interacting with AppointmentRequest model, performing CRUD
 * tasks with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class AppointmentRequestService extends Service<AppointmentRequest, Long> {

    /** Repository for CRUD tasks */
    @Autowired
    private AppointmentRequestRepository repository;

    /** UserService for CRUD operations on User */
    @Autowired
    private UserService<User>            userService;

    @Override
    protected JpaRepository<AppointmentRequest, Long> getRepository () {
        return repository;
    }

    /**
     * Find all appointment requests for a given Patient
     *
     * @param patient
     *            Patient for lookups
     * @return Matching requests
     */
    public List<AppointmentRequest> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

    /**
     * Find all appointment requests for a given HCP
     *
     * @param hcp
     *            HCP for lookups
     * @return Matching requests
     */
    public List<AppointmentRequest> findByHcp ( final User hcp ) {
        return repository.findByHcp( hcp );
    }

    /**
     * Find all appointment requests for a given HCP and patient
     *
     * @param hcp
     *            HCP for lookups
     * @param patient
     *            Patient for lookups
     * @return Matching requests
     */
    public List<AppointmentRequest> findByHcpAndPatient ( final User hcp, final User patient ) {
        return repository.findByHcpAndPatient( hcp, patient );
    }

    /**
     * Builds an AppointmentRequest from the deserialised form
     *
     * @param raf
     *            AppointmentRequestForm containing data to build an AR from
     * @return Built AppointmentRequest
     */
    public AppointmentRequest build ( final AppointmentRequestForm raf ) {
        final AppointmentRequest ar = new AppointmentRequest();

        ar.setPatient( userService.findByName( raf.getPatient() ) );
        ar.setHcp( userService.findByName( raf.getHcp() ) );
        ar.setComments( raf.getComments() );

        final ZonedDateTime requestDate = ZonedDateTime.parse( raf.getDate() );
        if ( requestDate.isBefore( ZonedDateTime.now() ) ) {
            throw new IllegalArgumentException( "Cannot request an appointment before the current time" );
        }
        ar.setDate( requestDate );

        Status s = null;
        try {
            s = Status.valueOf( raf.getStatus() );
        }
        catch ( final NullPointerException npe ) {
            s = Status.PENDING; /*
                                 * Incoming AppointmentRequests will come in
                                 * from the form with no status. Set status to
                                 * Pending until it is adjusted further
                                 */
        }
        ar.setStatus( s );
        AppointmentType at = null;
        try {
            at = AppointmentType.valueOf( raf.getType() );
        }
        catch ( final NullPointerException npe ) {
            at = AppointmentType.GENERAL_CHECKUP; /*
                                                   * If for some reason we don't
                                                   * have a type, default to
                                                   * general checkup
                                                   */
        }
        ar.setType( at );

        return ar;
    }

}
