package gov.usgs.cida.pubs.validation;

import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class BaseValidatorTest extends BaseSpringDaoTest {

	protected ConstraintValidatorContextImpl context;
	
	protected List<String> methodParameterNames = new ArrayList<>();
	protected PathImpl propertyPath = PathImpl.createPathFromString("");
	@Mock
	protected ConstraintDescriptor<?> constraintDescriptor;
	
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        context = new ConstraintValidatorContextImpl(methodParameterNames, propertyPath, constraintDescriptor);
    }

}
