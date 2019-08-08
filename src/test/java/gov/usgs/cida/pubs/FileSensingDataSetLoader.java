package gov.usgs.cida.pubs;

import java.io.InputStream;
import java.sql.Date;
import java.time.Instant;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

public class FileSensingDataSetLoader extends AbstractDataSetLoader {

	@Override
	protected IDataSet createDataSet(Resource resource) throws Exception {
		if (resource.getFilename().endsWith("xml")) {
			return createXmlDataSet(resource);
		} else {
			return createCsvDataset(resource);
		}
	}

	private IDataSet createCsvDataset(Resource resource) throws Exception {
		return createReplacementDataSet(new CsvURLDataSet(resource.getURL()));
	}

	private IDataSet createXmlDataSet(Resource resource) throws Exception {
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		try (InputStream inputStream = resource.getInputStream()) {
			return createReplacementDataSet(builder.build(inputStream));
		}
	}

	private IDataSet createReplacementDataSet(IDataSet iDataSet) {
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(iDataSet);
		replacementDataSet.addReplacementObject("[today]", Date.from(Instant.now()));
		return replacementDataSet;
	}
}
