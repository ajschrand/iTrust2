package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.SatisfactionSurveyForm;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.SatisfactionSurvey;
import edu.ncsu.csc.iTrust2.repositories.OfficeVisitRepository;
import edu.ncsu.csc.iTrust2.repositories.SatisfactionSurveyRepository;

/**
 * Service class for interacting with SatisfactionSurvey model, performing CRUD
 * tasks with database and building a persistence object from a Form.
 *
 * @author Kai Presler-Marshall
 * @author abhirud
 *
 */
@Component
@Transactional
public class SatisfactionSurveyService extends Service<SatisfactionSurvey, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private SatisfactionSurveyRepository repository;

    /** OfficeVisit repository */
    @Autowired
    private OfficeVisitRepository        officeVisitRepository;

    @Override
    protected JpaRepository<SatisfactionSurvey, Long> getRepository () {
        return repository;
    }

    /**
     * Gets all satisfactionsurveys for a given HCP
     *
     * @param hcp
     *            HCP username to search by
     * @return Matching surveys
     */
    public List<SatisfactionSurvey> findByHcp ( final String hcp ) {
        return repository.findByHcpUsername( hcp );
    }

    /**
     * Gets all satisfactionsurveys for a given patient
     *
     * @param patient
     *            Patient username to search by
     * @return Matching surveys
     */
    public List<SatisfactionSurvey> findByPatient ( final String patient ) {
        return repository.findByPatientUsername( patient );
    }

    /**
     * Gets all satisfactionsurveys for a given HCP and patient
     *
     * @param hcp
     *            HCP username
     * @param patient
     *            Patient username
     * @return Matching surveys
     */
    public List<SatisfactionSurvey> findByHcpAndPatient ( final String hcp, final String patient ) {
        return repository.findByHcpUsernameAndPatientUsername( hcp, patient );
    }

    /**
     * Builds a SatisfactionSurvey from a deserialised SatisfactionSurveyForm
     *
     * @param ssf
     *            Form to build from
     * @return Built SatisfactionSurvey
     */
    public SatisfactionSurvey build ( final SatisfactionSurveyForm ssf ) {
        final OfficeVisit officeVisit = officeVisitRepository.findById( ssf.getOfficeVisitId() ).orElseThrow();

        final SatisfactionSurvey survey = new SatisfactionSurvey();
        survey.setComments( ssf.getComments() );
        survey.setExaminationResponseTime( ssf.getExaminationResponseTime() );
        survey.setHcp( officeVisit.getHcp() );
        survey.setPatient( officeVisit.getPatient() );
        survey.setTreatmentSatisfaction( ssf.getTreatmentSatisfaction() );
        survey.setVisitSatisfaction( ssf.getVisitSatisfaction() );
        survey.setWaitingRoomTime( ssf.getWaitingRoomTime() );
        return survey;
    }

}
