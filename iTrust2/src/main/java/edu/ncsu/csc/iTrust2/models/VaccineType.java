package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.ncsu.csc.iTrust2.forms.VaccineTypeForm;

/**
 * This class creates the vaccine type object
 *
 * @author skadhir
 * @author mpenaga
 *
 */
@Entity
public class VaccineType extends DomainObject {
    /**
     * Empty constructor for hiberate
     */
    public VaccineType () {

    }

    /**
     * Create a form from a VaccineType object
     *
     * @param form
     *            vaccine to populate form with
     */
    public VaccineType ( final VaccineTypeForm form ) {
        setId( form.getId() );
        setName( form.getName() );
        setMinAge( form.getMinAge() );
        setMaxAge( form.getMaxAge() );
        setNumDoses( form.getNumDoses() );
        setDaysBetweenDoses( form.getDaysBetweenDoses() );
        setIsAvailable( ( form.getIsAvailable() ) );
        setInventoryAmount( form.getInventoryAmount() );
    }

    /**
     * ID of this drug
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;
    /**
     * Name of the vaccine
     */
    @Length ( max = 64 )
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
    @JsonProperty
    private boolean isAvailable;
    /**
     * The inventory amount
     */
    private int     inventoryAmount;

    /**
     * Gets the ID of the vaccine
     *
     * @return the id of the vaccine
     */
    @Override
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
