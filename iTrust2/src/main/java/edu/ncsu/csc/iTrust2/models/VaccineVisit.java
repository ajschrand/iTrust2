package edu.ncsu.csc.iTrust2.models;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;

/**
 * DomainObject used to hold information regarding a VaccineVisit.
 *
 * @author accline2
 * @author mjcheim
 */
@Entity
public class VaccineVisit extends DomainObject {

    /**
     * Request associated with visit
     */
    @ManyToOne
    private VaccineAppointmentRequest correspondingRequest;

    /**
     * Patient associated with the visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private Patient                   patient;

    /**
     * Vaccinator responsible for visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "vaccinator_id", columnDefinition = "varchar(100)" )
    private User                      vaccinator;

    /**
     * The id of this vaccine visit
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long                      id;

    /**
     * The date of this Vaccine visit
     */
    @NotNull
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime             date;

    /** type of vaccine used in this visit. */
    @NotNull
    @ManyToOne
    private VaccineType               vaccineType;

    /**
     * The dose number for this visit
     */
    @NotNull
    private Integer                   dose;

    /**
     * The date of follow up Vaccine visit
     */
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime             followUpDate;

    /**
     * The CPT codes associated with this visit
     */
    @ManyToMany ( cascade = CascadeType.ALL )
    private List<CPTCode>             cptCodes;

    /**
     * Hibernate constructor
     */
    public VaccineVisit () {

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
     * Gets corresponding appointment request
     *
     * @return the correspondingRequest
     */
    public VaccineAppointmentRequest getCorrespondingRequest () {
        return correspondingRequest;
    }

    /**
     * Sets the corresponding appointment request
     *
     * @param correspondingRequest
     *            the correspondingRequest to set
     */
    public void setCorrespondingRequest ( final VaccineAppointmentRequest correspondingRequest ) {
        this.correspondingRequest = correspondingRequest;
    }

    /**
     * Get the vaccinator
     *
     * @return the vaccinator
     */
    public User getVaccinator () {
        return vaccinator;
    }

    /**
     * Sets the vaccinator
     *
     * @param vaccinator
     *            the vaccinator to set
     */
    public void setVaccinator ( final User vaccinator ) {
        this.vaccinator = vaccinator;
    }

    /**
     * Gets the id
     *
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the date/time of the visit
     *
     * @return the date
     */
    public ZonedDateTime getDate () {
        return date;
    }

    /**
     * Sets the date/time of the visit
     *
     * @param date
     *            the date to set
     */
    public void setDate ( final ZonedDateTime date ) {
        this.date = date;
    }

    /**
     * Gets the vaccine type
     *
     * @return the vaccineType
     */
    public VaccineType getVaccineType () {
        return vaccineType;
    }

    /**
     * Sets the vaccine type
     *
     * @param vaccineType
     *            the vaccineType to set
     */
    public void setVaccineType ( final VaccineType vaccineType ) {
        this.vaccineType = vaccineType;
    }

    /**
     * Gets the dose number of the visit
     *
     * @return the dose
     */
    public Integer getDose () {
        return dose;
    }

    /**
     * Sets the dose number
     *
     * @param dose
     *            the dose to set
     */
    public void setDose ( final Integer dose ) {
        this.dose = dose;
    }

    /**
     * Gets the followup date
     *
     * @return the followUpDate
     */
    public ZonedDateTime getFollowUpDate () {
        return followUpDate;
    }

    /**
     * Sets the follow up date
     *
     * @param followUpDate
     *            the followUpDate to set
     */
    public void setFollowUpDate ( final ZonedDateTime followUpDate ) {
        this.followUpDate = followUpDate;
    }

    /**
     * Returns the VaccineVisitForm's cptCodes
     *
     * @return the cptCodes
     */
    public List<CPTCode> getCptCodes () {
        return cptCodes;
    }

    /**
     * Sets the VaccineVisitForm's cptCodes
     *
     * @param cptCodes
     *            the cptCodes to set
     */
    public void setCptCodes ( final List<CPTCode> cptCodes ) {
        this.cptCodes = cptCodes;
    }

    /**
     * Check to see if the patient is old enough for the vaccine.
     *
     * @throws IllegalArgumentException
     *             if patient is too young
     */
    public void validateMinAge () {
        final LocalDate ld = LocalDate.now().minusYears( vaccineType.getMinAge() );
        if ( !patient.getDateOfBirth().isBefore( ld ) ) {
            throw new IllegalArgumentException( "Patient not old enough for this Vaccine" );
        }
    }

    /**
     * Check to see if the patient is too old for the vaccine.
     *
     * @throws IllegalArgumentException
     *             if patient is too old
     */
    public void validateMaxAge () {
        final LocalDate ld = LocalDate.now().minusYears( vaccineType.getMaxAge() );
        if ( !patient.getDateOfBirth().isAfter( ld ) ) {
            throw new IllegalArgumentException( "Patient too old for this Vaccine" );
        }
    }
}
