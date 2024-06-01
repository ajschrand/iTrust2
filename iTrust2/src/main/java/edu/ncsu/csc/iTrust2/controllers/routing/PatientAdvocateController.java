package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller class responsible for managing the behavior for the HCP Landing
 * Screen
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class PatientAdvocateController {

    /**
     * Returns the Landing screen for the Patient Advocate
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "patientadvocate/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public String index ( final Model model ) {
        return Role.ROLE_PATIENTADVOCATE.getLanding();
    }

    /**
     * Returns the page allowing Patient Advocates to edit their
     *
     * @return The page to display
     */
    @GetMapping ( "/patientadvocate/editDemographics" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public String editPatientDemographics () {
        return "/patientadvocate/editDemographics";
    }

    /**
     * Returns the page allowing Patient Advocates to view patient information
     *
     * @return The page to display
     */
    @GetMapping ( "/patientadvocate/viewPatientInformation" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public String viewPatientInformation () {
        return "/patientadvocate/viewPatientInformation";
    }

    /**
     * Returns the page allowing Patient Advocates to view patient office visits
     *
     * @return The page to display
     */
    @GetMapping ( "/patientadvocate/viewInfo/patientOfficeVisit" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public String viewPatientOfficeVisit () {
        return "/patientadvocate/viewInfo/patientOfficeVisit";
    }

    /**
     * Returns the page allowing Patient Advocates to view patient billing info
     *
     * @return The page to display
     */
    @GetMapping ( "/patientadvocate/viewInfo/billing" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public String viewPatientBillingInfo () {
        return "/patientadvocate/viewInfo/billing";
    }

    /**
     * Returns the page allowing Patient Advocates to view patient prescription
     * info
     *
     * @return The page to display
     */
    @GetMapping ( "/patientadvocate/viewInfo/prescriptions" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    public String viewPatientPrescriptions () {
        return "/patientadvocate/viewInfo/prescriptions";
    }

}
