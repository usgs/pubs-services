package gov.usgs.cida.pubs.validation.norelated;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Before;
import org.junit.Test;
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

	@Autowired
	public Validator validator;

	protected ConstraintValidatorContextImpl context;
	protected List<String> methodParameterNames = new ArrayList<>();
	protected PathImpl propertyPath = PathImpl.createPathFromString("abc");
	protected NoRelatedValidatorForPwPublication noRelatedValidator;
	protected PwPublication pub;

	@MockBean
	protected ConstraintDescriptor<?> constraintDescriptor;

	@Before
	public void setUp() throws Exception {
		context = new ConstraintValidatorContextImpl(methodParameterNames, DefaultClockProvider.INSTANCE, propertyPath, constraintDescriptor, null);
		noRelatedValidator = new NoRelatedValidatorForPwPublication();
		pub = new PwPublication();
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
		pub.setId(8);
		assertTrue(noRelatedValidator.isValid(pub, context));
	}

	@Test
	public void isNotValidDirect() {
		pub.setId(4);
		assertFalse(noRelatedValidator.isValid(pub, context));
	}

	@Test
	public void isValidWired() {
		pub.setId(8);
		pub.setValidationErrors(validator.validate(pub, PurgeChecks.class));
		assertTrue(pub.isValid());
	}

	@Test
	public void isNotValidWired() {
		pub.setId(4);
		pub.setValidationErrors(validator.validate(pub, PurgeChecks.class));
		assertFalse(pub.isValid());
		assertEquals("[Field:id - Message:4 is linked to other publications: /n"
				+ "IndexId: index5 relationship: isPartOf/n"
				+ "IndexId: index6 relationship: isPartOf/n"
				+ "IndexId: index7 relationship: supersededBy/n"
				+ "IndexId: index8 relationship: supersededBy."
				+ " - Level:FATAL - Value:null]", pub.getValidationErrors().getValidationErrors().toString());
	}
}
