package gov.usgs.cida.pubs.validation.norelated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.validation.constraint.PurgeChecks;
import gov.usgs.cida.pubs.validation.constraint.norelated.NoRelatedValidatorForPwPublication;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, Publication.class,
			PwPublication.class, PwPublicationDao.class, PublicationDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseSetup("classpath:/testData/relatedPublications.xml")
public class NoRelatedValidatorForPwPublicationIT extends BaseIT {

	public static final String INDEX4_VALIDATION_RESULTS = 
			"[Field:id - Message:Index ID: index4 is linked to other publications: /n"
			+ "Index ID: index5 relationship: isPartOf/n"
			+ "Index ID: index6 relationship: isPartOf/n"
			+ "Index ID: index7 relationship: supersededBy/n"
			+ "Index ID: index8 relationship: supersededBy."
			+ " - Level:FATAL - Value:null]";

	@Autowired
	protected Validator validator;

	protected ConstraintValidatorContextImpl context;
	protected List<String> methodParameterNames = new ArrayList<>();
	protected PathImpl propertyPath = PathImpl.createPathFromString("abc");
	protected NoRelatedValidatorForPwPublication noRelatedValidator;
	protected PwPublication pub;

	@MockBean
	protected ConstraintDescriptor<?> constraintDescriptor;

	@BeforeEach
	public void setUp() throws Exception {
		context = new ConstraintValidatorContextImpl(DefaultClockProvider.INSTANCE, propertyPath, constraintDescriptor, null);
		noRelatedValidator = new NoRelatedValidatorForPwPublication();
	}

	@Test
	public void isValidNPETest() {
		assertTrue(noRelatedValidator.isValid(null, null));
		assertTrue(noRelatedValidator.isValid(null, context));
		assertTrue(noRelatedValidator.isValid(pub, null));

		assertTrue(noRelatedValidator.isValid(pub, context));
	}

	@Test
	public void isValidDirect() {
		pub = new PwPublication(8, "index8");
		assertTrue(noRelatedValidator.isValid(pub, context));
	}

	@Test
	public void isNotValidDirect() {
		pub = new PwPublication(4, "index4");
		assertFalse(noRelatedValidator.isValid(pub, context));
	}

	@Test
	public void isValidWired() {
		pub = new PwPublication(8, "index4");
		pub.setValidationErrors(validator.validate(pub, PurgeChecks.class));
		assertTrue(pub.isValid());
	}

	@Test
	public void isNotValidWired() {
		pub = new PwPublication(4, "index4");
		pub.setValidationErrors(validator.validate(pub, PurgeChecks.class));
		assertFalse(pub.isValid());
		assertEquals(INDEX4_VALIDATION_RESULTS, pub.getValidationErrors().getValidationErrors().toString());
	}
}
