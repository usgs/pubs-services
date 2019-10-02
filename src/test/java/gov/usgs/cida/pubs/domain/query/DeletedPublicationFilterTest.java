package gov.usgs.cida.pubs.domain.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DeletedPublicationFilterTest {
	public static final Integer PAGE_99 = 99;
	public static final Integer PAGE_SIZE_12 = 12;

	@Test
	public void getPageNumber() {
		DeletedPublicationFilter dpf = new DeletedPublicationFilter();
		dpf.setPageNumber(PAGE_99);

		//bad pageSize = null pageNumber
		dpf.setPageSize(null);
		assertNull(dpf.getPageNumber());

		dpf.setPageSize(0);
		assertNull(dpf.getPageNumber());

		//good pageSize = use pageNumber
		dpf.setPageSize(PAGE_SIZE_12);
		assertEquals(PAGE_99, dpf.getPageNumber());
	}

	@Test
	public void getPageRowStart() {
		DeletedPublicationFilter dpf = new DeletedPublicationFilter();

		//bad pageSize &/or bad pageNumber = null pageRowStart
		dpf.setPageNumber(null);
		dpf.setPageSize(null);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(0);
		dpf.setPageSize(null);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(null);
		dpf.setPageSize(0);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(PAGE_99);
		dpf.setPageSize(null);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(null);
		dpf.setPageSize(PAGE_SIZE_12);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(0);
		dpf.setPageSize(0);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(PAGE_99);
		dpf.setPageSize(0);
		assertNull(dpf.getPageRowStart());

		dpf.setPageNumber(0);
		dpf.setPageSize(PAGE_SIZE_12);
		assertNull(dpf.getPageRowStart());

		//good pageSize & good pageNumber = get a pageRowStart
		dpf.setPageNumber(PAGE_99);
		dpf.setPageSize(PAGE_SIZE_12);
		assertEquals(1176, dpf.getPageRowStart().intValue());
	}
}
