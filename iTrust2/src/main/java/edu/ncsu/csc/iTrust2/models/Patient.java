package edu.ncsu.csc.iTrust2.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.LocalDateAdapter;
import edu.ncsu.csc.iTrust2.forms.PatientForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.models.enums.VaccinationStatus;

/**
 * Represents a Patient stored in the iTrust2 system
 *
 * @author Kai Presler-Marshall
 * @author Lauren Murillo
 *
 */
@Entity
public class Patient extends User {

    /**
     * The first name of this patient
     */
    @Length ( min = 1 )
    private String            firstName;

    /**
     * The preferred name of this patient
     */
    @Length ( max = 20 )
    private String            preferredName;

    /**
     * The last name of this patient
     */
    @Length ( min = 1 )
    private String            lastName;

    /**
     * The email address of this patient
     */
    @Length ( max = 30 )
    private String            email;

    /**
     * The address line 1 of this patient
     */
    @Length ( max = 50 )
    private String            address1;

    /**
     * The address line 2 of this patient
     */
    @Length ( max = 50 )
    private String            address2;

    /**
     * The city of residence of this patient
     */
    @Length ( max = 15 )
    private String            city;

    /**
     * The state of residence of this patient
     */
    @Enumerated ( EnumType.STRING )
    private State             state;

    /**
     * The zip code of this patient
     */
    @Length ( min = 5, max = 10 )
    private String            zip;

    /**
     * The phone number of this patient
     */
    @Length ( min = 12, max = 12 )
    private String            phone;

    /** The number of doses of the vaccine the patient has */
    private String            doses;

    /** The vaccine the patient has recieved (if applicable) */
    @ManyToOne
    private VaccineType       vaccine;

