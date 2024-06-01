package edu.ncsu.csc.iTrust2.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.DrugForm;
import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.DrugService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PrescriptionService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class PrescriptionTest {

    @Autowired
    private DrugService             drugService;

    @Autowired
    private PrescriptionService     service;

    @Autowired
    private PatientService<Patient> patientService;

    @Test
    public void testBuildFromForm () {

        if ( !drugService.existsByCode( "1234-5678-90" ) ) {
            final DrugForm drugForm = new DrugForm();
            drugForm.setCode( "1234-5678-90" );
            drugForm.setDescription( "Tylenol - Acetominophen" );
            drugForm.setName( "Tylenol" );
            drugService.save( new Drug( drugForm ) );
        }
        if ( !patientService.existsByName( "headachepatient" ) ) {
            patientService.save( new Patient( new UserForm( "headachepatient", "patient", Role.ROLE_PATIENT, 1 ) ) );
        }

        final PrescriptionForm pf = new PrescriptionForm();
        pf.setDrug( "1234-5678-90" );
        pf.setDosage( 50 );
        pf.setPatient( "headachepatient" );
        pf.setStartDate( "2021-01-01" );
        pf.setEndDate( "2022-02-02" );

        final Prescription prescription = service.build( pf );
        Assertions.assertNotNull( prescription );
        Assertions.assertNotNull( prescription.getDrug() );
        Assertions.assertEquals( "Tylenol", prescription.getDrug().getName() );
    }
}
