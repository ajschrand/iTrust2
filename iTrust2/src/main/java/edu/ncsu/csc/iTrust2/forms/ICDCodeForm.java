package edu.ncsu.csc.iTrust2.forms;

import edu.ncsu.csc.iTrust2.models.ICDCode;

/**
 * Intermediate form for adding or editing ICDCodes. Used to create and
 * serialize ICDCodes.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
public class ICDCodeForm {

    /** The code of the Diagnosis */
    private String  code;
    /** The description of the diagnosis */
    private String  description;
    /** ID of the ICDCode */
    private Long    id;
    /**
     * If ICD code is ophtalmology-related
     */
    private Boolean isOphthalmology;

    /**
     * Empty constructor for GSON
     */
    public ICDCodeForm () {

    }

    /**
     * Construct a form off an existing ICDCode object
     *
     * @param code
     *            The object to fill this form with
     */
    public ICDCodeForm ( final ICDCode code ) {
        setCode( code.getCode() );
        setDescription( code.getDescription() );
        setId( code.getId() );
        setIsOphthalmology( code.getIsOphthalmology() );
    }

    /**
     * Sets the String representation of the code
     *
     * @param code
     *            The new code
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Returns the String representation of the code
     *
     * @return The code
     */
    public String getCode () {
        return code;
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
     * Returns the description of the code
     *
     * @return The description
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the ID of the Code
     *
     * @param l
     *            The new ID of the Code. For Hibernate.
     */
    public void setId ( final Long l ) {
        id = l;
    }

    /**
     * Returns the ID of the Diagnosis
     *
     * @return The diagnosis ID
     */
    public Long getId () {
        return id;
    }

    /**
     * Returns the ICDCodeForm's isOphthalmology
     *
     * @return the isOphthalmology
     */
    public Boolean getIsOphthalmology () {
        return isOphthalmology;
    }

    /**
     * Sets the ICDCodeForm's isOphthalmology
     *
     * @param isOphthalmology
     *            the isOphthalmology to set
     */
    public void setIsOphthalmology ( final Boolean isOphthalmology ) {
        this.isOphthalmology = isOphthalmology;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( code == null ) ? 0 : code.hashCode() );
        result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        result = prime * result + ( ( isOphthalmology == null ) ? 0 : isOphthalmology.hashCode() );
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
        final ICDCodeForm other = (ICDCodeForm) obj;
        if ( code == null ) {
            if ( other.code != null ) {
                return false;
            }
        }
        else if ( !code.equals( other.code ) ) {
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
        if ( isOphthalmology == null ) {
            if ( other.isOphthalmology != null ) {
                return false;
            }
        }
        else if ( !isOphthalmology.equals( other.isOphthalmology ) ) {
            return false;
        }
        return true;
    }

}
