package gov.usgs.cida.pubs.transform.intfc;

import java.io.IOException;

public interface ITransformer {

	void write(Object object) throws IOException;

}
