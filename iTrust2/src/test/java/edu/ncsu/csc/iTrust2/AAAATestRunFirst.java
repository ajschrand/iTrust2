package edu.ncsu.csc.iTrust2;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.iTrust2.common.DBUtils;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class AAAATestRunFirst {

    @Autowired
    DataSource ds;

    @Test
    public void resetDB () {
        DBUtils.resetDB( ds );
    }

}
