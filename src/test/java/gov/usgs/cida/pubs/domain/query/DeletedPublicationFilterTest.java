package gov.usgs.cida.pubs.domain.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DeletedPublicationFilterTest {
	public static final Integer PAGE_99 = 99;
	public static final Integer PAGE_SIZE_12 = 12;

	@Test
	public void getPageNumber() {
		DeletedPublicationFilter dpf = new DeletedPublicationFilter();
		dpf.setPage_number(PAGE_99);

		//bad pageSize = null pageNumber
		dpf.setPage_size(null);
		assertNull(dpf.getPage_number());

		dpf.setPage_size(0);
		assertNull(dpf.getPage_number());

		//good pageSize = use pageNumber
		dpf.setPage_size(PAGE_SIZE_12);
		assertEquals(PAGE_99, dpf.getPage_number());
	}

	@Test
	public void getPageRowStart() {
		DeletedPublicationFilter dpf = new DeletedPublicationFilter();

		//bad pageSize &/or bad pageNumber = null pageRowStart
		dpf.setPage_number(null);
		dpf.setPage_size(null);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(0);
		dpf.setPage_size(null);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(null);
		dpf.setPage_size(0);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(PAGE_99);
		dpf.setPage_size(null);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(null);
		dpf.setPage_size(PAGE_SIZE_12);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(0);
		dpf.setPage_size(0);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(PAGE_99);
		dpf.setPage_size(0);
		assertNull(dpf.getPage_row_start());

		dpf.setPage_number(0);
		dpf.setPage_size(PAGE_SIZE_12);
		assertNull(dpf.getPage_row_start());

		//good pageSize & good pageNumber = get a pageRowStart
		dpf.setPage_number(PAGE_99);
		dpf.setPage_size(PAGE_SIZE_12);
		assertEquals(1176, dpf.getPage_row_start().intValue());
	}
}
