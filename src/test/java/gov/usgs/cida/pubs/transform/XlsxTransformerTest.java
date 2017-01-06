package gov.usgs.cida.pubs.transform;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class XlsxTransformerTest {
	private int rowCount = 1;

	@Test
	public void writeTest() throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, String> mapping = new HashMap<>();
		mapping.put("A", "colA");
		mapping.put("B", "colB");
		mapping.put("C", "colC");
		mapping.put("D", "colD");
		mapping.put("E", "colE");
		mapping.put("F", "colF");

		XlsxTransformer transformer = new XlsxTransformer(baos, mapping);
		try {
			transformer.write(getTestRow());
			transformer.end();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			Workbook wb = new XSSFWorkbook(bais);
			assertNotNull(wb);
			Sheet sheet = wb.getSheet("report");
			assertNotNull(sheet);
			assertEquals(1, sheet.getLastRowNum());
			Row row0 = sheet.getRow(0);
			assertNotNull(row0);
			Iterator<Cell> x = row0.cellIterator();
			while (x.hasNext()) {
				Cell c = x.next();
				switch (c.getStringCellValue()) {
				case "colA":
					break;
				case "colB":
					break;
				case "colC":
					break;
				case "colD":
					break;
				case "colE":
					break;
				case "colF":
					break;
				default:
					fail(c.getStringCellValue() + " is not valid");
				}
			}

			Row row1 = sheet.getRow(1);
			assertNotNull(row1);
			Iterator<Cell> y = row1.cellIterator();
			int i = 1;
			while (y.hasNext()) {
				y.next();
				i++;
			}
			assertEquals(6, i);
			assertEquals("data1", row1.getCell(0).getStringCellValue());
			assertEquals("data2", row1.getCell(1).getStringCellValue());
			assertEquals("1", row1.getCell(2).getStringCellValue());
			//We don't do dates - this is the ugly default JAVA .toString() format
			assertEquals("Wed Dec 31 18:00:10 CST 1969", row1.getCell(3).getStringCellValue());
			assertNull(row1.getCell(4));
			assertEquals(29382.2398, row1.getCell(5).getNumericCellValue(), 0);

			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void bunchOfRows() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, String> mapping = new HashMap<>();
		mapping.put("A", "colA");
		mapping.put("B", "colB");
		mapping.put("C", "colC");
		mapping.put("D", "colD");
		mapping.put("E", "colE");
		mapping.put("F", "colF");

		XlsxTransformer transformer = new XlsxTransformer(baos, mapping);
		try {
			for (int i = 0; i < 1000; i++) {
				transformer.write(getTestRow());
			}
			transformer.end();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			Workbook wb = new XSSFWorkbook(bais);
			assertNotNull(wb);
			Sheet sheet = wb.getSheet("report");
			assertNotNull(sheet);
			Row row1 = sheet.getRow(0);
			assertNotNull(row1);
			assertEquals("colA", row1.getCell(0).getStringCellValue());

			Row lastRow = sheet.getRow(1000);
			assertNotNull(lastRow);
			assertEquals("1000", lastRow.getCell(2).getStringCellValue());
			assertEquals(1000, sheet.getLastRowNum());

			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	public Map<String, Object> getTestRow() {
		Map<String, Object> record = new LinkedHashMap<String, Object>();
		record.put("A", "data1");
		record.put("B", "data2");
		record.put("C", rowCount++);
		record.put("D", new Date(10000));
		record.put("E", null);
		record.put("F", new BigDecimal(29382.2398));
		record.put("G", "nocando");

		return record;
	}

}
