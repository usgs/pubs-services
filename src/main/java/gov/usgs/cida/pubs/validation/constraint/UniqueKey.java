package gov.usgs.cida.pubs.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
//import gov.usgs.cida.pubs.validation.link.UniqueKeyValidatorForLinkDim;
//import gov.usgs.cida.pubs.validation.mpListPubsRel.UniqueKeyValidatorForMpListPubsRel;
import gov.usgs.cida.pubs.validation.publication.UniqueKeyValidatorForPublication;
//import gov.usgs.cida.pubs.validation.supersedeRel.UniqueKeyValidatorForSupersedeRel;

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
//        UniqueKeyValidatorForLinkDim.class,
//        UniqueKeyValidatorForMpListPubsRel.class,
        UniqueKeyValidatorForPublication.class,
//        UniqueKeyValidatorForSupersedeRel.class
})
@Documented
public @interface UniqueKey {

  String message() default "Duplicates found";    
  Class<?>[] groups() default {};  
  public abstract Class<?>[] payload() default {};

  String[] propertyName() default {}; 

}
