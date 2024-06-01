package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Extension of AppointmentRequest which adds additional functionality for
 * vaccine-specific appointments.
 *
 * @author accline2, mjcheim
 */
@Entity
public class VaccineAppointmentRequest extends AppointmentRequest {

    /** type of vaccine requested. */
    @NotNull
    @ManyToOne
    private VaccineType vaccine;

    /**
     * Empty constructor for Hibernate.
     */
    public VaccineAppointmentRequest () {

    }

    /**
     * Gets the vaccine
     *
     * @return the vaccine
     */
    public VaccineType getVaccine () {
        return vaccine;
    }

    /**
     * Sets the vaccine
     *
     * @param vaccine
     *            the vaccine to set
     */
    public void setVaccine ( final VaccineType vaccine ) {
        this.vaccine = vaccine;
    }

}
