package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import edu.ncsu.csc.iTrust2.models.SatisfactionSurvey;
import edu.ncsu.csc.iTrust2.models.enums.WaitTime;

/**
 * Form for Satisfaction Surveys
 * 
 * @author abhirud
 */
public class SatisfactionSurveyForm implements Serializable {

    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor so that we can create an SatisFfaction Survey form for
     * the user to fill out
     */
    public SatisfactionSurveyForm () {
    }

    /**
     * The ID of the office visit related to this survey
     */
    private Long     officeVisitId;

    /**
     * The wait time in the waiting room
     */
    private WaitTime waitingRoomTime;

    /**
     * The wait time in the examination room
     */
    private WaitTime examinationResponseTime;

    /**
     * The overall satisfaction of the patient
     */
    @Min ( 0 )
    @Max ( 5 )
    private Integer  visitSatisfaction;

    /**
     * The satisfaction of the patient with the treatment they received.
     */
    @Min ( 0 )
    @Max ( 5 )
    private Integer  treatmentSatisfaction;

    /**
     * The Comments of this Satisfaction Survey
     */
    private String   comments;

    /**
     * Get the id of the office visit for this survey
     *
     * @return ID of Office Visit
     */
    public Long getOfficeVisitId () {
        return officeVisitId;
    }

    /**
     * Set the ID of the office visit for this survey
     *
     * @param officeVisitId
     *            ID of Office Visit
     */
    public void setOfficeVisitId ( final Long officeVisitId ) {
        this.officeVisitId = officeVisitId;
    }

    /**
     * Returns the enum for the time the Patient waited in the waiting room
     *
     * @return WaitTime the enum for the time the Patient waited in the waiting
     *         room
     */
    public WaitTime getWaitingRoomTime () {
        return this.waitingRoomTime;
    }

    /**
     * Sets the enum for the time the Patient waited in the waiting room
     *
     * @param waitingRoomTime
     *            the new enum for the time the Patient waited in the waiting
     *            room
     */
    public void setWaitingRoomTime ( final WaitTime waitingRoomTime ) {
        this.waitingRoomTime = waitingRoomTime;
    }

    /**
     * Returns the enum for the time the Patient waited in the examination room
     *
     * @return WaitTime the enum for the time the Patient waited in the
     *         examination room
     */
    public WaitTime getExaminationResponseTime () {
        return this.examinationResponseTime;
    }

    /**
     * Sets the enum for the time the Patient waited in the examination room
     *
     * @param examinationResponseTime
     *            the new enum for the time the Patient waited in the
     *            examination room
     */
    public void setExaminationResponseTime ( final WaitTime examinationResponseTime ) {
        this.examinationResponseTime = examinationResponseTime;
    }

    /**
     * Returns the visit satisfaction rating for the patient
     *
     * @return Integer the visit satisfaction rating for the patient
     */
    public Integer getVisitSatisfaction () {
        return this.visitSatisfaction;
    }

    /**
     * Sets the visit satisfaction rating
     *
     * @param visitSatisfaction
     *            the new visit satisfaction rating
     */
    public void setVisitSatisfaction ( final Integer visitSatisfaction ) {
        this.visitSatisfaction = visitSatisfaction;
    }

    /**
     * Returns the treatment satisfaction rating for the patient
     *
     * @return Integer the treatment satisfaction rating for the patient
     */
    public Integer getTreatmentSatisfaction () {
        return this.treatmentSatisfaction;
    }

    /**
     * Sets the treatment satisfaction rating
     *
     * @param treatmentSatisfaction
     *            the new treatment satisfaction rating
     */
    public void setTreatmentSatisfaction ( final Integer treatmentSatisfaction ) {
        this.treatmentSatisfaction = treatmentSatisfaction;
    }

    /**
     * Gets the comments for the Satisfaction survey
     *
     * @return String the comments for the Satisfaction survey
     */
    public String getComments () {
        return this.comments;
    }

    /**
     * Sets the comments for the Satisfaction survey
     *
     * @param comments
     *            the new comments for the Satisfaction survey
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Constructor to build a SatisfactionSurveyForm from a SatisfactionSurvey
     * 
     * @param ss
     *            SatisfactionSurvey to use
     */
    public SatisfactionSurveyForm ( final SatisfactionSurvey ss ) {

        setWaitingRoomTime( ss.getWaitingRoomTime() );
        setExaminationResponseTime( ss.getExaminationResponseTime() );
        setVisitSatisfaction( ss.getVisitSatisfaction() );
        setTreatmentSatisfaction( ss.getTreatmentSatisfaction() );
        setComments( ss.getComments() );

    }

}
