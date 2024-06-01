package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller class responsible for managing the behavior for the BSM Landing
 * Screen
 *
 * @author nhwiblit
 *
 */
@Controller
public class BSMController {

    /**
     * Returns the Landing screen for the BSM
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "bsm/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_BSM')" )
    public String index ( final Model model ) {
        return Role.ROLE_BSM.getLanding();
    }

    /**
     * Returns the page allowing BSMs to view/edit CPT codes
     *
     * @return The page to display
     */
    @GetMapping ( "/bsm/viewCPTCodes" )
    @PreAuthorize ( "hasAnyRole('ROLE_BSM')" )
    public String viewCPTCodes () {
        return "/bsm/viewCPTCodes";
    }

    /**
     * Returns the page allowing BSMs to manage bills
     *
     * @return The page to display
     */
    @GetMapping ( "/bsm/manageBills" )
    @PreAuthorize ( "hasAnyRole('ROLE_BSM')" )
    public String manageBills () {
        return "/bsm/manageBills";
    }

}
