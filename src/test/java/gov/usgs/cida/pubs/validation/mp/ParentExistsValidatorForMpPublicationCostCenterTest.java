package gov.usgs.cida.pubs.validation.mp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

@Ignore
public class ParentExistsValidatorForMpPublicationCostCenterTest extends BaseSpringTest {

    @Autowired
    public Validator validator;

//    @Autowired
//    public ConstraintValidatorContextImpl context;

//    @Test
//    public void isValidTest() {
//        MpPublicationCostCenter pcc = new MpPublicationCostCenter();
//        pcc.setPublicationId(-1);
//        ParentExistsValidatorForMpPublicationCostCenter testValidator = new ParentExistsValidatorForMpPublicationCostCenter();
//        @SuppressWarnings({ "rawtypes", "unchecked" })
//        ConstraintDescriptor<ParentExists> constraintDescriptor = new ConstraintDescriptorImpl(null, null, 
//                ParentExists, null, null);
////        ConstraintValidatorContext context = new ConstraintValidatorContextImpl(List<String> methodParameterNames, PathImpl propertyPath, 
////                ConstraintDescriptor<?> constraintDescriptor);
//        ConstraintValidatorContext context = new ConstraintValidatorContextImpl(null, null, null);
////        assertFalse(testValidator.isValid(pcc, context));
//        assertNull(validator.validate(new MpPublicationCostCenter()));
//    }

}
