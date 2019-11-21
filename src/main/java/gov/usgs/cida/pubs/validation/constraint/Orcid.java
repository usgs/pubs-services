package gov.usgs.cida.pubs.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import gov.usgs.cida.pubs.validation.constraint.Orcid.List;
import gov.usgs.cida.pubs.validation.orcid.OrcidValidator;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy={
		OrcidValidator.class
})
@Documented
@Repeatable(List.class)
public @interface Orcid {

	String message() default "{pubs.orcid.error}";
	Class<?>[] groups() default {};
	public abstract Class<?>[] payload() default {};

	@Target({FIELD})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		Orcid[] value();
	}

}
