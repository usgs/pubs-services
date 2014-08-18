package gov.usgs.cida.pubs.validation;

import gov.usgs.cida.pubs.domain.BaseDomain;
//import gov.usgs.cida.pubs.domain.MpListPubsRel;
import gov.usgs.cida.pubs.validation.constraint.NoChildren;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoChildrenValidator implements ConstraintValidator<NoChildren, BaseDomain<?>> {

    /** {@inheritDoc}
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(NoChildren constraintAnnotation) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc}
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(BaseDomain<?> value, ConstraintValidatorContext context) {
        boolean rtn = true;
        Map<String, Object> filters = new HashMap<String,Object>();
        filters.put("listId", value.getId());

        //TODO put back when lists are implemented.
//        if (value.getClass().isAssignableFrom(MpListPubsRel.class)) {
//            List<MpListPubsRel> list = MpListPubsRel.getDao().getByMap(filters);
//            if (0 != list.size()) {
//                rtn = false;
//            }
//        }

        return rtn;
    }

}
