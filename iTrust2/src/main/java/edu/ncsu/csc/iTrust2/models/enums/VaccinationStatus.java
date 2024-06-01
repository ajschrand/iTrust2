package edu.ncsu.csc.iTrust2.models.enums;

/**
 * Enumeration representing the vaccination status of a patient.
 *
 * @author Lauren Murillo
 *
 */
public enum VaccinationStatus {

    /** patient not yet started vaccination process */

    NOT_VACCINATED ( 1 ),

    /** patient received first dose of vaccine */
    FIRST_DOSE ( 2 ),

    /** patient is fully vaccinated */
    FULLY_VACCINATED ( 3 );

    /**
     * Code of the status
     */
    private int code;

    /**
     * Create a Status from the numerical code.
     *
     * @param code
     *            Code of the Status
     */
    private VaccinationStatus ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical Code of the Status
     *
     * @return Code of the Status
     */
    public int getCode () {
        return code;
    }
}
