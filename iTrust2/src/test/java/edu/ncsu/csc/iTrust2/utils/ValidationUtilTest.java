package edu.ncsu.csc.iTrust2.utils;

import org.junit.Test;

import edu.ncsu.csc.iTrust2.forms.SatisfactionSurveyForm;
import edu.ncsu.csc.iTrust2.models.enums.WaitTime;

import static org.junit.Assert.fail;

/**
 * Class with validation for utility methods that are used in forms and entity
 * validation
 *
 * @author bvolpat
 *
 */
public class ValidationUtilTest {

    @Test
    public void testSatisfactionSurvey () {

        final SatisfactionSurveyForm surveyForm = new SatisfactionSurveyForm();
        surveyForm.setTreatmentSatisfaction( 2 );
        surveyForm.setVisitSatisfaction( -1 );
        surveyForm.setComments( "Good doctor" );
        surveyForm.setWaitingRoomTime( WaitTime.FIVE_TO_TEN );
        surveyForm.setExaminationResponseTime( WaitTime.FIFTEEN_TO_TWENTY );

        try {
            ValidationUtil.validate( surveyForm );
            fail( "Should have failed because of visit satisfaction -1" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }
    }
}
