package edu.ncsu.csc.iTrust2.models.enums;

/**
 * For keeping track of various types of Payment. Used in Payment.
 *
 * @author yli246
 *
 */
public enum PaymentType {

    /**
     * credit
     */
    CREDIT ( "CREDIT" ),

    /**
     * cash
     */
    CASH ( "CASH" ),

    /**
     * check
     */
    CHECK ( "CHECK" ),

    /**
     * insurance
     */
    INSURANCE ( "INSURANCE" ),
    /**
     * Delinquent
     */
    DELINQUENT ( "DELINQUENT" );

    /**
     * Type of Payment
     */
    private String type;

    /**
     * Constructor for PaymentType.
     *
     * @param type
     *            Type of the PaymentType to create
     */
    private PaymentType ( final String type ) {
        this.type = type;
    }

    /**
     * Retrieve the type of the Payment
     *
     * @return Type of the Payment
     */
    public String getType () {
        return this.type;
    }

    /**
     * Parses a String into the appropriate PaymentType enum value
     *
     * @param paymentType
     *            String to parse
     * @return Parsed enum type
     */
    public static PaymentType parse ( final String paymentType ) {
        for ( final PaymentType pay : values() ) {
            if ( pay.toString().equals( paymentType ) ) {
                return pay;
            }
        }
        return CREDIT;
    }
}
