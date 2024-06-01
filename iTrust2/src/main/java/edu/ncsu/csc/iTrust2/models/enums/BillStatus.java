package edu.ncsu.csc.iTrust2.models.enums;

/**
 * Statues for Bills
 *
 * @author nhwiblit
 *
 */
public enum BillStatus {
    /**
     * Unpaid bill
     */
    UNPAID ( "unpaid" ),
    /**
     * Partially paid bill
     */
    PARTIALLY_PAID ( "partially paid" ),
    /**
     * Fully paid bill
     */
    PAID ( "paid" ),
    /**
     * Delinquent bill
     */
    DELINQUENT ( "delinquent" );

    /**
     * Code of the bill status
     */
    private String code;

    /**
     * Creates a bill status from a string code
     *
     * @param code
     *            code of the bill status
     */
    private BillStatus ( String code ) {
        this.code = code;
    }

    /**
     * Gets the string Code of the bill status
     *
     * @return Code of the bill status
     */
    public String getCode () {
        return code;
    }
}
