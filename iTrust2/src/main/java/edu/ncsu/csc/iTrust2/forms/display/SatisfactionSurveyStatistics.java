package edu.ncsu.csc.iTrust2.forms.display;

import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.WaitTime;

/**
 * Provides overall statisfaction statistics for a HCP, with averages for
 * different metrics and comments
 *
 * @author Kai Presler-Marshall
 *
 */
public class SatisfactionSurveyStatistics {

    /**
     * The HCP for this statistics
     */
    private User                         hcp;

    /**
     * The wait time in the waiting room
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private WaitTime                     averageWaitingRoomTime;

    /**
     * The wait time in the examination room
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private WaitTime                     averageExaminationResponseTime;

    /**
     * The overall satisfaction of the patient
     */
    @Min ( 0 )
    @Max ( 5 )
    @Nonnull
    private Integer                      averageVisitSatisfaction;

    /**
     * The satisfaction of the patient with the treatment they received.
     */
    @Min ( 0 )
    @Max ( 5 )
    @Nonnull
    private Integer                      averageTreatmentSatisfaction;

    /**
     * The number of surveys used to calculate this statistics
     */
    private Integer                      numberOfSurveys;

    /**
     * The Comments of this Satisfaction Survey
     */
    private List<SatisfactionSurveyNote> notes;

    /**
     * empty constructor for Hibernate
     */
    public SatisfactionSurveyStatistics () {
    }

    /**
     * HCP user on this statistics
     *
     * @return HCP
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * HCP used on this stats
     *
     * @param hcp
     *            HCP
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
    }

    /**
     * Sets the averageWaitingRoomTime to the new value
     *
     * @param averageWaitingRoomTime
     *            the updated value
     */
    public void setAverageWaitingRoomTime ( final WaitTime averageWaitingRoomTime ) {
        this.averageWaitingRoomTime = averageWaitingRoomTime;
    }

    /**
     * sets the averageExaminationResponseTime to the new value
     *
     * @param averageExaminationResponseTime
     *            the updated value
     */
    public void setAverageExaminationResponseTime ( final WaitTime averageExaminationResponseTime ) {
        this.averageExaminationResponseTime = averageExaminationResponseTime;
    }

    /**
     * sets the averageVisitSatisfaction to the new value
     *
     * @param averageVisitSatisfaction
     *            the updated value
     */
    public void setAverageVisitSatisfaction ( final Integer averageVisitSatisfaction ) {
        this.averageVisitSatisfaction = averageVisitSatisfaction;
    }

    /**
     * sets the averageTreatmentSatisfaction to the new value
     *
     * @param averageTreatmentSatisfaction
     *            the updated value
     */
    public void setAverageTreatmentSatisfaction ( final Integer averageTreatmentSatisfaction ) {
        this.averageTreatmentSatisfaction = averageTreatmentSatisfaction;
    }

    /**
     * sets the notes to the new value
     *
     * @param notes
     *            the updated value
     */
    public void setNotes ( final List<SatisfactionSurveyNote> notes ) {
        this.notes = notes;
    }

    /**
     * returns the averageWaitingRoomTime
     *
     * @return the averageWaitingRoomTime
     */
    public WaitTime getAverageWaitingRoomTime () {
        return averageWaitingRoomTime;
    }

    /**
     * returns the averageExaminationResponseTime
     *
     * @return the averageExaminationResponseTime
     */
    public WaitTime getAverageExaminationResponseTime () {
        return averageExaminationResponseTime;
    }

    /**
     * returns the averageVisitSatisfaction
     *
     * @return the averageVisitSatisfaction
     */
    public Integer getAverageVisitSatisfaction () {
        return averageVisitSatisfaction;
    }

    /**
     * returns the averageTreatmentSatisfaction
     *
     * @return the averageTreatmentSatisfaction
     */
    public Integer getAverageTreatmentSatisfaction () {
        return averageTreatmentSatisfaction;
    }

    /**
     * Get the number of surveys
     *
     * @return Survey count
     */
    public Integer getNumberOfSurveys () {
        return numberOfSurveys;
    }

    /**
     * Set the number of surveys used for this statistic
     *
     * @param numberOfSurveys
     *            Survey count
     */
    public void setNumberOfSurveys ( final Integer numberOfSurveys ) {
        this.numberOfSurveys = numberOfSurveys;
    }

    /**
     * returns the notes
     *
     * @return the notes
     */
    public List<SatisfactionSurveyNote> getNotes () {
        return notes;
    }

}
