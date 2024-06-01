/**
 *
 */
package edu.ncsu.csc.iTrust2.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc.iTrust2.forms.PatientAdvocateForm;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateUserForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;

/**
 * This class defines a PateintAdvocate. PatientAdvocates have additional
 * information that are different from Personnel, but have all the identifiable
 * information of Personnel. They have a list of ID's that are references to
 * associations. The associations hold the permissions that Patients give to
 * PatientAdvocates. You can add and delete associations.
 *
 *
 * @author Team5
 *
 */
@Entity
public class PatientAdvocate extends Personnel {
    /**
     * Middle name of the patient advocate
     */
    private String middleName;

    /**
     * Nickname of the patient advocate
     */
    private String nickName;

    /**
     * List of all associations between this patient advocate and various
     * patients
     */
    @ElementCollection
    List<Long>     associationIDs;

    /**
     * For Hibernate
     */
    public PatientAdvocate () {
        associationIDs = new ArrayList<Long>();
    }

    /**
     * Constructor to build a Patient Advocate out of a UserForm
     *
     * @param uf
     *            Form to build Patient Advocate
     */
    public PatientAdvocate ( final UserForm uf ) {
        super( uf );
        if ( getRoles().contains( Role.ROLE_HCP ) ) {
            throw new IllegalArgumentException( "Attempted to create a PatientAdvocate record for a HCP user!" );
        }
        associationIDs = new ArrayList<Long>();
    }

    /**
     * Create a new Patient Advocate based off of the PatientAdvocateForm
     *
     * @param form
     *            the filled-in patient advocate form with patient advocate
     *            information
     * @return `this` PatientAdvocate, after updating from form
     */
    public PatientAdvocate update ( final PatientAdvocateForm form ) {
        super.setFirstName( form.getFirstName() );
        setMiddleName( form.getMiddleName() );
        super.setLastName( form.getLastName() );
        setNickName( form.getNickname() );
        super.setAddress1( form.getAddress1() );
        super.setAddress2( form.getAddress2() );
        super.setCity( form.getCity() );
        super.setState( State.valueOf( form.getState() ) );
        super.setZip( form.getZip() );
        super.setPhone( form.getPhone() );
        super.setEmail( form.getEmail() );

        return this;
    }

    /**
     * Create a new Patient Advocate based off of the PatientAdvocateUserForm
     *
     * @param form
     *            the filled-in patient advocate user form with patient advocate
     *            information
     * @return `this` PatientAdvocate, after updating from form
     */
    public PatientAdvocate updateAll ( final PatientAdvocateUserForm form ) {
        super.setFirstName( form.getFirstName() );
        setMiddleName( form.getMiddleName() );
        super.setLastName( form.getLastName() );
        setNickName( form.getNickname() );
        super.setAddress1( form.getAddress1() );
        super.setAddress2( form.getAddress2() );
        super.setCity( form.getCity() );
        super.setState( State.valueOf( form.getState() ) );
        super.setZip( form.getZip() );
        super.setPhone( form.getPhone() );
        super.setEmail( form.getEmail() );
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        super.setPassword( pe.encode( form.getPassword() ) );
        super.setRoles( form.getRoles().stream().map( Role::valueOf ).collect( Collectors.toSet() ) );
        super.setEnabled( form.getEnabled().toLowerCase().equals( "1" ) ? 1 : 0 );
        return this;
    }

    /**
     * Retrieves the middle name of this patient advocate
     *
     * @return the middle name of this patient advocate
     */
    public String getMiddleName () {
        return middleName;
    }

    /**
     * Set the middle name of this patient advocate
     *
     * @param middleName
     *            the middle name to set this patient advocate to
     */
    public void setMiddleName ( final String middleName ) {
        this.middleName = middleName;
    }

    /**
     * Retrieves the nickname of this patient advocate
     *
     * @return the nickname of this patient advocate
     */
    public String getNickName () {
        return nickName;
    }

    /**
     * Set the nickname of this patient advocate
     *
     * @param nickName
     *            the nickname to set this patient advocate to
     */
    public void setNickName ( final String nickName ) {
        this.nickName = nickName;
    }

    @Override
    public String toString () {
        return "PatientAdvocate [middleName=" + middleName + ", nickName=" + nickName + "]";
    }

    /**
     * Gets the list of patient to patient advocate associations
     *
     * @return returns a list of associations
     */
    public List<Long> getAssociations () {
        return associationIDs;
    }

    /**
     * Add an additional association between a patient and patient advocate
     *
     * @param paAssociation
     *            association to add to the patient advocate
     */
    public void addAssociation ( final Long paAssociation ) {
        associationIDs.add( paAssociation );
    }

    /**
     * Removes the specified association that matches the id
     *
     * @param id
     *            id of the association to remove
     *
     * @return returns true if id was removed
     */
    public boolean deleteAssocation ( final Long id ) {
        return associationIDs.remove( id );
    }

}
