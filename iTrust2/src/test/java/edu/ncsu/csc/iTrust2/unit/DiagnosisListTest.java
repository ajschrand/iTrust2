package edu.ncsu.csc.iTrust2.unit;

import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.DiagnosisListForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Class to test that DiagnosisList and DiagnosisListForms are created from each
 * other properly.
 *
 * @author bvolpat
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class DiagnosisListTest {

    @Test
    @Transactional
    public void testCodes () {

        final Diagnosis diag = new Diagnosis();

        final ICDCode icdCode = new ICDCode();
        icdCode.setCode( "H26.9" );
        icdCode.setDescription( "Cataracts" );
        icdCode.setIsOphthalmology( true );

        final OfficeVisit officeVisit = new OfficeVisit();
        officeVisit.setId( 1L );
        officeVisit.setDate( ZonedDateTime.now() );
        officeVisit.setHcp( new Personnel( new UserForm( "hcp", "hcp", Role.ROLE_HCP, 1 ) ) );

        diag.setCode( icdCode );
        diag.setNote( "Good note" );
        diag.setVisit( officeVisit );

        final DiagnosisListForm form = new DiagnosisListForm( diag );
        Assertions.assertNotNull( form );
        Assertions.assertEquals( diag.getVisit().getDate(), form.getVisitDate() );
        Assertions.assertEquals( "Good note", form.getNote() );
        Assertions.assertEquals( "hcp", form.getHcp().getUsername() );
    }

}
