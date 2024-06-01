package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.SatisfactionSurveyForm;
import edu.ncsu.csc.iTrust2.forms.display.SatisfactionSurveyStatistics;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.SatisfactionSurvey;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.SatisfactionSurveyService;
import edu.ncsu.csc.iTrust2.services.SatisfactionSurveyStatisticsService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Controller for Satisfaction Surveys operations
 *
 * @author abhirud
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APISatisfactionSurveyController extends APIController {

    /** SatisfactionSurvey service */
    @Autowired
    private SatisfactionSurveyService           satisfactionSurveyService;

    /** OfficeVisit service */
    @Autowired
    private OfficeVisitService                  officeVisitService;

    /** SatisfactionSurveyStats service */
    @Autowired
    private SatisfactionSurveyStatisticsService satisfactionSurveyStatisticsService;

    /** User Service */
    @Autowired
    private UserService                         userService;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil                          loggerUtil;

    /**
     * Retrieves a list of all the satisfaction surveys
     *
     * @return a list of all the satisfaction surveys
     */
    @GetMapping ( BASE_PATH + "/surveys/" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public List<SatisfactionSurvey> getSatisfactionSurveys () {
        return satisfactionSurveyService.findAll();
    }

    /**
     * Retrieves a list of all the satisfaction surveys for a specific HCP
     *
     * @param hcp
     *            the hcp whose surveys are to be returned
     * @return a list of all the satisfaction surveys
     */
    @GetMapping ( BASE_PATH + "/surveys/hcp/{hcp}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public List<SatisfactionSurvey> getSatisfactionSurveys ( @PathVariable ( "hcp" ) final String hcp ) {
        return satisfactionSurveyService.findByHcp( hcp );
    }

    /**
     * Creates and saves a new Satisfaction Survey from the RequestBody
     * provided.
     *
     * @param surveyForm
     *            The satisfaction survey to be validated and saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/surveys" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity createSatisfactionSurvey ( @RequestBody final SatisfactionSurveyForm surveyForm ) {
        try {

            final SatisfactionSurvey survey = satisfactionSurveyService.build( surveyForm );

            if ( null != survey.getId() && satisfactionSurveyService.existsById( survey.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Satisfaction Survey with the id " + survey.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            satisfactionSurveyService.save( survey );

            final OfficeVisit officeVisit = officeVisitService.findById( surveyForm.getOfficeVisitId() );
            officeVisit.setSatisfactionSurvey( survey );
            officeVisitService.save( officeVisit );

            loggerUtil.log( TransactionType.PATIENT_COMPLETED_SATISFACION_SURVEY, LoggerUtil.currentUser(),
                    survey.getHcp().getUsername() );
            return new ResponseEntity( survey, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse(
                            "Could not validate or save the SatisfactionSurvey provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * This method returns the average stats for an hcp
     *
     * @param hcpName
     *            the hcp username whose stats are to be returned
     * @return The stats for the hcp
     */
    @GetMapping ( BASE_PATH + "/surveys/statistics/{hcp}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity getSatisfactionSurveyStatistics ( @PathVariable ( "hcp" ) final String hcpName ) {

        final User hcp = userService.findByName( hcpName );

        final SatisfactionSurveyStatistics stats = satisfactionSurveyStatisticsService.findForHcp( hcp );

        loggerUtil.log( TransactionType.ADMIN_VIEW_STATS, LoggerUtil.currentUser(), hcp.getUsername(),
                "Check provider statistics" );

        return new ResponseEntity( stats, HttpStatus.OK );
    }

    /**
     * This method returns the overall average stats for hcps
     *
     * @return the overall average stats for hcps
     */
    @GetMapping ( BASE_PATH + "/surveys/statistics" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity getAverageSatisfactionSurveyStatistics () {
        final List<SatisfactionSurveyStatistics> statistics = satisfactionSurveyStatisticsService.findAll();
        loggerUtil.log( TransactionType.ADMIN_VIEW_STATS, LoggerUtil.currentUser() );
        return new ResponseEntity( statistics, HttpStatus.OK );
    }

}
