package edu.ncsu.csc.iTrust2.models;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.iTrust2.models.enums.WaitTime;

/**
 * A SatisfactionSurvey keeps track of how satisfied a Patient was with their
 * visit and the HCP who provided their care
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class SatisfactionSurvey extends DomainObject {

    /**
     * The id of this SatisfactionSurvey
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long     id;

    /**
     * The patient of this SatisfactionSurvey
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User     patient;

    /**
     * The hcp of this SatisfactionSurvey
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User     hcp;

    /**
     * The wait time in the waiting room
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private WaitTime waitingRoomTime;

    /**
     * The wait time in the examination room
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private WaitTime examinationResponseTime;

    /**
     * The overall satisfaction of the patient
     */
    @Min ( 0 )
    @Max ( 5 )
    @Nonnull
    private Integer  visitSatisfaction;

    /**
     * The satisfaction of the patient with the treatment they received.
     */
    @Min ( 0 )
    @Max ( 5 )
    @Nonnull
    private Integer  treatmentSatisfaction;

    /**
     * The Comments of this Satisfaction Survey
     */
    private String   comments;

    /**
     * empty constructor for Hibernate
     */
    public SatisfactionSurvey () {
    }

    /**
     * Get the id of this Satisfacion survey
     *
     * @return the id of this Satisfacion survey
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Gets patient who filled the satisfaction survey
     *
     * @return User the user who filled the survey
     */
    public User getPatient () {
        return this.patient;
    }

    /**
     * Sets the user who filled the satisfaction survey
     *
     * @param patient
     *            the user who filled the survey
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * The HCP who the survey was about
     *
     * @return User the user who the survey is about
     */
    public User getHcp () {
        return this.hcp;
    }

    /**
     * Sets the HCP who the survey was about
     *
     * @param hcp
     *            the HCP who the survey is about
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
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

}
