package gov.usgs.cida.pubs.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
//import gov.usgs.cida.pubs.validation.link.ParentExistsValidatorForLinkDim;
//import gov.usgs.cida.pubs.validation.mpListPubsRel.ParentExistsValidatorForMpListPubsRel;
import gov.usgs.cida.pubs.validation.publication.ParentExistsValidatorForPublication;
//import gov.usgs.cida.pubs.validation.supersedeRel.ParentExistsValidatorForSupersedeRel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy={
//        ParentExistsValidatorForLinkDim.class,
//        ParentExistsValidatorForMpListPubsRel.class,
        ParentExistsValidatorForPublication.class,
//        ParentExistsValidatorForSupersedeRel.class
})
@Documented
public @interface ParentExists {

  String message() default "{pubs.noparent.exists}";
  Class<?>[] groups() default {};
  public abstract Class<?>[] payload() default {};

  String[] propertyName() default {};

}
