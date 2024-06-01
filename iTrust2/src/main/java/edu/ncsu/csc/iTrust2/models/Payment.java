package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.models.enums.PaymentType;

/**
 * Represents a Payment stored in the iTrust2 system
 *
 * @author yli246
 *
 */
@Entity
public class Payment extends DomainObject {

    /**
     * ID of this Payment
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long        id;

    /**
     * Amount of this Payment
     */
    private int         amount;

    /**
     * The type of the Payment
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private PaymentType type;

    /**
     * Empty constructor for Hibernate
     */
    public Payment () {

    }

    /**
     * constructor of paymentForm
     *
     * @param form
     *            form to construct from
     */
    public Payment ( final PaymentForm form ) {
        setAmount( form.getAmount() );
        setPaymentType( PaymentType.parse( form.getType() ) );
    }

    /**
     * get id
     *
     * @return the id
     */
    @Override
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
     * get payment type
     *
     * @return the type
     */
    public PaymentType getPaymentType () {
        return type;
    }

    /**
     * set type
     *
     * @param type
     *            the type to set
     */
    public void setPaymentType ( final PaymentType type ) {
        this.type = type;
    }

}
