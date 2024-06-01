package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.VaccineVisit;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.VaccineVisitService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the Vaccination Certification
 * model.
 *
 * @author skadhir, accline2
 */
@RestController
@SuppressWarnings ( { "rawtypes" } )
public class APIVaccinationCertificateController extends APIController {

    /** Patient Service */
    @Autowired
    private PatientService      userService;

    /** Vaccine Visit Service */
    @Autowired
    private VaccineVisitService visitService;

    /**
     * Gets the vaccination certificate for a patient
     *
     * @return the certificate
     */
    @GetMapping ( BASE_PATH + "/vaccinationcertificate" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public Certificate getVaccinationCertificate () {
        final Patient self = (Patient) userService.findByName( LoggerUtil.currentUser() );
        final List<VaccineVisit> visits = visitService.findByPatient( self );

        final List<CertificateElement> celist = new ArrayList<CertificateElement>();
        // CertificateElement ce = new CertificateElement( null, null, null,
        // null, null );
        for ( int i = 0; i < visits.size(); i++ ) {
            final Patient patient = visits.get( i ).getPatient();
            final String dateTime = visits.get( i ).getDate().toString();
            final String numDoses = ( i + 1 ) + "";
            final String vaccineType = visits.get( i ).getVaccineType().getName();
            final String vaccinator = visits.get( i ).getVaccinator().getUsername();
            final VaccineVisit visit = visits.get( i );
            final CertificateElement ce = new CertificateElement( patient, dateTime, numDoses, vaccinator, vaccineType,
                    visit );

            celist.add( ce );
        }
        final Certificate certificate = new Certificate( celist, self );
        return certificate;
    }

    /**
     * Helper class to hold parts of a certificate
     *
     * @author accline2, mjcheim
     *
     */
    public class CertificateElement {

        /**
         * patient
         */
        private Patient      patient;

        /**
         * date vaccine was given
         */
        private String       dateTime;

        /**
         * number of doses
         */
        private String       numDoses;

        /**
         * username of vaccinator
         */
        private String       vaccinator;

        /**
         * type of vaccine given
         */
        private String       vaccineType;

        /**
         * The vaccine visit
         */
        private VaccineVisit visit;

        /**
         * Constructor for CertificateElement
         *
         * @param patient
         *            patient to set
         * @param dateTime
         *            dateTime to set
         * @param numDoses
         *            numDoses to set
         * @param vaccinator
         *            vaccinator to set
         * @param vaccineType
         *            vaccineType to set
         * @param visit
         *            the vaccination visit to set
         */
        public CertificateElement ( final Patient patient, final String dateTime, final String numDoses,
                final String vaccinator, final String vaccineType, final VaccineVisit visit ) {
            super();
            this.patient = patient;
            this.dateTime = dateTime;
            this.numDoses = numDoses;
            this.vaccinator = vaccinator;
            this.vaccineType = vaccineType;
            this.visit = visit;
        }

        /**
         * Gets the patient
         *
         * @return the patient
         */
        public Patient getPatient () {
            return patient;
        }

        /**
         * Sets the patient
         *
         * @param patient
         *            the patient to set
         */
        public void setPatient ( final Patient patient ) {
            this.patient = patient;
        }

        /**
         * Gets the date/time of the visit
         *
         * @return the dateTime
         */
        public String getDateTime () {
            return dateTime;
        }

        /**
         * Sets the date/time of the visit
         *
         * @param dateTime
         *            the dateTime to set
         */
        public void setDateTime ( final String dateTime ) {
            this.dateTime = dateTime;
        }

        /**
         * Gets the number of doses
         *
         * @return the numDoses
         */
        public String getNumDoses () {
            return numDoses;
        }

        /**
         * Sets the number of doses
         *
         * @param numDoses
         *            the numDoses to set
         */
        public void setNumDoses ( final String numDoses ) {
            this.numDoses = numDoses;
        }

        /**
         * Gets the vaccinator
         *
         * @return the vaccinator
         */
        public String getVaccinator () {
            return vaccinator;
        }

        /**
         * Sets the vaccinator
         *
         * @param vaccinator
         *            the vaccinator to set
         */
        public void setVaccinator ( final String vaccinator ) {
            this.vaccinator = vaccinator;
        }

        /**
         * Gets the vaccine type
         *
         * @return the vaccineType
         */
        public String getVaccineType () {
            return vaccineType;
        }

        /**
         * Sets the vaccine type
         *
         * @param vaccineType
         *            the vaccineType to set
         */
        public void setVaccineType ( final String vaccineType ) {
            this.vaccineType = vaccineType;
        }

        /**
         * Gets the vaccination visit
         *
         * @return the visit
         */
        public VaccineVisit getVisit () {
            return visit;
        }

        /**
         * Sets the vaccination visit
         *
         * @param visit
         *            the visit to set
         */
        public void setVisit ( final VaccineVisit visit ) {
            this.visit = visit;
        }

    }

    /**
     * Helper method for when there are 2 doses
     *
     * @author skadhir, accline2
     *
     */
    public class Certificate {
        /**
         * The list of certificate elements
         */
        private List<CertificateElement> celist;
        /**
         * The patient who's certificate we're building
         */
        private Patient                  patient;

        /**
         * Constructor for the certificate
         *
         * @param celist
         *            list of certificate elements
         * @param patient
         *            the patient who's certificate we're building
         */
        public Certificate ( final List<CertificateElement> celist, final Patient patient ) {
            super();
            this.celist = celist;
            this.patient = patient;
        }

        /**
         * Gets the list holding the certificate elements
         *
         * @return the celist
         */
        public List<CertificateElement> getCelist () {
            return celist;
        }

        /**
         * Sets the certificate element list
         *
         * @param celist
         *            the celist to set
         */
        public void setCelist ( final List<CertificateElement> celist ) {
            this.celist = celist;
        }

        /**
         * Gets the patient
         *
         * @return the patient
         */
        public Patient getPatient () {
            return patient;
        }

        /**
         * Sets the patient
         *
         * @param patient
         *            the patient to set
         */
        public void setPatient ( final Patient patient ) {
            this.patient = patient;
        }

    }
}
