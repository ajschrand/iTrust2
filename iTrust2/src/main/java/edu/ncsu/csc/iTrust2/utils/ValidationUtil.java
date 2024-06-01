package edu.ncsu.csc.iTrust2.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Class with utility methods to be used in forms and entity validation
 *
 * @author bvolpat
 *
 */
public class ValidationUtil {

    private ValidationUtil () {

    }

    /**
     * Execute validations in object, using its class definition to read
     * constraints. Based on JBoss Loom:
     * https://www.javatips.net/api/jboss-migration-master/engine/src/main/java/org/jboss/loom/utils/Utils.java
     *
     * @param object
     *            Object to validate
     * @param <T>
     *            Object type to validate
     * @throws IllegalArgumentException
     *             If the provided object cannot be validated properly
     */
    public static <T extends Object> void validate ( final T object ) throws IllegalArgumentException {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        final Set<ConstraintViolation<T>> valRes = validator.validate( object );
        if ( !valRes.isEmpty() ) {
            final StringBuilder sb = new StringBuilder( "Validation failed for: " );
            sb.append( object );

            for ( final ConstraintViolation<T> fail : valRes ) {
                sb.append( "\n  " ).append( fail.getPropertyPath() + " - " + fail.getMessage() );
            }
            throw new IllegalArgumentException( sb.toString() );
        }
    }

}
