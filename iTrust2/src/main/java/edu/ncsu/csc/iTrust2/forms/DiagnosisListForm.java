package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;
import java.time.ZonedDateTime;

import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.User;

/**
 * Used to store Diagnoses for a Patient
 *
 * @author Kai Presler-Marshall
 *
 */
public class DiagnosisListForm implements Serializable {

    private static final long serialVersionUID = -3984755238529999272L;

    /**
     * Represents the time of the Diagnosis
     */
    private ZonedDateTime     visitDate;

    /** HCP who made the diagnosis */
    private User              hcp;

    /** Note for the diagnosis */
    private String            note;

    /** ID for the diagnosis */
    private Long              id;

    /** Associated code for the diagnosis */
    private ICDCode           code;

    /**
     * Constructor
     *
     * @param diag
     *            Diagnosis to build this Form from
     */
    public DiagnosisListForm ( final Diagnosis diag ) {
        this.id = diag.getId();
        this.visitDate = diag.getVisit().getDate();
        this.hcp = diag.getVisit().getHcp();
        this.note = diag.getNote();
        this.code = diag.getCode();
    }

    /**
     * Time of the Diagnosis/visit
     *
     * @return Time
     */
    public ZonedDateTime getVisitDate () {
        return visitDate;
    }

    /**
     * Sets the time of the Diagnosis/visit
     *
     * @param visitDate
     *            New date to set
     */
    public void setVisitDate ( final ZonedDateTime visitDate ) {
        this.visitDate = visitDate;
    }

    /**
     * Gets the HCP who made the diagnosis
     *
     * @return Associated HCP
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * Sets the HCP who made this diagnosis
     *
     * @param hcp
     *            New HCP
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
    }

    /**
     * Gets the note for this Diagnosis
     *
     * @return Note
     */
    public String getNote () {
        return note;
    }

    /**
     * Sets the Note for this diagnosis
     *
     * @param note
     *            New note
     */
    public void setNote ( final String note ) {
        this.note = note;
    }

    /**
     * Gets the ID of this diagnosis
     *
     * @return ID
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the Diagnosis
     *
     * @param id
     *            new ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the ICDCode for the Diagnosis
     *
     * @return ICDCode
     */
    public ICDCode getCode () {
        return code;
    }

    /**
     * Sets the ICDCode of this diagnosis
     *
     * @param code
     *            new code to set
     */
    public void setCode ( final ICDCode code ) {
        this.code = code;
    }
}
