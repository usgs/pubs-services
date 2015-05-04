package gov.usgs.cida.pubs.validation;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public abstract class BaseValidatorTest extends BaseSpringTest {

	protected ConstraintValidatorContextImpl context;
	
	protected List<String> methodParameterNames = new ArrayList<>();
	protected PathImpl propertyPath = PathImpl.createPathFromString("");
	@Mock
	protected ConstraintDescriptor<?> constraintDescriptor;
	
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        context = new ConstraintValidatorContextImpl(methodParameterNames, propertyPath, constraintDescriptor);
    }

}
