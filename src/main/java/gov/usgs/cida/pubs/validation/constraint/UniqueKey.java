package gov.usgs.cida.pubs.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpListPublication;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpPublication;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpPublicationContributor;
import gov.usgs.cida.pubs.validation.mp.unique.UniqueKeyValidatorForMpPublicationCostCenter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

/**
 * @author drsteini
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy={
		UniqueKeyValidatorForMpListPublication.class,
		UniqueKeyValidatorForMpPublication.class,
		UniqueKeyValidatorForMpPublicationContributor.class,
		UniqueKeyValidatorForMpPublicationCostCenter.class
})
@Documented
public @interface UniqueKey {

	String message() default "Duplicates found";
	Class<?>[] groups() default {};
	public abstract Class<?>[] payload() default {};

	String[] propertyName() default {};

}
