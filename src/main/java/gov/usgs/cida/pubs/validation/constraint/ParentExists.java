package gov.usgs.cida.pubs.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import gov.usgs.cida.pubs.validation.mp.parent.ParentExistsValidatorForMpListPublication;
import gov.usgs.cida.pubs.validation.mp.parent.ParentExistsValidatorForMpPublication;
import gov.usgs.cida.pubs.validation.mp.parent.ParentExistsValidatorForMpPublicationContributor;
import gov.usgs.cida.pubs.validation.mp.parent.ParentExistsValidatorForMpPublicationCostCenter;
import gov.usgs.cida.pubs.validation.mp.parent.ParentExistsValidatorForMpPublicationLink;
import gov.usgs.cida.pubs.validation.mp.parent.ParentExistsValidatorForPersonContributor;
import gov.usgs.cida.pubs.validation.parent.ParentExistsValidatorForPublicationSeries;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy={
	ParentExistsValidatorForMpListPublication.class,
	ParentExistsValidatorForMpPublication.class,
	ParentExistsValidatorForMpPublicationContributor.class,
	ParentExistsValidatorForMpPublicationCostCenter.class,
	ParentExistsValidatorForMpPublicationLink.class,
	ParentExistsValidatorForPersonContributor.class,
	ParentExistsValidatorForPublicationSeries.class
})
@Documented
public @interface ParentExists {

	String message() default "{pubs.noparent.exists}";
	Class<?>[] groups() default {};
	public abstract Class<?>[] payload() default {};

	String[] propertyName() default {};

}
