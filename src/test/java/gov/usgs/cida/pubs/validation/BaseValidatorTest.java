package gov.usgs.cida.pubs.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

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

	public static final String NOT_NULL_MSG = "may not be null";
	public static final String BAD_URL_MSG = "must be a valid URL";

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

	public void assertValidationResults(List<ValidatorResult> actual, String... expected) {
		List<String> actualStrings = new ArrayList<>();
		for (ValidatorResult x : actual) {
			actualStrings.add(x.toString());
		}
		assertThat(actualStrings, containsInAnyOrder(expected));
	}

}
