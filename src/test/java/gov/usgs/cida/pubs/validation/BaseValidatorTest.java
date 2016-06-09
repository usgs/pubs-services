package gov.usgs.cida.pubs.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.engine.time.DefaultTimeProvider;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.usgs.cida.pubs.BaseSpringTest;

public abstract class BaseValidatorTest extends BaseSpringTest {

	protected ConstraintValidatorContextImpl context;
	
	protected List<String> methodParameterNames = new ArrayList<>();
	protected PathImpl propertyPath = PathImpl.createPathFromString("");
	@Mock
	protected ConstraintDescriptor<?> constraintDescriptor;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new ConstraintValidatorContextImpl(methodParameterNames, DefaultTimeProvider.getInstance(), propertyPath, constraintDescriptor);
	}

}
