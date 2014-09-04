package gov.usgs.cida.pubs.validation.mp;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import gov.usgs.cida.pubs.validation.constraint.MpListProtectedValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy=MpListProtectedValidator.class)
@Documented
public @interface MpListProtected {

	  String message() default "{List.delete.protected}";
	  Class<?>[] groups() default {};
	  public abstract Class<?>[] payload() default {};

	  String protectedIds() default "";
}
