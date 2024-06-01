package edu.ncsu.csc.iTrust2.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.display.SatisfactionSurveyNote;
import edu.ncsu.csc.iTrust2.forms.display.SatisfactionSurveyStatistics;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.SatisfactionSurvey;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.WaitTime;

/**
 * Service class for interacting with SatisfactionSurveyStatistics model,
 * performing CRUD tasks with database and building a persistence object from a
 * Form.
 *
 * @author Kai Presler-Marshall
 * @author bvolpat
 *
 */
@Component
@Transactional
public class SatisfactionSurveyStatisticsService {

    /** SatisfactionSurvey service */
    @Autowired
    private SatisfactionSurveyService surveyService;

    /** Personnel service */
    @Autowired
    private PersonnelService          personnelService;

    /**
     * Finds all SatisfactionSurveyStatistics for all HCPs
     *
     * @return Matching Statistics
     */
    public List<SatisfactionSurveyStatistics> findAll () {
        final List<SatisfactionSurveyStatistics> allStatistics = new ArrayList<>();

        final List<Personnel> personnel = personnelService.findAll();

        for ( final Personnel p : personnel ) {
            if ( !p.getRoles().contains( Role.ROLE_HCP ) ) {
                continue;
            }
            allStatistics.add( findForHcp( p ) );
        }

        return allStatistics;
    }

    /**
     * Finds SatisfactionSurveyStatistics for a single HCP
     *
     * @param hcp
     *            HCP to search for
     * @return Statistics for that HCP
     */
    public SatisfactionSurveyStatistics findForHcp ( final User hcp ) {
        final SatisfactionSurveyStatistics statistics = new SatisfactionSurveyStatistics();
        statistics.setHcp( hcp );

        final List<SatisfactionSurvey> surveys = surveyService.findByHcp( hcp.getUsername() );
        statistics.setNumberOfSurveys( surveys.size() );
        statistics.setAverageVisitSatisfaction(
                (int) surveys.stream().mapToInt( SatisfactionSurvey::getVisitSatisfaction ).average().orElse( 0 ) );
        statistics.setAverageTreatmentSatisfaction(
                (int) surveys.stream().mapToInt( SatisfactionSurvey::getTreatmentSatisfaction ).average().orElse( 0 ) );

        final int averageWaitingRoomTime = (int) surveys.stream().map( SatisfactionSurvey::getWaitingRoomTime )
                .mapToInt( WaitTime::getCode ).average().orElse( 0 );
        statistics.setAverageWaitingRoomTime( WaitTime.getByCode( averageWaitingRoomTime ) );

        final int averageExaminationResponseTime = (int) surveys.stream()
                .map( SatisfactionSurvey::getExaminationResponseTime ).mapToInt( WaitTime::getCode ).average()
                .orElse( 0 );
        statistics.setAverageExaminationResponseTime( WaitTime.getByCode( averageExaminationResponseTime ) );

        final List<SatisfactionSurveyNote> notes = new ArrayList<>();

        for ( final SatisfactionSurvey satisfactionSurvey : surveys ) {
            final SatisfactionSurveyNote note = new SatisfactionSurveyNote();
            note.setComments( satisfactionSurvey.getComments() );
            note.setAverageSatisfaction(
                    ( satisfactionSurvey.getVisitSatisfaction() + satisfactionSurvey.getTreatmentSatisfaction() ) / 2 );
            notes.add( note );
        }

        statistics.setNotes( notes );
        return statistics;
    }

}
