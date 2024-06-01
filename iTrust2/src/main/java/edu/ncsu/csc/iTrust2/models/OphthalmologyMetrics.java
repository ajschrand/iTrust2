package edu.ncsu.csc.iTrust2.models;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Object persisted in the database that represents the Ophthalmology Metrics of
 * a patient's office visit.
 *
 * @author Bruno Volpato
 */

@Entity
public class OphthalmologyMetrics extends DomainObject {

    /**
     * ID of the OphthalmologyMetrics
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /**
     * Visual Acuity (Right eye)
     */
    @Min ( 10 )
    @Max ( 200 )
    @Nonnull
    private Integer visualAcuityRight;

    /**
     * Visual Acuity (Left eye)
     */
    @Min ( 10 )
    @Max ( 200 )
    @Nonnull
    private Integer visualAcuityLeft;

    /**
     * Sphere (Right eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    @Nonnull
    private Float   sphereRight;

    /**
     * Sphere (Left eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    @Nonnull
    private Float   sphereLeft;

    /**
     * Axis (Right eye)
     */
    @Min ( 1 )
    @Max ( 180 )
    @Nonnull
    private Float   axisRight;

    /**
     * Axis (Left eye)
     */
    @Min ( 1 )
    @Max ( 180 )
    @Nonnull
    private Float   axisLeft;

    /**
     * Cylinder (Right eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    @Nonnull
    private Float   cylinderRight;

    /**
     * Cylinder (Left eye)
     */
    @Min ( -100 )
    @Max ( 100 )
    @Nonnull
    private Float   cylinderLeft;

    /**
     * Returns the OpthalmologyMetrics's id
     *
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the OpthalmologyMetrics's id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the OpthalmologyMetrics's visualAcuityRight
     *
     * @return the visualAcuityRight
     */
    public Integer getVisualAcuityRight () {
        return visualAcuityRight;
    }

    /**
     * Sets the OpthalmologyMetrics's visualAcuityRight
     *
     * @param visualAcuityRight
     *            the visualAcuityRight to set
     */
    public void setVisualAcuityRight ( final Integer visualAcuityRight ) {
        this.visualAcuityRight = visualAcuityRight;
    }

    /**
     * Returns the OpthalmologyMetrics's visualAcuityLeft
     *
     * @return the visualAcuityLeft
     */
    public Integer getVisualAcuityLeft () {
        return visualAcuityLeft;
    }

    /**
     * Sets the OpthalmologyMetrics's visualAcuityLeft
     *
     * @param visualAcuityLeft
     *            the visualAcuityLeft to set
     */
    public void setVisualAcuityLeft ( final Integer visualAcuityLeft ) {
        this.visualAcuityLeft = visualAcuityLeft;
    }

    /**
     * Returns the OpthalmologyMetrics's sphereRight
     *
     * @return the sphereRight
     */
    public Float getSphereRight () {
        return sphereRight;
    }

    /**
     * Sets the OpthalmologyMetrics's sphereRight
     *
     * @param sphereRight
     *            the sphereRight to set
     */
    public void setSphereRight ( final Float sphereRight ) {
        this.sphereRight = sphereRight;
    }

    /**
     * Returns the OpthalmologyMetrics's sphereLeft
     *
     * @return the sphereLeft
     */
    public Float getSphereLeft () {
        return sphereLeft;
    }

    /**
     * Sets the OpthalmologyMetrics's sphereLeft
     *
     * @param sphereLeft
     *            the sphereLeft to set
     */
    public void setSphereLeft ( final Float sphereLeft ) {
        this.sphereLeft = sphereLeft;
    }

    /**
     * Returns the OpthalmologyMetrics's axisRight
     *
     * @return the axisRight
     */
    public Float getAxisRight () {
        return axisRight;
    }

    /**
     * Sets the OpthalmologyMetrics's axisRight
     *
     * @param axisRight
     *            the axisRight to set
     */
    public void setAxisRight ( final Float axisRight ) {
        this.axisRight = axisRight;
    }

    /**
     * Returns the OpthalmologyMetrics's axisLeft
     *
     * @return the axisLeft
     */
    public Float getAxisLeft () {
        return axisLeft;
    }

    /**
     * Sets the OpthalmologyMetrics's axisLeft
     *
     * @param axisLeft
     *            the axisLeft to set
     */
    public void setAxisLeft ( final Float axisLeft ) {
        this.axisLeft = axisLeft;
    }

    /**
     * Returns the OpthalmologyMetrics's cylinderRight
     *
     * @return the cylinderRight
     */
    public Float getCylinderRight () {
        return cylinderRight;
    }

    /**
     * Sets the OpthalmologyMetrics's cylinderRight
     *
     * @param cylinderRight
     *            the cylinderRight to set
     */
    public void setCylinderRight ( final Float cylinderRight ) {
        this.cylinderRight = cylinderRight;
    }

    /**
     * Returns the OpthalmologyMetrics's cylinderLeft
     *
     * @return the cylinderLeft
     */
    public Float getCylinderLeft () {
        return cylinderLeft;
    }

    /**
     * Sets the OpthalmologyMetrics's cylinderLeft
     *
     * @param cylinderLeft
     *            the cylinderLeft to set
     */
    public void setCylinderLeft ( final Float cylinderLeft ) {
        this.cylinderLeft = cylinderLeft;
    }

    @Override
    public int hashCode () {
        return Objects.hash( axisLeft, axisRight, cylinderLeft, cylinderRight, id, sphereLeft, sphereRight,
                visualAcuityLeft, visualAcuityRight );
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
        final OphthalmologyMetrics other = (OphthalmologyMetrics) obj;
        return Objects.equals( axisLeft, other.axisLeft ) && Objects.equals( axisRight, other.axisRight )
                && Objects.equals( cylinderLeft, other.cylinderLeft )
                && Objects.equals( cylinderRight, other.cylinderRight ) && Objects.equals( id, other.id )
                && Objects.equals( sphereLeft, other.sphereLeft ) && Objects.equals( sphereRight, other.sphereRight )
                && Objects.equals( visualAcuityLeft, other.visualAcuityLeft )
                && Objects.equals( visualAcuityRight, other.visualAcuityRight );
    }

}
