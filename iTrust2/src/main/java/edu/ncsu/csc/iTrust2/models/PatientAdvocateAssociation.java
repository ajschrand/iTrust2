package edu.ncsu.csc.iTrust2.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.forms.PatientAdvocateAssociationForm;

/**
 * This class defines and association between a Patient and a PatientAdvocate.
 * It defines an id (to be used in the database) the username of the Patient,
 * the usernameo of the PatientAdvocate, and the permissions allowed. Class has
 * getters and setters, as well as ways to construct it with an association
 * form.
 *
 * @author Team5
 *
 */
@Entity
public class PatientAdvocateAssociation extends DomainObject {

    /**
     * ID of the association to be used and generated in the database.
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Username of the Patient
     */
    String       patientUsername;

    /**
     * Username of the PatientAdvocate
     */
    String       advocateUsername;

    /**
     * Boolean value for if the PatientAdvocate is allowed to view the Patient's
     * billing information
     */
    boolean      billing;

    /**
     * Boolean value for if the PatientAdvocate is allowed to view the Patient's
     * visit information
     */
    boolean      visit;

    /**
     * Boolean value for if the PatientAdvocate is allowed to view the Patient's
     * prescription information
     */
    boolean      prescription;

    /**
     * For hidernate
     */
    public PatientAdvocateAssociation () {

    }

    /**
     * Constructor for creating an association with only knowing the Patient
     * username and PatientAdvocate username. Sets all permissions to false.
     *
     * @param patient
     *            name of the Patient
     * @param patientAdvocate
     *            name of the PatientAdvocate
     */
    public PatientAdvocateAssociation ( final String patient, final String patientAdvocate ) {
        setPatient( patient );
        setPatientAdvocate( patientAdvocate );
        setBilling( false );
        setVisit( false );
        setPrescription( false );
    }

    /**
     * Creates an association from a PatientAdvocateAssociationForm.
     *
     * @param paForm
     *            association form
     */
    public PatientAdvocateAssociation ( final PatientAdvocateAssociationForm paForm ) {
        setPatient( paForm.getPatient() );
        setPatientAdvocate( paForm.getPatientAdvocate() );
        setBilling( paForm.isBilling() );
        setVisit( paForm.isVisit() );
        setPrescription( paForm.isPrescription() );
    }

    /**
     * Updates an association with a PatientAdvocateAssociationForm.
     *
     * @param paForm
     *            the association form to update with
     * @return the association after the update
     */
    public PatientAdvocateAssociation update ( final PatientAdvocateAssociationForm paForm ) {
        if ( paForm.getPatient() != null ) {
            setPatient( paForm.getPatient() );
        }

        if ( paForm.getPatientAdvocate() != null ) {
            setPatientAdvocate( paForm.getPatientAdvocate() );
        }

        setBilling( paForm.isBilling() );
        setVisit( paForm.isVisit() );
        setPrescription( paForm.isPrescription() );

        return this;
    }

    /**
     * Gets the Patient's username
     *
     * @return returns the patient username
     */
    public String getPatient () {
        return patientUsername;
    }

    /**
     * Sets the Patient's username
     *
     * @param patientUsername
     *            username of the Patient
     */
    public void setPatient ( final String patientUsername ) {
        this.patientUsername = patientUsername;
    }

    /**
     * Gets the PatientAdvocat's username
     *
     * @return returns the patient advocate name
     */
    public String getPatientAdvocate () {
        return advocateUsername;
    }

    /**
     * Sets the PatientAdvocate's username
     *
     * @param advocateUsername
     *            username of the PatientAdvocate
     */
    public void setPatientAdvocate ( final String advocateUsername ) {
        this.advocateUsername = advocateUsername;
    }

    /**
     * Gets the billing permission
     *
     * @return returns the billing permission
     */
    public boolean getBilling () {
        return billing;
    }

    /**
     * Sets the billing permission
     *
     * @param billing
     *            billing permission to set
     */
    public void setBilling ( final boolean billing ) {
        this.billing = billing;
    }

    /**
     * Gets the visit permission
     *
     * @return returns the visit permission
     */
    public boolean getVisit () {
        return visit;
    }

    /**
     * Sets the visit permission
     *
     * @param visit
     *            visit permission to set
     */
    public void setVisit ( final boolean visit ) {
        this.visit = visit;
    }

    /**
     * Gets the prescription permission
     *
     * @return returns the prescription permission
     */
    public boolean getPrescription () {
        return prescription;
    }

    /**
     * Sets the prescription permission
     *
     * @param prescription
     *            prescription permission to set
     */
    public void setPrescription ( final boolean prescription ) {
        this.prescription = prescription;
    }

    @Override
    public Serializable getId () {
        return id;
    }

    @Override
    public int hashCode () {
        return Objects.hash( advocateUsername, billing, id, patientUsername, prescription, visit );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( obj == null ) {
            return false;
        }

        if ( getClass() != obj.getClass() ) {
            return false;
        }

        final PatientAdvocateAssociation other = (PatientAdvocateAssociation) obj;
        return Objects.equals( advocateUsername, other.advocateUsername ) && billing == other.billing
                && Objects.equals( id, other.id ) && Objects.equals( patientUsername, other.patientUsername )
                && prescription == other.prescription && visit == other.visit;
    }
}
