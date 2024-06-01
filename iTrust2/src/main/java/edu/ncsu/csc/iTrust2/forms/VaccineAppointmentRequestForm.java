package edu.ncsu.csc.iTrust2.forms;

/**
 * Extension of AppointmentRequestForm which adds additional functionality for
 * vaccine-specific appointments.
 *
 * @author accline2, mjcheim
 *
 */
public class VaccineAppointmentRequestForm extends AppointmentRequestForm {

    /** String enumeration of vaccine requested. */
    private String vaccineType;

    /** String enumeration of patient's vaccine status. */
    private String vaccineStatus;

    /**
     * Empty constructor for Hibernate.
     */
    public VaccineAppointmentRequestForm () {

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
     * Gets the vaccine status
     *
     * @return the vaccineStatus
     */
    public String getVaccineStatus () {
        return vaccineStatus;
    }

    /**
     * Sets the vaccine status
     *
     * @param vaccineStatus
     *            the vaccineStatus to set
     */
    public void setVaccineStatus ( final String vaccineStatus ) {
        this.vaccineStatus = vaccineStatus;
    }

}
