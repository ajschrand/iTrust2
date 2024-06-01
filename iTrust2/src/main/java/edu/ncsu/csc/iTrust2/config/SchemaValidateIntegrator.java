package edu.ncsu.csc.iTrust2.config;

import java.util.EnumSet;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class that validates the validity of the schema, and drops/recreates if it
 * has found any issue.
 * 
 * @author bvolpat
 */
@Component
public class SchemaValidateIntegrator implements Integrator, IntegratorProvider {

    /** Logger, to print status messages to the console on validation */
    private static final Logger LOG = LoggerFactory.getLogger( SchemaValidateIntegrator.class );

    @Override
    public List<Integrator> getIntegrators () {
        return List.of( this );
    }

    @Override
    public void integrate ( final Metadata metadata, final SessionFactoryImplementor sessionFactory,
            final SessionFactoryServiceRegistry serviceRegistry ) {

        try {
            LOG.info( "Validating schema..." );
            new SchemaValidator().validate( metadata );
            LOG.info( "Schema validated!" );
        }
        catch ( final Exception e ) {
            LOG.error( "Incompatible database, dropping and recreating...", e );

            final SchemaExport export = new SchemaExport();
            export.drop( EnumSet.of( TargetType.DATABASE ), metadata );
            export.create( EnumSet.of( TargetType.DATABASE ), metadata );

        }
    }

    @Override
    public void disintegrate ( final SessionFactoryImplementor sessionFactory,
            final SessionFactoryServiceRegistry serviceRegistry ) {
        // just ignore it :)
    }

}
