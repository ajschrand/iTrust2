package edu.ncsu.csc.iTrust2.forms;

import javax.validation.constraints.Min;

import edu.ncsu.csc.iTrust2.models.Payment;

/**
 * Intermediate form for adding or editing Payment. Used to create and serialize
 * Payment.
 *
 * @author yli246
 *
 */
public class PaymentForm {

    /**
     * ID of this Payment
     */
    private Long   id;

    /**
     * Amount of this Payment
     */
    @Min ( 0 )
    private int    amount;

    /**
     * The type of the Payment
     */
    private String type;

    /**
     * Empty constructor for Hibernate
     */
    public PaymentForm () {

    }

    /**
     * constructor of paymentForm
     *
     * @param payment
     *            Payment to construct from
     */
    public PaymentForm ( final Payment payment ) {
        setId( payment.getId() );
        setAmount( payment.getAmount() );
        if ( payment.getPaymentType() != null ) {
            setType( payment.getPaymentType().toString() );
        }
    }

    /**
     * get id
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * set id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * get amount
     *
     * @return the amount
     */
    public int getAmount () {
        return amount;
    }

    /**
     * set amount
     *
     * @param amount
     *            the amount to set
     */
    public void setAmount ( final int amount ) {
        this.amount = amount;
    }

    /**
     * get type
     *
     * @return the type
     */
    public String getType () {
        return type;
    }

    /**
     * set type
     *
     * @param type
     *            the type to set
     */
    public void setType ( final String type ) {
        this.type = type;
    }
}