    /**
     * The birthday of this patient
     */
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = LocalDateConverter.class )
    @JsonAdapter ( LocalDateAdapter.class )
    private LocalDate         dateOfBirth;

    /**
     * The date of death of this patient
     */
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = LocalDateConverter.class )
    @JsonAdapter ( LocalDateAdapter.class )
    private LocalDate         dateOfDeath;

    /**
     * The cause of death of this patient
     */
    private String            causeOfDeath;

    /**
     * The blood type of this patient
     */
    @Enumerated ( EnumType.STRING )
    private BloodType         bloodType;

    /**
     * The ethnicity of this patient
     */
    @Enumerated ( EnumType.STRING )
    private Ethnicity         ethnicity;

    /**
     * The gender of this patient
     */
    @Enumerated ( EnumType.STRING )
    private Gender            gender;

    /**
     * The vaccination status of this patient
     */
    @Enumerated ( EnumType.STRING )
    private VaccinationStatus status;

    /**
     * A list of IDs that reference PatientAdvocateAssociations ID's. These
     * associations list the permissions that the Patient gives ton
     * PatientAdvocates
     */
    @ElementCollection
    private final List<Long>  associationIDs;

    /**
     * For Hibernate
     */
    public Patient () {
        associationIDs = new ArrayList<Long>();
    }

    /**
     * Creates a Patient from the provided UserForm
     *
     * @param uf
     *            UserForm to build a Patient from
     */
    public Patient ( final UserForm uf ) {
        super( uf );
        if ( !getRoles().contains( Role.ROLE_PATIENT ) ) {
            throw new IllegalArgumentException( "Attempted to create a Patient record for a non-Patient user!" );
        }
        associationIDs = new ArrayList<Long>();
    }

    /**
     * Updates this Patient based on the provided UserForm
     *
     * @param form
     *            Form to update from
     * @return updated patient
     */
    public Patient update ( final PatientForm form ) {

        setFirstName( form.getFirstName() );
        setPreferredName( form.getPreferredName() );
        setLastName( form.getLastName() );
        setEmail( form.getEmail() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.parse( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );

        setDateOfBirth( LocalDate.parse( form.getDateOfBirth() ) );

        // Patient can't set their date of death
        if ( form.getDateOfDeath() != null ) {
            setDateOfDeath( LocalDate.parse( form.getDateOfDeath() ) );
            setCauseOfDeath( form.getCauseOfDeath() );
        }

        setBloodType( BloodType.parse( form.getBloodType() ) );

        setEthnicity( Ethnicity.parse( form.getEthnicity() ) );

        setGender( Gender.parse( form.getGender() ) );

        setDoses( form.getDoses() );

        return this;
    }

    /**
     * Gets the first name of this Patient
     *
     * @return First name
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Sets the first name of this Patient.
     *
     * @param firstName
     *            New first name
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Gets the preferred name (if any) of this patient
     *
     * @return Preferred name
     */
    public String getPreferredName () {
        return preferredName;
    }

    /**
     * Sets the preferred name of this patient
     *
     * @param preferredName
     *            New first name
     */
    public void setPreferredName ( final String preferredName ) {
        this.preferredName = preferredName;
    }

    /**
     * Gets the last name of this patient
     *
     * @return Patient's last name
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Gets the last name for this Patient
     *
     * @param lastName
     *            Last name to return
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Gets the Email of this Patient
     *
     * @return Email to return
     */
    public String getEmail () {
        return email;
    }

    /**
     * Sets the Email of this patient
     *
     * @param email
     *            New email
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

    /**
     * Gets the first line of the address for this patient
     *
     * @return First line of address
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Sets the first line of the address for this patient
     *
     * @param address1
     *            New address line
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Gets the second line of the address for this patient
     *
     * @return Second line of address
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Sets the second line of the address for this patient
     *
     * @param address2
     *            New address line
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Gets the city for this patient
     *
     * @return City
     */
    public String getCity () {
        return city;
    }

    /**
     * Sets the city for this patient
     *
     * @param city
     *            New city to set
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Gets the state for this patient
     *
     * @return State
     */
    public State getState () {
        return state;
    }

    /**
     * Sets the state for this patient
     *
     * @param state
     *            New state to set
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Gets the zip for this patient
     *
     * @return ZIP
     */
    public String getZip () {
        return zip;
    }

    /**
     * Sets the ZIP for this patient
     *
     * @param zip
     *            New ZIP to set
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Gets the phone number for this patient
     *
     * @return Phone number
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Sets the phone number for this patient
     *
     * @param phone
     *            New phone number to set
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Gets the patient's birthdate
     *
     * @return Patient's birthdate
     */
    public LocalDate getDateOfBirth () {
        return dateOfBirth;
    }

    /**
     * Sets the patient's birthdate
     *
     * @param dateOfBirth
     *            New birthdate
     */
    public void setDateOfBirth ( final LocalDate dateOfBirth ) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the patient's date of death
     *
     * @return Patient's date of death
     */
    public LocalDate getDateOfDeath () {
        return dateOfDeath;
    }

    /**
     * Sets the patient's date of death
     *
     * @param dateOfDeath
     *            New date of death
     */
    public void setDateOfDeath ( final LocalDate dateOfDeath ) {
        this.dateOfDeath = dateOfDeath;
    }

    /**
     * Gets the patient's cause of death
     *
     * @return Cause of death
     */
    public String getCauseOfDeath () {
        return causeOfDeath;
    }

    /**
     * Sets the patient's cause of death
     *
     * @param causeOfDeath
     *            New cause of death
     */
    public void setCauseOfDeath ( final String causeOfDeath ) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * Gets the Patient's bloodtype
     *
     * @return Bloodtype
     */
    public BloodType getBloodType () {
        return bloodType;
    }

    /**
     * Sets the patient's bloodtype
     *
     * @param bloodType
     *            New bloodtype
     */
    public void setBloodType ( final BloodType bloodType ) {
        this.bloodType = bloodType;
    }

    /**
     * Gets the Patient's vaccination status
     *
     * @return the patient's vaccination status
     */
    public VaccinationStatus getVaccinationStatus () {
        return status;
    }

    /**
     * Sets the vaccination status of this patient
     *
     * @param status
     *            the patient's vaccination status
     */
    public void setVaccinationStatus ( final VaccinationStatus status ) {
        this.status = status;
    }

    /**
     * Gets the vaccine type the patient has recieved (if applicable)
     *
     * @return the vaccine
     */
    public VaccineType getVaccineType () {
        return vaccine;
    }

    /**
     * Sets the vaccine for the patient (if applicable)
     *
     * @param vaccine
     *            the vaccine
     */
    public void setVaccineType ( final VaccineType vaccine ) {
        if ( vaccine != null ) {
            this.vaccine = vaccine;
        }
    }

    /**
     * Gets number of doses a patient has recieved
     *
     * @return the number of doses
     */
    public String getDoses () {
        return doses;
    }

    /**
     * Sets the number of doses for a patient
     *
     * @param doses
     *            the number of doses
     */
    public void setDoses ( final String doses ) {
        if ( doses != null ) {
            this.doses = doses;
        }
    }

    /**
     * Gets the patient's ethnicity
     *
     * @return Ethnicity
     */
    public Ethnicity getEthnicity () {
        return ethnicity;
    }

    /**
     * Sets the patient's ethnicity
     *
     * @param ethnicity
     *            New ethnicity to set
     */
    public void setEthnicity ( final Ethnicity ethnicity ) {
        this.ethnicity = ethnicity;
    }

    /**
     * Gets the patient's gender
     *
     * @return Gender
     */
    public Gender getGender () {
        return gender;
    }

    /**
     * Sets the patient's gender
     *
     * @param gender
     *            New gender to set
     */
    public void setGender ( final Gender gender ) {
        this.gender = gender;
    }

    /**
     * Add the given patient advocate association to the list of associations
     *
     * @param paAssociation
     *            patient advocate association to add
     */
    public void addAssociation ( final Long paAssociation ) {
        associationIDs.add( paAssociation );
    }

    /**
     * Delete the association that has the given id
     *
     * @param id
     *            id of the association to delete
     * 
     * @return returns true if the association was deleted, false otherwise
     */
    public boolean deleteAssociation ( final Long id ) {
        return associationIDs.remove( id );
    }

    /**
     * Gets the list of patient to patient advocate association IDs
     *
     * @return returns a list of associations
     */
    public List<Long> getAssociations () {
        return associationIDs;
    }

}
