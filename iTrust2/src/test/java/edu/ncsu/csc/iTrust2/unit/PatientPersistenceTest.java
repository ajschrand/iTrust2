package edu.ncsu.csc.iTrust2.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.PatientAdvocateAssociation;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PatientAdvocateAssociationService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.VaccineTypeService;

/**
 * Tests the patient model
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class PatientPersistenceTest {

    @Autowired
    private PatientService<Patient>           service;

    @Autowired
    private PatientAdvocateAssociationService Aservice;

    @Autowired
    private VaccineTypeService                s;

    private static final String               USER_1 = "demoTestUser1";

    private static final String               USER_2 = "demoTestUser2";

    private static final String               USER_3 = "demoTestUser3";

    private static final String               PW     = "123456";

    @BeforeEach
    public void setup () {
        service.deleteAll();
        Aservice.deleteAll();
    }

    /**
     * Tests adding an association
     */
    @Test
    @Transactional
    public void testAddAssociation () {

        Assertions.assertEquals( 0, service.count(), "There should be no Patient records in the system!" );

        final Patient p1 = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate adv1 = new PatientAdvocate( new UserForm( USER_2, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        final PatientAdvocate adv2 = new PatientAdvocate( new UserForm( USER_3, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        final PatientAdvocateAssociation a = new PatientAdvocateAssociation( p1.getUsername(), adv1.getUsername() );
        final PatientAdvocateAssociation a2 = new PatientAdvocateAssociation( p1.getUsername(), adv2.getUsername() );
        p1.addAssociation( (Long) a.getId() );
        p1.addAssociation( (Long) a2.getId() );
        adv1.addAssociation( (Long) a.getId() );
        adv1.addAssociation( (Long) a2.getId() );
        service.save( p1 );

        Aservice.save( a );
        assertEquals( Aservice.count(), 1 );
        final PatientAdvocateAssociation aGet = Aservice.getAssociation( p1.getUsername(), adv1.getUsername() );
        assertNotNull( aGet );
        assertEquals( a, aGet );
        assertEquals( Aservice.getPatientAdvocateAssociations( adv1.getUsername() ).size(), 1 );
        assertEquals( Aservice.getPatientAssociations( p1.getUsername() ).size(), 1 );

        Aservice.save( a2 );
        assertEquals( Aservice.count(), 2 );
        final PatientAdvocateAssociation aGet2 = Aservice.getAssociation( p1.getUsername(), adv2.getUsername() );
        assertNotNull( aGet2 );
        assertEquals( a2, aGet2 );
        assertEquals( Aservice.getPatientAdvocateAssociations( adv2.getUsername() ).size(), 1 );
        assertEquals( Aservice.getPatientAssociations( p1.getUsername() ).size(), 2 );

    }

    /**
     * Tests deleting an association
     */
    @Test
    @Transactional
    public void testDeleteAssociation () {

        Assertions.assertEquals( 0, service.count(), "There should be no Patient records in the system!" );

        final Patient p1 = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate a1 = new PatientAdvocate( new UserForm( USER_2, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        final PatientAdvocateAssociation a = new PatientAdvocateAssociation( p1.getUsername(), a1.getUsername() );
        p1.addAssociation( (Long) a.getId() );
        service.save( p1 );
        Aservice.save( a );
        PatientAdvocateAssociation aGet = Aservice.getAssociation( p1.getUsername(), a1.getUsername() );
        assertNotNull( aGet );
        assertEquals( a, aGet );
        assertEquals( Aservice.count(), 1 );
        assertEquals( Aservice.getPatientAdvocateAssociations( a1.getUsername() ).size(), 1 );
        assertEquals( Aservice.getPatientAssociations( p1.getUsername() ).size(), 1 );

        Aservice.delete( a );
        assertEquals( Aservice.count(), 0 );
        assertEquals( Aservice.getPatientAdvocateAssociations( a1.getUsername() ).size(), 0 );
        assertEquals( Aservice.getPatientAssociations( p1.getUsername() ).size(), 0 );
        aGet = Aservice.getAssociation( p1.getUsername(), a1.getUsername() );
        assertNull( aGet );
    }

    /**
     * Tests getting patient association permissions
     */
    @Test
    @Transactional
    public void testGetPermissions () {

        Assertions.assertEquals( 0, service.count(), "There should be no Patient records in the system!" );

        final Patient p1 = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );
        final PatientAdvocate adv1 = new PatientAdvocate( new UserForm( USER_2, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );
        final PatientAdvocate adv2 = new PatientAdvocate( new UserForm( USER_3, PW, Role.ROLE_PATIENTADVOCATE, 1 ) );

        final PatientAdvocateAssociation a = new PatientAdvocateAssociation( p1.getUsername(), adv1.getUsername() );
        a.setVisit( true );

        final PatientAdvocateAssociation a2 = new PatientAdvocateAssociation( p1.getUsername(), adv2.getUsername() );
        a2.setPrescription( true );

        p1.addAssociation( (Long) a.getId() );
        p1.addAssociation( (Long) a2.getId() );

        service.save( p1 );
        Aservice.save( a );
        Aservice.save( a2 );
        assertEquals( Aservice.count(), 2 );

        final Patient patient = service.findById( p1.getUsername() );
        assertNotNull( patient );

        final PatientAdvocateAssociation aGet = Aservice.getAssociation( patient.getUsername(), adv1.getUsername() );
        assertFalse( aGet.getBilling() );
        assertTrue( aGet.getVisit() );
        assertFalse( aGet.getPrescription() );

        final PatientAdvocateAssociation aGet2 = Aservice.getAssociation( patient.getUsername(), adv2.getUsername() );
        assertFalse( aGet2.getBilling() );
        assertFalse( aGet2.getVisit() );
        assertTrue( aGet2.getPrescription() );

    }

}
