package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Ophthalmology Visit form used to document an Ophthalmology Office Visit by the
 * HCP. This will be validated and converted to a OphthalmologyVisit to be stored
 * in the database.
 *
 * @author Bruno Volpato
 *
 */
public class OphthalmologyVisitForm implements Serializable {
    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor so that we can create an Office Visit form for the user
     * to fill out
     */
    public OphthalmologyVisitForm () {
    }

    /**
     * Visual Acuity (Right eye)
     */
    @Min ( 10 )
    @Max ( 200 )
    private Integer visualAcuityRight;

    /**
     * Visual Acuity (Left eye)
     */
    @Min ( 10 )
    @Max ( 200 )
    private Integer visualAcuityLeft;

    /**
     * Sphere (Right eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    private Float   sphereRight;

    /**
     * Sphere (Left eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    private Float   sphereLeft;

    /**
     * Axis (Right eye)
     */
    @Min ( 1 )
    @Max ( 180 )
    private Float   axisRight;

    /**
     * Axis (Left eye)
     */
    @Min ( 1 )
    @Max ( 180 )
    private Float   axisLeft;

    /**
     * Cylinder (Right eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    private Float   cylinderRight;

    /**
     * Cylinder (Left eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    private Float   cylinderLeft;

    /**
     * Returns the OphthalmologyVisitForm's visualAcuityRight
     *
     * @return the visualAcuityRight
     */
    public Integer getVisualAcuityRight () {
        return visualAcuityRight;
    }

    /**
     * Sets the OphthalmologyVisitForm's visualAcuityRight
     *
     * @param visualAcuityRight
     *            the visualAcuityRight to set
     */
    public void setVisualAcuityRight ( Integer visualAcuityRight ) {
        this.visualAcuityRight = visualAcuityRight;
    }

    /**
     * Returns the OphthalmologyVisitForm's visualAcuityLeft
     *
     * @return the visualAcuityLeft
     */
    public Integer getVisualAcuityLeft () {
        return visualAcuityLeft;
    }

    /**
     * Sets the OphthalmologyVisitForm's visualAcuityLeft
     *
     * @param visualAcuityLeft
     *            the visualAcuityLeft to set
     */
    public void setVisualAcuityLeft ( Integer visualAcuityLeft ) {
        this.visualAcuityLeft = visualAcuityLeft;
    }

    /**
     * Returns the OphthalmologyVisitForm's sphereRight
     *
     * @return the sphereRight
     */
    public Float getSphereRight () {
        return sphereRight;
    }

    /**
     * Sets the OphthalmologyVisitForm's sphereRight
     *
     * @param sphereRight
     *            the sphereRight to set
     */
    public void setSphereRight ( Float sphereRight ) {
        this.sphereRight = sphereRight;
    }

    /**
     * Returns the OphthalmologyVisitForm's sphereLeft
     *
     * @return the sphereLeft
     */
    public Float getSphereLeft () {
        return sphereLeft;
    }

    /**
     * Sets the OphthalmologyVisitForm's sphereLeft
     *
     * @param sphereLeft
     *            the sphereLeft to set
     */
    public void setSphereLeft ( Float sphereLeft ) {
        this.sphereLeft = sphereLeft;
    }

    /**
     * Returns the OphthalmologyVisitForm's axisRight
     *
     * @return the axisRight
     */
    public Float getAxisRight () {
        return axisRight;
    }

    /**
     * Sets the OphthalmologyVisitForm's axisRight
     *
     * @param axisRight
     *            the axisRight to set
     */
    public void setAxisRight ( Float axisRight ) {
        this.axisRight = axisRight;
    }

    /**
     * Returns the OphthalmologyVisitForm's axisLeft
     *
     * @return the axisLeft
     */
    public Float getAxisLeft () {
        return axisLeft;
    }

    /**
     * Sets the OphthalmologyVisitForm's axisLeft
     *
     * @param axisLeft
     *            the axisLeft to set
     */
    public void setAxisLeft ( Float axisLeft ) {
        this.axisLeft = axisLeft;
    }

    /**
     * Returns the OphthalmologyVisitForm's cylinderRight
     *
     * @return the cylinderRight
     */
    public Float getCylinderRight () {
        return cylinderRight;
    }

    /**
     * Sets the OphthalmologyVisitForm's cylinderRight
     *
     * @param cylinderRight
     *            the cylinderRight to set
     */
    public void setCylinderRight ( Float cylinderRight ) {
        this.cylinderRight = cylinderRight;
    }

    /**
     * Returns the OphthalmologyVisitForm's cylinderLeft
     *
     * @return the cylinderLeft
     */
    public Float getCylinderLeft () {
        return cylinderLeft;
    }

    /**
     * Sets the OphthalmologyVisitForm's cylinderLeft
     *
     * @param cylinderLeft
     *            the cylinderLeft to set
     */
    public void setCylinderLeft ( Float cylinderLeft ) {
        this.cylinderLeft = cylinderLeft;
    }

}
