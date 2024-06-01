package edu.ncsu.csc.iTrust2.services;

import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.VaccineAppointmentRequestForm;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineAppointmentRequest;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.models.enums.VaccinationStatus;
import edu.ncsu.csc.iTrust2.repositories.VaccineAppointmentRequestRepository;

/**
 * Service class for interacting with VaccineAppointmentRequest model,
 * performing CRUD tasks with database and building a persistence object from a
 * Form.
 *
 * @author accline2, mjcheim
 *
 */
@Component
@Transactional
public class VaccineAppointmentRequestService extends Service<VaccineAppointmentRequest, Long> {

    /** Repository for CRUD tasks */
    @Autowired
    private VaccineAppointmentRequestRepository repository;

    /** UserService for CRUD operations on User */
    @Autowired
    private UserService<User>                   userService;

    /** Repository to check Vaccine Types */
    @Autowired
    private VaccineTypeService                  vaccineService;

    @Override
    protected JpaRepository<VaccineAppointmentRequest, Long> getRepository () {
        return repository;
    }

    /**
     * Find all appointment requests for a given Patient
     *
     * @param patient
     *            Patient for lookups
     * @return Matching requests
     */
    public List<VaccineAppointmentRequest> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

    /**
     * Find all appointment requests for a given HCP
     *
     * @param hcp
     *            HCP for lookups
     * @return Matching requests
     */
    public List<VaccineAppointmentRequest> findByHcp ( final User hcp ) {
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
    public List<VaccineAppointmentRequest> findByHcpAndPatient ( final User hcp, final User patient ) {
        return repository.findByHcpAndPatient( hcp, patient );
    }

    /**
     * Builds a VaccineAppointmentRequest from a VaccineAppointmentRequestForm
     *
     * @param varf
     *            form to build
     * @return built VaccineAppointmentRequest
     */
    public VaccineAppointmentRequest build ( final VaccineAppointmentRequestForm varf ) {
        final VaccineAppointmentRequest request = new VaccineAppointmentRequest();

        request.setPatient( userService.findByName( varf.getPatient() ) );
        request.setComments( varf.getComments() );

        request.setHcp( userService.findByName( varf.getHcp() ) );

        final ZonedDateTime requestDate = ZonedDateTime.parse( varf.getDate() );
        if ( requestDate.isBefore( ZonedDateTime.now() ) ) {
            throw new IllegalArgumentException( "Cannot request an appointment before the current time" );
        }
        request.setDate( requestDate );

        Status s = null;
        try {
            s = Status.valueOf( varf.getStatus() );
        }
        catch ( final NullPointerException npe ) {
            s = Status.PENDING; /*
                                 * Incoming AppointmentRequests will come in
                                 * from the form with no status. Set status to
                                 * Pending until it is adjusted further
                                 */
        }
        request.setStatus( s );
        AppointmentType at = null;
        try {
            at = AppointmentType.valueOf( varf.getType() );
        }
        catch ( final NullPointerException npe ) {
            at = AppointmentType.GENERAL_CHECKUP; /*
                                                   * If for some reason we don't
                                                   * have a type, default to
                                                   * general checkup
                                                   */
        }
        request.setType( at );

        request.setVaccine( vaccineService.findByName( varf.getVaccineType() ) );

        final String stat = varf.getVaccineStatus();

        if ( stat.equals( VaccinationStatus.FULLY_VACCINATED.toString() ) ) {
            throw new IllegalArgumentException( "Already fully vaxxed." );
        }
        if ( request.getVaccine().getNumDoses() <= 1 && !stat.equals( VaccinationStatus.NOT_VACCINATED.toString() ) ) {

            throw new IllegalArgumentException( "Not qualified for vaccine." );
        }

        return request;
    }

}
