package edu.ncsu.csc.iTrust2.forms;

import javax.validation.constraints.NotEmpty;

/**
 * This class defines a form that holds getters and setters to be used in
 * creating the actual object the form is based off of. In this case it is used
 * to create a PatientAdvocateAssociation.
 *
 * @author noahalexander
 *
 */
public class PatientAdvocateAssociationForm {

    /**
     * Username of the Patient
     */
    @NotEmpty
    private String  patient;

    /**
     * Username of the PatientAdvocate
     */
    @NotEmpty
    private String  patientAdvocate;

    /**
     * Billing permission
     */
    private boolean billing;

    /**
     * Visit permission
     */
    private boolean visit;

    /**
     * Prescription permission
     */
    private boolean prescription;

    /**
     * Creates a PatientAdvocateAssociationForm object. For initializing a blank
     * form
     */
    public PatientAdvocateAssociationForm () {

    }

    /**
     * Gets the Patient username
     *
     * @return the patient username
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the Patient's username
     *
     * @param patient
     *            the patient username to set
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Gets the PatientAdvocate username
     *
     * @return the patientAdvocate username
     */
    public String getPatientAdvocate () {
        return patientAdvocate;
    }

    /**
     * Sets the PatientAdvocate username
     *
     * @param patientAdvocate
     *            the advocate username to set
     */
    public void setPatientAdvocate ( final String patientAdvocate ) {
        this.patientAdvocate = patientAdvocate;
    }

    /**
     * Gets the billing permission
     *
     * @return returns the billing permission
     */
    public boolean isBilling () {
        return billing;
    }

    /**
     * Sets the billing permission
     *
     * @param billing
     *            the billing permission to set
     */
    public void setBilling ( final boolean billing ) {
        this.billing = billing;
    }

    /**
     * Gets the visit permission
     *
     * @return returns the visit permission
     */
    public boolean isVisit () {
        return visit;
    }

    /**
     * Sets the visit permission
     *
     * @param visit
     *            the visit permission to set
     */
    public void setVisit ( final boolean visit ) {
        this.visit = visit;
    }

    /**
     * Gets the prescription permission
     *
     * @return the prescription permission
     */
    public boolean isPrescription () {
        return prescription;
    }

    /**
     * Sets the prescription permission
     *
     * @param prescription
     *            the prescription permission to set
     */
    public void setPrescription ( final boolean prescription ) {
        this.prescription = prescription;
    }

}
