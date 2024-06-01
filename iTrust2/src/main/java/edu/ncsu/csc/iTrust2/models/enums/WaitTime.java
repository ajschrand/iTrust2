package edu.ncsu.csc.iTrust2.models.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum of all the different wait times for the Satisfaction Survey
 *
 * @author abhirud
 *
 */
public enum WaitTime {

    /**
     * Less than 5 minutes
     */
    LESS_THAN_FIVE ( 1 ),

    /**
     * Between 5 minutes and 10 minutes
     */
    FIVE_TO_TEN ( 2 ),

    /**
     * Between 10 minutes and 15 minutes
     */
    TEN_TO_FIFTEEN ( 3 ),

    /**
     * Between 15 minutes and 20 minutes
     */
    FIFTEEN_TO_TWENTY ( 4 ),

    /**
     * Between 20 minutes and 25 minutes
     */
    TWENTY_TO_TWENTY_FIVE ( 5 ),

    /**
     * Over 25 minutes
     */
    OVER_TWENTY_FIVE ( 6 );

    /**
     * Numerical code of the WaitTime
     */
    private int                           code;

    /**
     * Tracks the possible wait times and provides a lookup to retrieve enum
     * values from the corresponding Integers
     */
    private static Map<Integer, WaitTime> reverseLookup = new HashMap<>();
    static {
        for ( final WaitTime m : EnumSet.allOf( WaitTime.class ) ) {
            reverseLookup.put( m.code, m );
        }
    }

    /**
     * Creates the WaitTime from its code.
     *
     * @param code
     *            Code of the WaitTime
     */
    WaitTime ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical code of the WaitTime
     *
     * @return Code of the WaitTime
     */
    public int getCode () {
        return code;
    }

    /**
     * Get a WaitTime instance for the given code
     *
     * @param code
     *            Code to search
     * @return Wait Time
     */
    public static WaitTime getByCode ( final int code ) {
        return reverseLookup.get( code );
    }
}
