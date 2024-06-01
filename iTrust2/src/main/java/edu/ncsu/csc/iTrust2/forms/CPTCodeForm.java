package edu.ncsu.csc.iTrust2.forms;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Intermediate form for adding or editing CPTCodes. Used to create and
 * serialize CPTCodes.
 *
 * @author yli246
 *
 */
public class CPTCodeForm {

    /**
     * ID of this CPTCode
     */
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
     * Empty constructor for GSON
     */
    public CPTCodeForm () {

    }

    /**
     * Construct a form off an existing CPTCode object
     *
     * @param code
     *            The object to fill this form with
     */
    public CPTCodeForm ( final CPTCode code ) {
        setCode( code.getCode() );
        setDescription( code.getDescription() );
        setId( code.getId() );
        setCost( code.getCost() );
        setVersion( code.getVersion() );
        setisActive( code.getisActive() );
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
     * Returns whether the code is active or not
     *
     * @return active or not
     */
    public Boolean getisActive () {
        return isActive;
    }

    /**
     * Sets whether the code is active
     *
     * @param isActive
     *            whether the code is active
     */
    public void setisActive ( final Boolean isActive ) {
        this.isActive = isActive;
    }

    /**
     * Returns the CPTCode's id
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( code == 0 ) ? 0 : Integer.hashCode( code ) );
        result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        result = prime * result + ( ( cost == 0 ) ? 0 : Integer.hashCode( cost ) );
        result = prime * result + ( ( version == 0 ) ? 0 : Integer.hashCode( version ) );
        result = prime * result + ( ( isActive == null ) ? 0 : isActive.hashCode() );
        return result;
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
        final CPTCodeForm other = (CPTCodeForm) obj;
        if ( code == 0 ) {
            if ( other.code != 0 ) {
                return false;
            }
        }
        else if ( code != other.code ) {
            return false;
        }
        if ( description == null ) {
            if ( other.description != null ) {
                return false;
            }
        }
        else if ( !description.equals( other.description ) ) {
            return false;
        }
        if ( id == null ) {
            if ( other.id != null ) {
                return false;
            }
        }
        else if ( !id.equals( other.id ) ) {
            return false;
        }
        if ( cost == 0 ) {
            if ( other.cost != 0 ) {
                return false;
            }
        }
        else if ( cost != other.cost ) {
            return false;
        }
        if ( version == 0 ) {
            if ( other.version != 0 ) {
                return false;
            }
        }
        else if ( version != other.version ) {
            return false;
        }
        if ( isActive == null ) {
            if ( other.isActive != null ) {
                return false;
            }
        }
        else if ( !isActive.equals( other.isActive ) ) {
            return false;
        }
        return true;
    }

}
