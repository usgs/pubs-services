package gov.usgs.cida.pubs;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import gov.usgs.cida.pubs.springinit.DbTestConfig;

@ActiveProfiles("it")
@TestExecutionListeners({DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
	})
@DbUnitConfiguration(dataSetLoader=FileSensingDataSetLoader.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional(propagation=Propagation.NOT_SUPPORTED)
@Import({DbTestConfig.class})
@DirtiesContext
public abstract class BaseIT extends BaseTest {

	protected static DocumentBuilder docBuilder;

	public static void setUpDocBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		try {
			docBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
	}

}
