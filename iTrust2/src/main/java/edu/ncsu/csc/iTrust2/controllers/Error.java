package edu.ncsu.csc.iTrust2.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller handles the new custom WhiteLabelError Page
 *
 * @author Lauren Murillo
 *
 */
@Controller
public class Error implements ErrorController {
    /**
     * Maps to the custom error page
     *
     * @return the error page
     */
    @RequestMapping ( "/error" )
    public String handleError () {
        return "error";
    }

    /**
     * Gets the path of the error page
     *
     * @return the error page
     */
    @Override
    public String getErrorPath () {
        return "error/";
    }

}
