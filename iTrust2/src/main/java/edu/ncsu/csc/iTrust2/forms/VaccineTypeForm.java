package edu.ncsu.csc.iTrust2.forms;

import edu.ncsu.csc.iTrust2.models.VaccineType;

/**
 * Form used by frontend and API calls which holds VaccineType values in String
 * format.
 *
 * @author skadhir
 * @author mpenaga
 *
 */
public class VaccineTypeForm {
    /**
     * The ID of the vaccine
     */
    private Long    id;

    /**
     * Name of the vaccine
     */
    private String  name;
    /**
     * The min age of the vaccine
     */
    private int     minAge;

    /**
     * The max age of the vaccine
     */
    private int     maxAge;
    /**
     * Number of doses for the vaccine
     */
    private int     numDoses;
    /**
     * Days between doses (if applicable)
     */
    private int     daysBetweenDoses;
    /**
     * Determines if the vaccine is avaliable
     */
    private boolean isAvailable;
    /**
     * The inventory amount
     */
    private int     inventoryAmount;

    /**
     * Empty constructor for filling in fields without a Vaccine Type object.
     */
    public VaccineTypeForm () {
    }

    /**
     * Create a form from a VaccineType object
     *
     * @param vaccineType
     *            vaccine to populate form with
     */
    public VaccineTypeForm ( final VaccineType vaccineType ) {
        setId( vaccineType.getId() );
        setName( vaccineType.getName() );
        setMinAge( vaccineType.getMinAge() );
        setMaxAge( vaccineType.getMaxAge() );
        setNumDoses( vaccineType.getNumDoses() );
        setDaysBetweenDoses( vaccineType.getDaysBetweenDoses() );
        setIsAvailable( ( vaccineType.getIsAvailable() ) );
        setInventoryAmount( vaccineType.getInventoryAmount() );

    }

    /**
     * Gets the ID of the vaccine
     *
     * @return the id of the vaccine
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the vaccine
     *
     * @param id
     *            id of the vaccine
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets vaccine name
     *
     * @return the name of the vaccine
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the vaccine
     *
     * @param name
     *            the name of the vaccine
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the min age
     *
     * @return the min age
     */
    public int getMinAge () {
        return minAge;
    }

    /**
     * Sets the min age
     *
     * @param minAge
     *            the min age
     */
    public void setMinAge ( final int minAge ) {
        this.minAge = minAge;
    }

    /**
     * Gets the max age of the vaccine
     *
     * @return the max age
     */
    public int getMaxAge () {
        return maxAge;
    }

    /**
     * Sets the max age
     *
     * @param maxAge
     *            the max age
     */
    public void setMaxAge ( final int maxAge ) {
        this.maxAge = maxAge;
    }

    /**
     * Gets the number of doses
     *
     * @return number of doses
     */
    public int getNumDoses () {
        return numDoses;
    }

    /**
     * Sets the number of doses
     *
     * @param numDoses
     *            the number of doses
     */
    public void setNumDoses ( final int numDoses ) {
        this.numDoses = numDoses;
    }

    /**
     * Gets the days between doses
     *
     * @return days between doses
     */
    public int getDaysBetweenDoses () {
        return daysBetweenDoses;
    }

    /**
     * Sets the days between doses
     *
     * @param daysBetweenDoses
     *            days between doses
     */
    public void setDaysBetweenDoses ( final int daysBetweenDoses ) {
        this.daysBetweenDoses = daysBetweenDoses;
    }

    /**
     * Determines if the vaccine is available
     *
     * @return true if it is available
     */
    public boolean getIsAvailable () {
        return isAvailable;
    }

    /**
     * Sets the availability
     *
     * @param isAvailable
     *            true if available
     */
    public void setIsAvailable ( final boolean isAvailable ) {
        this.isAvailable = isAvailable;
    }

    /**
     * Gets the inventory amount of a vaccine
     *
     * @return the inventory amount
     */
    public int getInventoryAmount () {
        return inventoryAmount;
    }

    /**
     * Sets the inventory amount
     *
     * @param inventoryAmount
     *            the inventory amount
     */
    public void setInventoryAmount ( final int inventoryAmount ) {
        this.inventoryAmount = inventoryAmount;
    }

}
