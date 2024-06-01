package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc.iTrust2.models.VaccineVisit;

/**
 * Form used by frontend and API calls which holds VaccineVisit values in String
 * format.
 *
 * @author accline2
 * @author mjcheim
 */
public class VaccineVisitForm implements Serializable {

    /** serializing id */
    private static final long serialVersionUID = 1L;

    /** patient of visit */
    private String            patient;

    /** id of visit */
    private String            id;

    /** vaccinator of visit */
    private String            vaccinator;

    /** date of visit */
    private String            dateTime;

    /** type of vaccine given */
    private String            vaccine;

    /** which dose number this visit is for (1,2,3...) */
    private String            dose;

    /** date of followup vaccine if required */
    private String            followUpDate;

    /** id of appointmentrequest associated with visit */
    private String            requestId;

    /** The CPT Codes associated with visit */
    private List<Long>        cptCodes;

    /**
     * Hibernate constructor
     */
    public VaccineVisitForm () {

    }

    /**
     * Create a form from a VaccineVisit object
     *
     * @param ov
     *            visit to populate form with
     */
    public VaccineVisitForm ( final VaccineVisit ov ) {
        setPatient( ov.getPatient().getUsername() );
        setId( ov.getId().toString() );
        setVaccinator( ov.getVaccinator().getUsername() );
        setDateTime( ov.getDate().toString() );
        setCptCodes( new ArrayList<Long>() );
        setVaccine( ov.getVaccineType().toString() );
        setDose( ov.getDose().toString() );
        if ( ov.getCorrespondingRequest() != null ) {
            setRequestId( ov.getCorrespondingRequest().getId() + "" );
        }
        else {
            setRequestId( "" );
        }
        if ( ov.getFollowUpDate() != null ) {
            setFollowUpDate( ov.getFollowUpDate().toString() );
        }
        else {
            setFollowUpDate( "" );
        }
    }

    /**
     * The ID of the appointment
     *
     * @return the requestId
     */
    public String getRequestId () {
        return requestId;
    }

    /**
     * Sets the request id of the appointment
     *
     * @param requestId
     *            the requestId to set
     */
    public void setRequestId ( final String requestId ) {
        this.requestId = requestId;
    }

    /**
     * Gets the patient
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the patient
     *
     * @param patient
     *            the patient to set
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Gets the visit id
     *
     * @return the id
     */
    public String getId () {
        return id;
    }

    /**
     * Sets the visit id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final String id ) {
        this.id = id;
    }

    /**
     * Gets tha vaccinator of the visit
     *
     * @return the vaccinator
     */
    public String getVaccinator () {
        return vaccinator;
    }

    /**
     * Sets the vaccinator of the appointment
     *
     * @param vaccinator
     *            the vaccinator to set
     */
    public void setVaccinator ( final String vaccinator ) {
        this.vaccinator = vaccinator;
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
     * Gest the vaccine
     *
     * @return the vaccine
     */
    public String getVaccine () {
        return vaccine;
    }

    /**
     * Sets the vaccine
     *
     * @param vaccine
     *            the vaccine to set
     */
    public void setVaccine ( final String vaccine ) {
        this.vaccine = vaccine;
    }

    /**
     * Gets the doses
     *
     * @return the dose
     */
    public String getDose () {
        return dose;
    }

    /**
     * Sets the doses
     *
     * @param dose
     *            the dose to set
     */
    public void setDose ( final String dose ) {
        this.dose = dose;
    }

    /**
     * Gets the followupdate
     *
     * @return the followUpDate
     */
    public String getFollowUpDate () {
        return followUpDate;
    }

    /**
     * Set the followup Date
     *
     * @param followUpDate
     *            the followUpDate to set
     */
    public void setFollowUpDate ( final String followUpDate ) {
        this.followUpDate = followUpDate;
    }

    /**
     * Returns the VaccineVisitForm's cptCodes
     *
     * @return the cptCodes
     */
    public List<Long> getCptCodes () {
        return cptCodes;
    }

    /**
     * Sets the VaccineVisitForm's cptCodes
     *
     * @param cptCodes
     *            the cptCodes to set
     */
    public void setCptCodes ( final List<Long> cptCodes ) {
        this.cptCodes = cptCodes;
    }

    /**
     * Gets the serial version id
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid () {
        return serialVersionUID;
    }

}
