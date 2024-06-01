package edu.ncsu.csc.iTrust2.forms;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

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
public class PatientAdvocateUserForm {
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
     * Username of the user
     */
    @NotEmpty
    @Length ( max = 20 )
    private String      username;

    /**
     * Password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      password;

    /***
     * Confirmation password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      password2;

    /**
     * Role of the user
     */
    @NotEmpty
    private Set<String> roles;

    /**
     * Whether the User is enabled or not
     */
    private String      enabled;

    /**
     * First name of the Personnel
     */
    @Length ( max = 20 )
    private String firstName;

    /**
     * Last name of the Personnel
     */
    @Length ( max = 30 )
    private String lastName;

    /**
     * Address1 of the Personnel
     */
    @Length ( max = 50 )
    private String address1;

    /**
     * Address2 of the Personnel
     */
    @Length ( max = 50 )
    private String address2;

    /**
     * City of the Personnel
     */
    @Length ( max = 15 )
    private String city;

    /**
     * State of the Personnel
     */
    @Length ( min = 2, max = 2 )
    private String state;

    /**
     * Zip of the Personnel
     */
    @Length ( min = 5, max = 10 )
    private String zip;

    /**
     * Phone of the Personnel
     */
    @Pattern ( regexp = "(^[0-9]{3}-[0-9]{3}-[0-9]{4}$)", message = "Phone number must be formatted as xxx-xxx-xxxx" )
    private String phone;

    /**
     * Email of the Personnel
     */
    @Length ( max = 30 )
    private String email;

    /**
     * Get Username of the personnel
     *
     * @return The Personnel's username
     */
    public String getUsername () {
        return username;
    }

    /**
     * Set username of the Personenl
     *
     * @param username
     *            The personnel's username
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Get the first name of the personnel
     *
     * @return Personnel's first name
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the First Name of the Personnel
     *
     * @param firstName
     *            New FirstName to set
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the last name of the Personnel
     *
     * @return The last name of the Personnel
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of the Personnel
     *
     * @param lastName
     *            New last name to set
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get Address1 of the Personnel
     *
     * @return Address1 of Personnel
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set Address1 of Personnel
     *
     * @param address1
     *            New Address1 to set
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get Address2 of Personnel
     *
     * @return Address2 of personnel
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set Address2 of Personnel
     *
     * @param address2
     *            New Address2 to set
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the City of the Personnel
     *
     * @return Personnel's city of their address
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of the Personnel
     *
     * @param city
     *            New city to set
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the Personnel's state
     *
     * @return State of the Personnel
     */
    public String getState () {
        return state;
    }

    /**
     * Set the State of the Personnel
     *
     * @param state
     *            New State to set
     */
    public void setState ( final String state ) {
        this.state = state;
    }

    /**
     * Get the ZIP of the Personnel
     *
     * @return The Personnel's ZIP
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the ZIP of the Personnel
     *
     * @param zip
     *            New ZIP to set
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the Phone number of the personnel
     *
     * @return The Personnel's phone number
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the Phone Number of the Personnel
     *
     * @param phone
     *            New phone number to set
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the Email of the Personnel
     *
     * @return Personnel's email
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the Email of the Personnel
     *
     * @param email
     *            The Personnel's new email
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }
    
    /**
     * Creates a PatientAdvocateForm object. For initializing a blank form
     */
    public PatientAdvocateUserForm () {

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

    /**
     * Gets the Password provided in the form
     *
     * @return Password provided
     */
    public String getPassword () {
        return password;
    }

    /**
     * Sets the Password for the User on the form.
     *
     * @param password
     *            Password of the user
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Gets the Password confirmation provided in the form
     *
     * @return Password confirmation provided
     */
    public String getPassword2 () {
        return password2;
    }

    /**
     * Sets the Password confirmation in the Form
     *
     * @param password2
     *            The password confirmation
     */
    public void setPassword2 ( final String password2 ) {
        this.password2 = password2;
    }

    /**
     * Role of the new User
     *
     * @return The User's role
     */
    public Set<String> getRoles () {
        return roles;
    }

    /**
     * Sets the Role of the new User
     *
     * @param roles
     *            Roles of the user
     */
    public void setRoles ( final Set<String> roles ) {
        this.roles = roles;
    }

    /**
     * Adds the provided Role to this User
     *
     * @param role
     *            The Role to add
     */
    public void addRole ( final String role ) {
        if ( null == this.roles ) {
            this.roles = new HashSet<String>();
        }
        this.roles.add( role );
    }

    /**
     * Gets whether the new User created is to be enabled or not
     *
     * @return Whether the User is enabled
     */
    public String getEnabled () {
        return enabled;
    }

    /**
     * Retrieves whether the user is enabled or not
     *
     * @param enabled
     *            New Enabled setting
     */
    public void setEnabled ( final String enabled ) {
        this.enabled = enabled;
    }

}
