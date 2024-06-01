package edu.ncsu.csc.iTrust2.services;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.models.AppointmentRequest;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.repositories.OfficeVisitRepository;

/**
 * Service class for interacting with OfficeVisit model, performing CRUD tasks
 * with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class OfficeVisitService extends Service<OfficeVisit, Long> {

    /**
     * Repository for CRUD operations
     */
    @Autowired
    private OfficeVisitRepository       repository;

    /**
     * User service
     */
    @Autowired
    private UserService<User>           userService;

    /**
     * AppointmentRequest service
     */
    @Autowired
    private AppointmentRequestService   appointmentRequestService;

    /**
     * Hospital Service
     */
    @Autowired
    private HospitalService             hospitalService;

    /**
     * BasicHealthMetrics service
     */
    @Autowired
    private BasicHealthMetricsService   bhmService;

    /**
     * OphthalmologyMetrics service
     */
    @Autowired
    private OphthalmologyMetricsService omService;

    /**
     * Prescription service
     */
    @Autowired
    private PrescriptionService         prescriptionService;

    /**
     * CPTCode service
     */
    @Autowired
    private CPTCodeService              cptService;

    /**
     * Diagnosis service
     */
    @Autowired
    private DiagnosisService            diagnosisService;

    @Override
    protected JpaRepository<OfficeVisit, Long> getRepository () {
        return repository;
    }

    /**
     * Finds all OfficeVisits created by the specified HCP
     *
     * @param hcp
     *            HCP to search for
     * @return Matching OfficeVisits
     */
    public List<OfficeVisit> findByHcp ( final User hcp ) {
        return repository.findByHcp( hcp );
    }

    /**
     * Finds all OfficeVisits for the specified Patient
     *
     * @param patient
     *            Patient to search for
     * @return Matching OfficeVisits
     */
    public List<OfficeVisit> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

    /**
     * Find all OfficeVisits for both the specified Patient and HCP
     *
     * @param hcp
     *            HCP to search for
     * @param patient
     *            Patient to search for
     * @return List of visits found
     */
    public List<OfficeVisit> findByHcpAndPatient ( final User hcp, final User patient ) {
        return repository.findByHcpAndPatient( hcp, patient );
    }

    /**
     * Builds an OfficeVisit based on the deserialised OfficeVisitForm
     *
     * @param ovf
     *            Form to build from
     * @return Constructed OfficeVisit
     */
    public OfficeVisit build ( final OfficeVisitForm ovf ) {
        final OfficeVisit ov = new OfficeVisit();

        ov.setPatient( userService.findByName( ovf.getPatient() ) );
        ov.setHcp( userService.findByName( ovf.getHcp() ) );
        ov.setNotes( ovf.getNotes() );

        if ( ovf.getId() != null ) {
            ov.setId( Long.parseLong( ovf.getId() ) );
        }

        final ZonedDateTime visitDate = ZonedDateTime.parse( ovf.getDate() );
        ov.setDate( visitDate );

        AppointmentType at = null;
        try {
            at = AppointmentType.valueOf( ovf.getType() );
        }
        catch ( final NullPointerException npe ) {
            at = AppointmentType.GENERAL_CHECKUP; /*
                                                   * If for some reason we don't
                                                   * have a type, default to
                                                   * general checkup
                                                   */
        }
        ov.setType( at );

        if ( null != ovf.getPreScheduled() ) {
            final List<AppointmentRequest> requests = appointmentRequestService.findByHcpAndPatient( ov.getHcp(),
                    ov.getPatient() );
            try {
                final AppointmentRequest match = requests.stream().filter( e -> e.getDate().equals( ov.getDate() ) )
                        .collect( Collectors.toList() )
                        .get( 0 ); /*
                                    * We should have one and only one
                                    * appointment for the provided HCP & patient
                                    * and the time specified
                                    */
                ov.setAppointment( match );
            }
            catch ( final Exception e ) {
                throw new IllegalArgumentException( "Marked as preschedule but no match can be found" + e.toString() );
            }

        }
        ov.setHospital( hospitalService.findByName( ovf.getHospital() ) );
        ov.setBasicHealthMetrics( bhmService.build( ovf ) );
        ov.setOphthalmologyMetrics( omService.build( ovf ) );
        // associate all diagnoses with this visit
        if ( ovf.getDiagnoses() != null ) {
            ov.setDiagnoses(
                    ovf.getDiagnoses().stream().map( diagnosisService::build ).collect( Collectors.toList() ) );
            for ( final Diagnosis d : ov.getDiagnoses() ) {
                d.setVisit( ov );
            }
        }

        ov.validateDiagnoses();
        ov.validateOphthalmology();

        final List<PrescriptionForm> ps = ovf.getPrescriptions();
        if ( ps != null ) {
            ov.setPrescriptions( ps.stream().map( prescriptionService::build ).collect( Collectors.toList() ) );
        }

        if ( ovf.getCptCodes() != null ) {
            final List<CPTCode> cs = new ArrayList<CPTCode>();
            for ( int i = 0; i < ovf.getCptCodes().size(); i++ ) {
                cs.add( cptService.findById( ovf.getCptCodes().get( i ) ) );
            }
            ov.setCptCodes( cs );
        }

        final Patient p = (Patient) ov.getPatient();
        if ( p == null || p.getDateOfBirth() == null ) {
            return ov; // we're done, patient can't be tested against
        }
        final LocalDate dob = p.getDateOfBirth();
        int age = ov.getDate().getYear() - dob.getYear();
        // Remove the -1 when changing the dob to OffsetDateTime
        if ( ov.getDate().getMonthValue() < dob.getMonthValue() ) {
            age -= 1;
        }
        else if ( ov.getDate().getMonthValue() == dob.getMonthValue() ) {
            if ( ov.getDate().getDayOfMonth() < dob.getDayOfMonth() ) {
                age -= 1;
            }
        }

        if ( age < 3 ) {
            ov.validateUnder3();
        }
        else if ( age < 12 ) {
            ov.validateUnder12();
        }
        else {
            ov.validate12AndOver();
        }

        return ov;
    }

}
