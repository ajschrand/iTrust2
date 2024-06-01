package edu.ncsu.csc.iTrust2.forms;

import org.hibernate.validator.constraints.Length;

/**
 * Form for registering a user as a Patient Advocate in the iTrust2 system. This
 * form can be used to edit existing information. Since a Patient Advocate is
 * also a Personnel this form extends off of the PersonnelForm and adds new
 * fields not related to all other Personnel.
 *
 * @author Noah Alexander (ngalexa2)
 *
 */
public class PatientAdvocateForm extends PersonnelForm {
    /**
     * Middle name of the Patient Advocate
     */
    @Length ( max = 30 )
    private String middleName;

    /**
     * Nickname of the Patient Advocate
     */
    @Length ( max = 20 )
    private String nickname;

    /**
     * Creates a PatientAdvocateForm object. For initializing a blank form
     */
    public PatientAdvocateForm () {

    }

    /**
     * Returns the middle name of the Patient Advocate
     *
     * @return the middleName
     */
    public String getMiddleName () {
        return middleName;
    }

    /**
     * Setter to set the middle name of the Patient Advocate
     *
     * @param middleName
     *            sets the middle name of the Patient Advocate
     */
    public void setMiddleName ( final String middleName ) {
        this.middleName = middleName;
    }

    /**
     * Returns the middle name of the Patient Advocate
     *
     * @return the nickname
     */
    public String getNickname () {
        return nickname;
    }

    /**
     * Setter to set the nickname of the Patient Advocate
     *
     * @param nickname
     *            sets the nickname of the Patient Advocate
     */
    public void setNickname ( final String nickname ) {
        this.nickname = nickname;
    }

}
