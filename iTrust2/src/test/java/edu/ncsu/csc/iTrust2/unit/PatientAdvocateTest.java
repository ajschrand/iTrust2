package edu.ncsu.csc.iTrust2.unit;

import java.util.HashSet;
import java.util.Set;

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
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateAssociationForm;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateForm;
import edu.ncsu.csc.iTrust2.forms.PatientAdvocateUserForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Tests the patient model
 *
 * @author Noah Alexander
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class PatientAdvocateTest {
    /** Patient service object */
    @Autowired
    private PatientService<Patient>           service;

    /** Personnel service object*/
    @Autowired
    private PersonnelService                  perService;

    /** Patient Advocate Association service object */
    @Autowired
    private PatientAdvocateAssociationService paService;

    /**
    * Patient 1 user for testing
    */
    private static final String               PATIENT_USER  = "patientUser";

    /**
    * Patient 2 user for testing
    */
    private static final String               PATIENT2_USER = "patient2User";

    /**
    * Patient advocate user for testing
    */
    private static final String               ADVOCATE_USER = "advocateUser";

    /**
    * Password for testing
    */
    private static final String               PW            = "123456";

    /**
     * Resets all the services before each test to create a clean slate.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        perService.deleteAll();
        paService.deleteAll();
    }

    /**
     * Test valid patient advocate creations
     */
    @Test
    @Transactional
    public void testCreateValidPatientAdvocate () {

        Assertions.assertEquals( perService.findAll().size(), 0 );

        final PatientAdvocate p1 = new PatientAdvocate(
                new UserForm( ADVOCATE_USER, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        
        
        p1.setMiddleName( "Edgard" );
        p1.setNickName( "Eddy" );
        p1.setFirstName( "Billy" );
        perService.save( p1 );

        Assertions.assertEquals( perService.findAll().size(), 1 );

        final PatientAdvocate p2 = (PatientAdvocate) perService.findById( ADVOCATE_USER );
        Assertions.assertEquals( p2.getNickName(), "Eddy" );
        Assertions.assertEquals( p2.getMiddleName(), "Edgard" );
        Assertions.assertEquals( p2.getFirstName(), "Billy" );

    }

    /**
     * Test invalid patient advocate creations
     */
    @Test
    @Transactional
    public void testCreateInvalidPatientAdvocate () {

        Assertions.assertEquals( perService.findAll().size(), 0 );

        final UserForm form = new UserForm( ADVOCATE_USER, PW, Role.ROLE_PATIENTADVOCATE, 1 );
        form.addRole( Role.ROLE_ER.toString() );

        try {
            final PatientAdvocate p1 = new PatientAdvocate( form );
            Assertions.fail();

        }
        catch ( final Exception e ) {
            // This should happen
        }

    }

    /**
     * Test update patient advocate
     */
    @Test
    @Transactional
    public void testUpdatePatientAdvocate () {

        Assertions.assertEquals( perService.findAll().size(), 0 );

        final PatientAdvocate p1 = new PatientAdvocate(
                new UserForm( ADVOCATE_USER, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        p1.setMiddleName( "Edgard" );
        p1.setNickName( "Eddy" );
        p1.setFirstName( "Billy" );
        perService.save( p1 );

        Assertions.assertEquals( perService.findAll().size(), 1 );

        PatientAdvocate p2 = (PatientAdvocate) perService.findById( ADVOCATE_USER );
        Assertions.assertEquals( p2.getNickName(), "Eddy" );
        Assertions.assertEquals( p2.getMiddleName(), "Edgard" );
        Assertions.assertEquals( p2.getFirstName(), "Billy" );

        final PatientAdvocateForm form = new PatientAdvocateForm();
        form.setFirstName( "Billy" );
        form.setMiddleName( "Edgard" );
        form.setNickname( "Eddy" );
        form.setLastName( "Bob" );
        form.setAddress1( "address1" );
        form.setAddress2( "address2" );
        form.setState( State.NC.toString() );
        form.setCity( "Raleigh" );
        form.setZip( "11111" );
        form.setEmail( "test@email.com" );
        form.setPhone( "111-222-3333" );

        p2.update( form );

        Assertions.assertEquals( "Billy", p2.getFirstName() );
        Assertions.assertEquals( "Edgard", p2.getMiddleName() );
        Assertions.assertEquals( "Eddy", p2.getNickName() );
        Assertions.assertEquals( "Bob", p2.getLastName());
        Assertions.assertEquals( "address1",  p2.getAddress1() );
        Assertions.assertEquals( "address2", p2.getAddress2() );
        Assertions.assertEquals( State.NC.toString(), p2.getState().toString() );
        Assertions.assertEquals( "Raleigh", p2.getCity() );
        Assertions.assertEquals( "11111", p2.getZip() );
        Assertions.assertEquals( "test@email.com", p2.getEmail() );
        Assertions.assertEquals( "111-222-3333", p2.getPhone() );

        perService.save( p2 );

        Assertions.assertEquals( perService.findAll().size(), 1 );

        p2 = (PatientAdvocate) perService.findById( ADVOCATE_USER );

        Assertions.assertEquals( "Billy", p2.getFirstName() );
        Assertions.assertEquals( "Edgard", p2.getMiddleName() );
        Assertions.assertEquals( "Eddy", p2.getNickName() );
        Assertions.assertEquals( "Bob", p2.getLastName());
        Assertions.assertEquals( "address1",  p2.getAddress1() );
        Assertions.assertEquals( "address2", p2.getAddress2() );
        Assertions.assertEquals( State.NC.toString(), p2.getState().toString() );
        Assertions.assertEquals( "Raleigh", p2.getCity() );
        Assertions.assertEquals( "11111", p2.getZip() );
        Assertions.assertEquals( "test@email.com", p2.getEmail() );
        Assertions.assertEquals( "111-222-3333", p2.getPhone() );

    }

    /**
     * Test add patient advocate assoication
     */
    @Test
    @Transactional
    public void testAddAssociaton () {
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( ADVOCATE_USER, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );

        final Patient patient = new Patient( new UserForm( PATIENT_USER, PW, Role.ROLE_PATIENT, 1 ) );

        final PatientAdvocateAssociationForm paForm = new PatientAdvocateAssociationForm();
        paForm.setPatient( PATIENT_USER );
        paForm.setPatientAdvocate( ADVOCATE_USER );
        paForm.setBilling( false );
        paForm.setVisit( true );
        paForm.setPrescription( false );

        final PatientAdvocateAssociation association = new PatientAdvocateAssociation( paForm );
        paService.save( association );

        advocate.addAssociation( (Long) association.getId() );
        patient.addAssociation( (Long) association.getId() );

        service.save( patient );
        perService.save( advocate );

        Assertions.assertEquals( perService.findAll().size(), 1 );
        Assertions.assertEquals( service.findAll().size(), 1 );
        Assertions.assertEquals( paService.findAll().size(), 1 );

        final PatientAdvocate checkAdv = (PatientAdvocate) perService.findById( ADVOCATE_USER );

        final Patient checkPat = service.findById( PATIENT_USER );

        final PatientAdvocateAssociation checkA = paService.findById( checkAdv.getAssociations().get( 0 ) );

        Assertions.assertEquals( checkAdv.getAssociations().get( 0 ), checkPat.getAssociations().get( 0 ) );
        Assertions.assertEquals( checkAdv.getAssociations().get( 0 ), (Long) checkA.getId() );
        Assertions.assertEquals( (Long) checkA.getId(), checkPat.getAssociations().get( 0 ) );

    }

    /**
     * Test delete patient advocate association
     */
    @Test
    @Transactional
    public void testDeleteAssociaton () {
        final PatientAdvocate advocate = new PatientAdvocate(
                new UserForm( ADVOCATE_USER, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );

        final Patient patient = new Patient( new UserForm( PATIENT_USER, PW, Role.ROLE_PATIENT, 1 ) );

        final PatientAdvocateAssociationForm paForm = new PatientAdvocateAssociationForm();
        paForm.setPatient( PATIENT_USER );
        paForm.setPatientAdvocate( ADVOCATE_USER );
        paForm.setBilling( false );
        paForm.setVisit( true );
        paForm.setPrescription( false );

        final PatientAdvocateAssociation association = new PatientAdvocateAssociation( paForm );

        paService.save( association );

        advocate.addAssociation( (Long) association.getId() );
        patient.addAssociation( (Long) association.getId() );

        perService.save( advocate );
        service.save( patient );

        Assertions.assertEquals( perService.findAll().size(), 1 );
        Assertions.assertEquals( service.findAll().size(), 1 );
        Assertions.assertEquals( paService.findAll().size(), 1 );

        final PatientAdvocateAssociation checkAsc = paService.getAssociation( patient.getUsername(),
                advocate.getUsername() );
        paService.delete( checkAsc );

        final PatientAdvocate checkAdv = (PatientAdvocate) perService.findById( ADVOCATE_USER );

        Assertions.assertTrue( checkAdv.deleteAssocation( (Long) checkAsc.getId() ) );

        perService.save( checkAdv );

        Assertions.assertEquals( perService.findAll().size(), 1 );
        Assertions.assertEquals( service.findAll().size(), 1 );
        Assertions.assertEquals( paService.findAll().size(), 0 );
    }
    
    
    /**
     * Test update all patient advocate
     */
    @Test
    @Transactional
    public void testUpdateAllPatientAdvocate () {
    	
    	//make sure there are no personnel in service
        Assertions.assertEquals( perService.findAll().size(), 0 );

        //create new patient advocate
        final PatientAdvocate p1 = new PatientAdvocate(
                new UserForm( ADVOCATE_USER, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        p1.setMiddleName( "Edgard" );
        p1.setNickName( "Eddy" );
        p1.setFirstName( "Billy" );
        perService.save( p1 );

        //make sure patient advocate is in service
        Assertions.assertEquals( perService.findAll().size(), 1 );

        PatientAdvocate p2 = (PatientAdvocate) perService.findById( ADVOCATE_USER );
        Assertions.assertEquals("PatientAdvocate [middleName=Edgard, nickName=Eddy]", p2.toString());

        //create a new form for updating
        final PatientAdvocateUserForm form = new PatientAdvocateUserForm();
        form.setFirstName( "Billy" );
        form.setMiddleName( "Edgard" );
        form.setNickname( "Eddy" );
        form.setLastName( "Bob" );
        form.setAddress1( "address1" );
        form.setAddress2( "address2" );
        form.setState( State.NC.toString() );
        form.setCity( "Raleigh" );
        form.setZip( "11111" );
        form.setEmail( "test@email.com" );
        form.setPhone( "111-222-3333" );
        Set<String> set = new HashSet<String>();
        set.add("ROLE_PATIENTADVOCATE");
        form.setRoles(set);
        form.setPassword(PW);
        form.setPassword2(PW);
        form.setEnabled(ADVOCATE_USER);
        p2.updateAll( form );

        //make sure everything is correctly updated
        Assertions.assertEquals( "Billy", p2.getFirstName() );
        Assertions.assertEquals( "Edgard", p2.getMiddleName() );
        Assertions.assertEquals( "Eddy", p2.getNickName() );
        Assertions.assertEquals( "Bob", p2.getLastName());
        Assertions.assertEquals( "address1",  p2.getAddress1() );
        Assertions.assertEquals( "address2", p2.getAddress2() );
        Assertions.assertEquals( State.NC.toString(), p2.getState().toString() );
        Assertions.assertEquals( "Raleigh", p2.getCity() );
        Assertions.assertEquals( "11111", p2.getZip() );
        Assertions.assertEquals( "test@email.com", p2.getEmail() );
        Assertions.assertEquals( "111-222-3333", p2.getPhone() );

        perService.save( p2 );

        //make sure everything is correctly updated
        Assertions.assertEquals( perService.findAll().size(), 1 );

        p2 = (PatientAdvocate) perService.findById( ADVOCATE_USER );

        Assertions.assertEquals( "Billy", p2.getFirstName() );
        Assertions.assertEquals( "Edgard", p2.getMiddleName() );
        Assertions.assertEquals( "Eddy", p2.getNickName() );
        Assertions.assertEquals( "Bob", p2.getLastName());
        Assertions.assertEquals( "address1",  p2.getAddress1() );
        Assertions.assertEquals( "address2", p2.getAddress2() );
        Assertions.assertEquals( State.NC.toString(), p2.getState().toString() );
        Assertions.assertEquals( "Raleigh", p2.getCity() );
        Assertions.assertEquals( "11111", p2.getZip() );
        Assertions.assertEquals( "test@email.com", p2.getEmail() );
        Assertions.assertEquals( "111-222-3333", p2.getPhone() );

    }
    

}
