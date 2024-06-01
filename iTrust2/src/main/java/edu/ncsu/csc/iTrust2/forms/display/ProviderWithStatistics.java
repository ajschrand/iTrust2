package edu.ncsu.csc.iTrust2.forms.display;

import edu.ncsu.csc.iTrust2.models.Personnel;

/**
 * Class that has provider details with the statistics embedded
 *
 *
 * @author bvolpat
 * @author Kai Presler-Marshall
 */
public class ProviderWithStatistics {

    /**
     * Represents information about a HCP and satisfaction statistics from
     * patients
     *
     * @param personnel
     *            Personnel the statistics are for
     * @param statistics
     *            Detailed statistics
     */
    public ProviderWithStatistics ( final Personnel personnel, final SatisfactionSurveyStatistics statistics ) {
        this.username = personnel.getUsername();
        this.firstName = personnel.getFirstName();
        this.lastName = personnel.getLastName();
        this.hospitalId = personnel.getHospitalId();
        this.statistics = statistics;
    }

    /**
     * The username of the user
     */
    private String                       username;

    /**
     * The first name of the personnel
     */
    private String                       firstName;

    /**
     * The last name of the personnel
     */
    private String                       lastName;

    /**
     * The id of the hospital the personnel works at
     */
    private String                       hospitalId;

    /**
     * Statistics for the provider
     */
    private SatisfactionSurveyStatistics statistics;

    /**
     * Get user name
     *
     * @return Username
     */
    public String getUsername () {
        return username;
    }

    /**
     * Set user name
     *
     * @param username
     *            Username
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Get First Name
     *
     * @return First Name
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set First Name
     *
     * @param firstName
     *            First Name
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get Last Name
     *
     * @return Last Name
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set Last Name
     *
     * @param lastName
     *            Last Name
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get Hospital Id
     *
     * @return Hospital ID
     */
    public String getHospitalId () {
        return hospitalId;
    }

    /**
     * Set Hospital Id
     *
     * @param hospitalId
     *            Hospital Id
     */
    public void setHospitalId ( final String hospitalId ) {
        this.hospitalId = hospitalId;
    }

    /**
     * Get Statistics for Surveys
     *
     * @return Statistics
     */
    public SatisfactionSurveyStatistics getStatistics () {
        return statistics;
    }

    /**
     * Set Statistics for Surveys
     *
     * @param statistics
     *            Statistics
     */
    public void setStatistics ( final SatisfactionSurveyStatistics statistics ) {
        this.statistics = statistics;
    }
}
