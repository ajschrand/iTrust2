package edu.ncsu.csc.iTrust2.forms.display;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Shows information that an Admin can view about a Satisfaction, with average
 * satisfaction scores and any notes
 *
 * @author Kai
 *
 */
public class SatisfactionSurveyNote {

    /**
     * The overall satisfaction of the patient
     */
    @Min ( 0 )
    @Max ( 5 )
    private Integer averageSatisfaction;

    /**
     * The Comments of the Satisfaction Surveys
     */
    private String  comments;

    /**
     * Set the average satisfaction
     *
     * @param averageSatisfaction
     *            Average satisfaction
     */
    public void setAverageSatisfaction ( final Integer averageSatisfaction ) {
        this.averageSatisfaction = averageSatisfaction;
    }

    /**
     * Returns the average satisfaction rating of the hcp
     *
     * @return the average satisfaction rating of the hcp
     */
    public Integer getAverageSatisfaction () {
        return averageSatisfaction;
    }

    /**
     * sets the comments to the given value
     *
     * @param comments
     *            the new value
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * returns the comments
     *
     * @return the string with the comments
     */
    public String getComments () {
        return comments;
    }

}
