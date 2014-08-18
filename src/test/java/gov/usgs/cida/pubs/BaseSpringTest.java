package gov.usgs.cida.pubs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.BaseDomain;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

/**
 * This is the base class used by test classes that need to use the database.
 * All tests that require access to the database should extend this base class.
 * 
 * @author drsteini
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/testContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionDbUnitTestExecutionListener.class })
public abstract class BaseSpringTest {

    /** random for the class. */
    protected static final Random RANDOM = new Random();

    /**
     * @return next random positive int.
     */
    protected static int randomPositiveInt() {
        return RANDOM.nextInt(999999999) + 1;
    }

    /** Log for errors etc. */
    public static final Log LOG = LogFactory.getLog(BaseSpringTest.class);

    public void assertDaoTestResults(final Class<?> inClass, final Object inObject, final Object resultObject) {
        assertDaoTestResults(inClass, inObject, resultObject, null, false, false, false);
    }

    public void assertDaoTestResults(final Class<?> inClass, final Object inObject, final Object resultObject,
            final List<String> ignoreProperties, final boolean ignoreInsertAudit, final boolean ignoreUpdateAudit) {
        assertDaoTestResults(inClass, inObject, resultObject, ignoreProperties, ignoreInsertAudit, ignoreUpdateAudit, false);
    }

    public void assertDaoTestResults(final Class<?> inClass, final Object inObject, final Object resultObject,
            final List<String> ignoreProperties, final boolean ignoreInsertAudit, final boolean ignoreUpdateAudit, final boolean allowNull) {
        for (PropertyDescriptor prop : getPropertyMap(inClass).values()) {
            if ((null != ignoreProperties && ignoreProperties.contains(prop.getName())) 
                    || (ignoreInsertAudit && (prop.getName().contentEquals("insertDate") || prop.getName().contentEquals("insertUsername")))
                    || (ignoreUpdateAudit && (prop.getName().contentEquals("updateDate") || prop.getName().contentEquals("updateUsername")))) {
                assertTrue(true);
            } else {
                try {
                    if (null != prop.getReadMethod() && !"getClass".contentEquals(prop.getReadMethod().getName())) {
                        Object inProp = prop.getReadMethod().invoke(inObject);
                        Object resultProp = prop.getReadMethod().invoke(resultObject);
                        if (!allowNull) {
                            assertNotNull(prop.getName() + " original is null.", inProp);
                            assertNotNull(prop.getName() + " result is null.", resultProp);
                        };
                        if (resultProp instanceof Collection) {
                            //TODO - could try to match the lists...
                            assertEquals(prop.getName(), ((Collection<?>) inProp).size(), ((Collection<?>) resultProp).size());
                        } else {
                            assertProperty(inProp, resultProp, prop);
                        }
                    }
                }  catch (Exception e) {
                    throw new RuntimeException("Error getting property: " + prop.getName(), e);
                }
            }
        }
    }

    private void assertProperty(final Object inProp, final Object resultProp,
            final PropertyDescriptor prop) throws Exception {
        if (resultProp instanceof BaseDomain) {
            LOG.info(prop.getName() + " input ID: " + ((BaseDomain<?>) inProp).getId() 
                    + " result ID: " + ((BaseDomain<?>) resultProp).getId());
            assertEquals(prop.getName(), ((BaseDomain<?>) inProp).getId(), ((BaseDomain<?>) resultProp).getId());
        } else {
            LOG.info(prop.getName() + " input: " + inProp + " result: " + resultProp);
            assertEquals(prop.getName(), inProp, resultProp);
        }
    }

    public HashMap<String, PropertyDescriptor> getPropertyMap(final Class<?> inClass) {
        HashMap<String, PropertyDescriptor> returnMethodList = new HashMap<String, PropertyDescriptor>();
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(inClass);
        } catch (IntrospectionException e1) {
            LOG.error("error introspecting bean: " + inClass.getCanonicalName(), e1);
        }
        returnMethodList = new HashMap<String, PropertyDescriptor>();
        if (info != null) {
            //for each of this objects setter method.
            for (PropertyDescriptor propDesc : info.getPropertyDescriptors()) {
                //assuming JavaBean convention
                returnMethodList.put(propDesc.getName(), propDesc);
            }
        }
        return returnMethodList;
    }

    protected String getStringFromFile(final String fileName) {
        StringBuilder contents = new StringBuilder();
        try {
            // use buffering, reading one line at a time
            // FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
            try {
                String line = null; // not declared within while loop
                /*
                 * readLine is a bit quirky : it returns the content of a line
                 * MINUS the newline. it returns null only for the END of the
                 * stream. it returns an empty String if two newlines appear in
                 * a row.
                 */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }

    protected String stripCrLf(final String input) {
        return input.replace("\n", "").replace("\r", "");
    }

    @Resource(name="expectedGetMpPub1")
    public String expectedGetMpPub1;

}
