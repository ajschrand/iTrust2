package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;

import edu.ncsu.csc.iTrust2.models.Diagnosis;

/**
 * Diagnosis Form represents a Diagnosis object, but is easier to use for
 * deserialisation for API endpoints
 *
 * @author Kai Presler-Marshall
 *
 */
public class DiagnosisForm implements Serializable {

    private static final long serialVersionUID = 7366969934788466076L;

    /** ID of the OfficeVisit this is associated with */
    private Long              visit;

    /** Notes for the Diagnosis */
    private String            note;

    /** ID of this Diagnosis */
    private Long              id;

    /** Code associated with this visit */
    private String            code;

    /** Default constructor, use setters after this */
    public DiagnosisForm () {

    }

    /**
     * Builds a DiagnosisForm from a Diagnosis
     * 
     * @param diag
     *            Diagnosis to use
     */
    public DiagnosisForm ( final Diagnosis diag ) {
        /* May not be attached to a visit yet */
        if ( null != diag.getVisit() ) {
            visit = diag.getVisit().getId();
        }

        note = diag.getNote();
        id = diag.getId();
        code = diag.getCode().getCode();
    }

    /**
     * Gets the ID of the associated visit
     *
     * @return Visit ID
     */
    public Long getVisit () {
        return visit;
    }

    /**
     * Sets the ID of the associated visit
     *
     * @param visit
     *            ID of the visit
     */
    public void setVisit ( final Long visit ) {
        this.visit = visit;
    }

    /**
     * Gets the notes of this Diagnosis
     *
     * @return notes
     */
    public String getNote () {
        return note;
    }

    /**
     * Sets the note on this Diagnosis
     *
     * @param note
     *            Note to set
     */
    public void setNote ( final String note ) {
        this.note = note;
    }

    /**
     * Gets the ID of this Diagnosis
     *
     * @return id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of this diagnosis
     *
     * @param id
     *            New id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the diagnosis code
     *
     * @return Diagnosis code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the diagnosis code
     *
     * @param code
     *            New diagnosis code
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

}
