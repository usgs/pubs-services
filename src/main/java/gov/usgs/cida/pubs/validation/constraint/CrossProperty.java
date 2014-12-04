package gov.usgs.cida.pubs.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import gov.usgs.cida.pubs.validation.mp.crossproperty.CrossPropertyValidatorForMpPublication;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

/** 
 * 	Constraint validation for validating between properties of a POJO.
 *  For example, when a publication has a publicationSubtype of USGS Numbered Series it must also have a 
 *  	seriesTitle specified.
 */

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy={
        CrossPropertyValidatorForMpPublication.class
})
@Documented
public @interface CrossProperty {

  String message() default "{pubs.crossproperty.error}";
  Class<?>[] groups() default {};
  public abstract Class<?>[] payload() default {};

  String[] propertyName() default {};

}
