package gov.usgs.cida.pubs.webservice;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.usgs.cida.pubs.BaseIT;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class SpringDocIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldDisplaySwaggerUiPage() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk())
				.andReturn();
		String contentAsString = rtn.getResponse().getContentAsString();
		assertTrue(contentAsString.contains("Swagger UI"));
	}

	@Test
	public void shouldGetPublicApi() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/v3/api-docs/public"))
				.andExpect(status().isOk())
				.andReturn();
		String contentAsString = rtn.getResponse().getContentAsString();
		assertTrue(contentAsString.contains("USGS Publications Warehouse Web Services API"));
	}
}
