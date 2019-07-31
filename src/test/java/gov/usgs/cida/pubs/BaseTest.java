package gov.usgs.cida.pubs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.ReplacementDataSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileCopyUtils;

import com.github.springtestdbunit.dataset.ReplacementDataSetModifier;

import gov.usgs.cida.pubs.domain.BaseDomain;

@RunWith(SpringRunner.class)
@TestExecutionListeners({DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
	MockitoTestExecutionListener.class})
public abstract class BaseTest {
	public static final Log LOG = LogFactory.getLog(BaseTest.class);

	public static final String SEARCH_POLYGON = "polygon((-122.3876953125 37.80869897600677,-122.3876953125 36.75979104322286,-123.55224609375 36.75979104322286," +
				"-123.55224609375 37.80869897600677,-122.3876953125 37.80869897600677))";
	public static final String GEOGRAPHIC_EXTENTS = "{\"type\": \"FeatureCollection\",\"features\": [{\"type\": \"Feature\",\"properties\": {},\"geometry\": {"
				+ "\"type\": \"Polygon\",\"coordinates\": [[[-91.91162109375,45.69850658738846],[-91.91162109375,47.16730970131578],"
				+ "[-90.3955078125,47.16730970131578],[-90.3955078125,45.69850658738846],[-91.91162109375,45.69850658738846]]]}}]}";
	/** random for the class. */
	protected static final Random RANDOM = new Random();

	/**
	 * @return next random positive int.
	 */
	protected static int randomPositiveInt() {
		return RANDOM.nextInt(999999999) + 1;
	}

	protected Integer id;

	protected class IdModifier extends ReplacementDataSetModifier {

		@Override
		protected void addReplacements(ReplacementDataSet dataSet) {
			dataSet.addReplacementSubstring("[id]", id.toString());
		}

	}

	public void assertDaoTestResults(final Class<?> inClass, final Object inObject, final Object resultObject) {
		assertDaoTestResults(inClass, inObject, resultObject, null, false, false, false);
	}

	public void assertDaoTestResults(final Class<?> inClass, final Object inObject, final Object resultObject, final List<String> ignoreProperties, final boolean ignoreInsertAudit, final boolean ignoreUpdateAudit) {
		assertDaoTestResults(inClass, inObject, resultObject, ignoreProperties, ignoreInsertAudit, ignoreUpdateAudit, false);
	}

	public void assertDaoTestResults(final Class<?> inClass, final Object inObject, final Object resultObject, final List<String> ignoreProperties, final boolean ignoreInsertAudit, final boolean ignoreUpdateAudit,
			final boolean allowNull) {
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
						}
						if (resultProp instanceof Collection) {
							//TODO - could try to match the lists...
							assertEquals(prop.getName(), ((Collection<?>) inProp).size(), ((Collection<?>) resultProp).size());
						} else {
							assertProperty(inProp, resultProp, prop);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("Error getting property: " + prop.getName(), e);
				}
			}
		}
	}

	private void assertProperty(final Object inProp, final Object resultProp, final PropertyDescriptor prop) throws Exception {
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

	public String harmonizeXml(String xmlDoc) {
		//remove carriage returns, new lines, tabs, spaces between elements, spaces at the start of the string.
		return xmlDoc.replace("\r", "").replace("\n", "").replace("\t", "").replaceAll("> *<", "><").replaceAll("^ *", "");
	}

	public String getCompareFile(String file) throws IOException {
		return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("testResult/" + file).getInputStream()));
	}

	public JSONObject getRtnAsJSONObject(MvcResult rtn) throws Exception {
		return new JSONObject(rtn.getResponse().getContentAsString());		
	}

	public JSONArray getRtnAsJSONArray(MvcResult rtn) throws Exception {
		return new JSONArray(rtn.getResponse().getContentAsString());		
	}

	public String currentYear = String.valueOf(LocalDate.now().getYear());

}
