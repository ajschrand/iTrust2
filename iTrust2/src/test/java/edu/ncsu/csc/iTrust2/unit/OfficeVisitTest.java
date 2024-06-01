package edu.ncsu.csc.iTrust2.unit;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.OphthalmologyVisitForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.OphthalmologyMetrics;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.BasicHealthMetricsService;
import edu.ncsu.csc.iTrust2.services.DrugService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.ICDCodeService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.PrescriptionService;
import edu.ncsu.csc.iTrust2.services.UserService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class OfficeVisitTest {

    @Autowired
    private OfficeVisitService        officeVisitService;

    @Autowired
    private BasicHealthMetricsService basicHealthMetricsService;

    @Autowired
    private HospitalService           hospitalService;

    @Autowired
    private UserService<User>         userService;

    @Autowired
    private ICDCodeService            icdCodeService;

    @Autowired
    private DrugService               drugService;

    @Autowired
    private PrescriptionService       prescriptionService;

    @BeforeEach
    public void setup () {
        officeVisitService.deleteAll();

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );

        userService.saveAll( List.of( hcp, alice ) );
    }

    @Test
    @Transactional
    public void testOfficeVisit () {
        Assertions.assertEquals( 0, officeVisitService.count() );

        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hospitalService.save( hosp );

        final OfficeVisit visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( userService.findByName( "hcp" ) );
        bhm.setPatient( userService.findByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        basicHealthMetricsService.save( bhm );

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( userService.findByName( "AliceThirteen" ) );
        visit.setHcp( userService.findByName( "AliceThirteen" ) );
        visit.setDate( ZonedDateTime.now() );
        officeVisitService.save( visit );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        icdCodeService.save( code );

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        officeVisitService.save( visit );

        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drugService.save( drug );

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        final LocalDate now = LocalDate.now();
        pres.setEndDate( now.plus( Period.ofWeeks( 5 ) ) );
        pres.setPatient( userService.findByName( "AliceThirteen" ) );
        pres.setStartDate( now );
        pres.setRenewals( 5 );

        prescriptionService.save( pres );

        final List<Prescription> pr = new ArrayList<Prescription>();
        pr.add( pres );
        visit.setPrescriptions( pr );

        officeVisitService.save( visit );

        Assertions.assertEquals( 1, officeVisitService.count() );

        OfficeVisit retrieved = officeVisitService.findAll().get( 0 );

        Assertions.assertEquals( "Li2O8", retrieved.getPrescriptions().get( 0 ).getDrug().getName() );

        visit.setPrescriptions( Collections.emptyList() );

        officeVisitService.save( visit );

        retrieved = officeVisitService.findAll().get( 0 );

        Assertions.assertEquals( 0, retrieved.getPrescriptions().size() );

        final Drug drug2 = new Drug();
        drug2.setCode( "1235-1234-12" );
        drug2.setDescription( "Selenium Compounds" );
        drug2.setName( "Se2O2" );
        drugService.save( drug2 );

        /* Make sure we can add multiple prescriptions */
        final Prescription pres2 = new Prescription();
        pres2.setDosage( 12 );
        pres2.setDrug( drug );
        pres2.setEndDate( now.plus( Period.ofWeeks( 15 ) ) );
        pres2.setPatient( userService.findByName( "AliceThirteen" ) );
        pres2.setStartDate( now );
        pres2.setRenewals( 2 );
        pr.add( pres2 );

        visit.setPrescriptions( pr );

        /* And that cascade actions work too */

        officeVisitService.save( visit );

        retrieved = officeVisitService.findAll().get( 0 );

        Assertions.assertEquals( 2, retrieved.getPrescriptions().size() );

    }

    @Test
    @Transactional
    public void testOfficeVisitOphthalmology () {
        Assertions.assertEquals( 0, officeVisitService.count() );

        final Hospital hosp = new Hospital( "Dr. Jenkins' Mad Eyes", "123 Main St", "12345", "NC" );
        hospitalService.save( hosp );

        final OfficeVisit visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();
        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( userService.findByName( "hcp" ) );
        bhm.setPatient( userService.findByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        basicHealthMetricsService.save( bhm );

        final OphthalmologyMetrics om = new OphthalmologyMetrics();
        om.setAxisLeft( 1.0f );
        om.setAxisRight( 1.0f );
        om.setCylinderLeft( 1.0f );
        om.setCylinderRight( 1.0f );
        om.setSphereLeft( 0.0f );
        om.setSphereRight( 0.0f );
        om.setVisualAcuityLeft( 20 );
        om.setVisualAcuityRight( 20 );

        visit.setBasicHealthMetrics( bhm );
        visit.setOphthalmologyMetrics( om );

        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY );
        visit.setHospital( hosp );
        visit.setPatient( userService.findByName( "AliceThirteen" ) );
        visit.setHcp( userService.findByName( "AliceThirteen" ) );
        visit.setDate( ZonedDateTime.now() );
        officeVisitService.save( visit );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Cataracts" );
        code.setIsOphthalmology( true );
        icdCodeService.save( code );

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        officeVisitService.save( visit );

        Assertions.assertEquals( 1, officeVisitService.count() );

        final OfficeVisit retrieved = officeVisitService.findAll().get( 0 );
        Assertions.assertNotNull( retrieved );

        Assertions.assertEquals( 1, retrieved.getDiagnoses().size() );

        Assertions.assertNotNull( retrieved.getOphthalmologyMetrics() );

        Assertions.assertEquals( 20, retrieved.getOphthalmologyMetrics().getVisualAcuityLeft().intValue() );

    }

    @Test
    @Transactional
    public void testOfficeVisitOphthalmologyForm () {

        final Hospital hosp = new Hospital( "Dr. Jenkins' Mad Eyes", "123 Main St", "12345", "NC" );
        hospitalService.save( hosp );

        final OfficeVisit visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();
        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( userService.findByName( "hcp" ) );
        bhm.setPatient( userService.findByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        basicHealthMetricsService.save( bhm );

        final OphthalmologyVisitForm ophF = new OphthalmologyVisitForm();
        ophF.setAxisLeft( 1.0f );
        ophF.setAxisRight( 1.0f );
        ophF.setCylinderLeft( 1.0f );
        ophF.setCylinderRight( 1.0f );
        ophF.setSphereLeft( 0.0f );
        ophF.setSphereRight( 0.0f );
        ophF.setVisualAcuityLeft( 20 );
        ophF.setVisualAcuityRight( 20 );

        final OphthalmologyMetrics om = new OphthalmologyMetrics();
        om.setAxisLeft( ophF.getAxisLeft() );
        om.setAxisRight( ophF.getAxisRight() );
        om.setCylinderLeft( ophF.getCylinderLeft() );
        om.setCylinderRight( ophF.getCylinderRight() );
        om.setSphereLeft( ophF.getSphereLeft() );
        om.setSphereRight( ophF.getSphereRight() );
        om.setVisualAcuityLeft( ophF.getVisualAcuityLeft() );
        om.setVisualAcuityRight( ophF.getVisualAcuityRight() );

        visit.setBasicHealthMetrics( bhm );
        visit.setOphthalmologyMetrics( om );

        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY );
        visit.setHospital( hosp );
        visit.setPatient( userService.findByName( "AliceThirteen" ) );
        visit.setHcp( userService.findByName( "AliceThirteen" ) );
        visit.setDate( ZonedDateTime.now() );
        officeVisitService.save( visit );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Cataracts" );
        code.setIsOphthalmology( true );
        icdCodeService.save( code );

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        officeVisitService.save( visit );

        Assertions.assertEquals( 1, officeVisitService.count() );

        final OfficeVisit retrieved = officeVisitService.findAll().get( 0 );
        Assertions.assertNotNull( retrieved );

        Assertions.assertEquals( 1, retrieved.getDiagnoses().size() );

        Assertions.assertNotNull( retrieved.getOphthalmologyMetrics() );

        Assertions.assertEquals( 20, retrieved.getOphthalmologyMetrics().getVisualAcuityLeft().intValue() );

    }
}
