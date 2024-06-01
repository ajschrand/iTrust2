package edu.ncsu.csc.iTrust2.models;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;

/**
 * Represents a Bill
 *
 * @author nhwiblit
 *
 */
@Entity
public class Bill extends DomainObject {

    /** ID of this Bill */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long          id;

    /**
     * Patient associated with the visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User          patient;

    /**
     * HCP responsible for visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User          hcp;

    /**
     * The date of this Bill
     */
    @NotNull
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime date;

    /**
     * The status of this bill
     */
    @NotNull
    private BillStatus    status;

    /**
     * The remaining cost for this Bill
     */
    @Min ( 0 )
    private double        remainingCost;

    /**
     * The amount already paid for this bill
     */
    private double        amountPaid;

    /**
     * The list of payments for this bill
     */
    @ManyToMany ( cascade = CascadeType.ALL )
    @JsonManagedReference
    private List<Payment> paymentList;

    /**
     * The CPT codes associated with this visit
     */
    @ManyToMany ( cascade = CascadeType.ALL )
    @JsonManagedReference
    private List<CPTCode> cptCodes;

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public Bill () {

    }

    /**
     * Updates the bill with the provided payment
     *
     * @param pf
     *            The payment form to be used to update the bill
     */
    public void updateBill ( final Payment pf ) {
        if ( getRemainingCost() != 0 ) {
            final double curAmmount = pf.getAmount();
            paymentList.add( pf );
            setRemainingCost( getRemainingCost() - curAmmount );
            setAmountPaid( curAmmount + getAmountPaid() );

            final ZonedDateTime delinquencyDate = date.plusDays( 60 );

            final ZonedDateTime now = ZonedDateTime.now();

            if ( delinquencyDate.isAfter( now ) ) {
                this.status = BillStatus.DELINQUENT;
            }
            else if ( getAmountPaid() != 0 ) {
                this.status = BillStatus.PARTIALLY_PAID;
            }
            else {
                this.status = BillStatus.UNPAID;
            }

        }
        else {
            this.status = BillStatus.PAID;
        }
    }

    /**
     * Adds a payment object to the payment list
     *
     * @param p
     *            The payment object being added
     */
    public void addPayment ( final Payment p ) {
        this.paymentList.add( p );
    }

    /**
     * Gets the patient this bill is for
     *
     * @return the patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the patient this bill is for
     *
     * @param patient
     *            the patient to set
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Gets the hcp that is part of the visit
     *
     * @return the hcp
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * Sets the hcp that is part of this visit
     *
     * @param hcp
     *            the hcp to set
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
    }

    /**
     * Gets the date of this bill
     *
     * @return the date
     */
    public ZonedDateTime getDate () {
        return date;
    }

    /**
     * Sets the date of this bill
     *
     * @param date
     *            the date to set
     */
    public void setDate ( final ZonedDateTime date ) {
        this.date = date;
    }

    /**
     * Gets the status of this bill
     *
     * @return the status
     */
    public BillStatus getStatus () {
        return status;
    }

    /**
     * Sets the status of this bill
     *
     * @param status
     *            the status to set
     */
    public void setStatus ( final BillStatus status ) {
        this.status = status;
    }

    /**
     * Gets the remaining cost of this bill
     *
     * @return the remainingCost
     */
    public double getRemainingCost () {
        return remainingCost;
    }

    /**
     * Sets the remaining cost of this bill
     *
     * @param remainingCost
     *            the remainingCost to set
     */
    public void setRemainingCost ( final double remainingCost ) {
        this.remainingCost = remainingCost;
    }

    /**
     * Gets the amount of this bill that has been paid
     *
     * @return the amountPaid
     */
    public double getAmountPaid () {
        return amountPaid;
    }

    /**
     * Sets the amount of this bill that has been paid
     *
     * @param amountPaid
     *            the amountPaid to set
     */
    public void setAmountPaid ( final double amountPaid ) {
        this.amountPaid = amountPaid;
    }

    /**
     * Gets the list of payments for this bill
     *
     * @return the paymentList
     */
    public List<Payment> getPaymentList () {
        return paymentList;
    }

    /**
     * Sets the list of payments for this bill
     *
     * @param paymentList
     *            the paymentList to set
     */
    public void setPaymentList ( final List<Payment> paymentList ) {
        this.paymentList = paymentList;
    }

    /**
     * Gets the list of CPT codes for this bill
     *
     * @return the cptCodes
     */
    public List<CPTCode> getCptCodes () {
        return cptCodes;
    }

    /**
     * Sets the list of CPT codes for this bill
     *
     * @param cptCodes
     *            the cptCodes to set
     */
    public void setCptCodes ( final List<CPTCode> cptCodes ) {
        this.cptCodes = cptCodes;
    }

    /**
     * Gets the id for this bill
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the id for this bill
     *
     * @return the id
     */
    @Override
    public Serializable getId () {
        return id;
    }

}
