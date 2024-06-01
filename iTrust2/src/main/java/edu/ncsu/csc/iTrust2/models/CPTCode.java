package edu.ncsu.csc.iTrust2.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Class for CPT codes. These codes themselves are stored as an int, along with
 * a description, an ID, a cost, a status and a version.
 *
 * @author yli246
 *
 */
@Entity
public class CPTCode extends DomainObject {

    /**
     * ID of this CPTCode
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /**
     * number of the code
     */
    private int     code;

    /**
     * Description of the code
     */
    private String  description;

    /**
     * cost of the code
     */
    private int     cost;

    /**
     * version of the code
     */
    private int     version;

    /**
     * if code is isActive
     */
    private Boolean isActive;

    /**
     * Empty constructor for Hibernate
     */
    public CPTCode () {

    }

    /**
     * Sets the ID of the Code
     *
     * @param id
     *            The new ID of the Code. For Hibernate.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the code
     *
     * @return The code
     */
    public int getCode () {
        return code;
    }

    /**
     * Sets the code
     *
     * @param code
     *            The new code
     */
    public void setCode ( final int code ) {
        this.code = code;
    }

    /**
     * Returns the description of the code
     *
     * @return The description
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Sets the description of this code
     *
     * @param d
     *            The new description
     */
    public void setDescription ( final String d ) {
        description = d;
    }

    /**
     * Returns the CPTCode's cost
     *
     * @return the cost
     */
    public int getCost () {
        return cost;
    }

    /**
     * Sets the CPTCode's cost
     *
     * @param cost
     *            the cost to set
     */
    public void setCost ( final int cost ) {
        this.cost = cost;
    }

    /**
     * Returns the CPTCode's version
     *
     * @return the version
     */
    public int getVersion () {
        return version;
    }

    /**
     * Sets the CPTCode's version
     *
     * @param version
     *            the version to set
     */
    public void setVersion ( final int version ) {
        this.version = version;
    }

    /**
     * Gets if the code is active
     *
     * @return Whether the code is active or not
     */
    public Boolean getisActive () {
        return isActive;
    }

    /**
     * Sets the CPTCode's active value
     *
     * @param isActive
     *            Whether the code is active
     */
    public void setisActive ( final Boolean isActive ) {
        this.isActive = isActive;
    }

    /**
     * Returns the CPTCode's id
     *
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    @Override
    public int hashCode () {
        return Objects.hash( code, description, id, cost, version, isActive );
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
        final CPTCode other = (CPTCode) obj;
        return Objects.equals( code, other.code ) && Objects.equals( description, other.description )
                && Objects.equals( id, other.id ) && Objects.equals( cost, other.cost )
                && Objects.equals( version, other.version ) && Objects.equals( isActive, other.isActive );
    }

}
