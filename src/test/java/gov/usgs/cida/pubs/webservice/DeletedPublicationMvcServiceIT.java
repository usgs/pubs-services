package gov.usgs.cida.pubs.webservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/deletedPublication.xml")
public class DeletedPublicationMvcServiceIT extends BaseIT {

	@Test
	public void getAllNoPaging(@Autowired TestRestTemplate restTemplate) throws Exception {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/publication/deleted", String.class);
		assertEquals(HttpStatus.OK, rtn.getStatusCode());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, rtn.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));

		assertThat(new JSONObject(rtn.getBody()),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allWithoutPaging.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getAllPageOne(@Autowired TestRestTemplate restTemplate) throws Exception {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/publication/deleted?page_number=1&page_size=2", String.class);
		assertEquals(HttpStatus.OK, rtn.getStatusCode());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, rtn.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));

		assertThat(new JSONObject(rtn.getBody()),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allPage1.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getAllPageThree(@Autowired TestRestTemplate restTemplate) throws Exception {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/publication/deleted?page_number=3&page_size=2", String.class);
		assertEquals(HttpStatus.OK, rtn.getStatusCode());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, rtn.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));

		assertThat(new JSONObject(rtn.getBody()),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/allPage3.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSinceNoPaging(@Autowired TestRestTemplate restTemplate) throws Exception {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/publication/deleted?deletedSince=2017-12-31", String.class);
		assertEquals(HttpStatus.OK, rtn.getStatusCode());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, rtn.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));

		assertThat(new JSONObject(rtn.getBody()),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sinceWithoutPaging.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSincePageOne(@Autowired TestRestTemplate restTemplate) throws Exception {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/publication/deleted?deletedSince=2017-12-31&page_number=1&page_size=1", String.class);
		assertEquals(HttpStatus.OK, rtn.getStatusCode());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, rtn.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));

		assertThat(new JSONObject(rtn.getBody()),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sincePage1.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getSincePageThree(@Autowired TestRestTemplate restTemplate) throws Exception {
		ResponseEntity<String> rtn = restTemplate.getForEntity("/publication/deleted?deletedSince=2017-12-31&page_number=3&page_size=1", String.class);
		assertEquals(HttpStatus.OK, rtn.getStatusCode());
		assertEquals(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE, rtn.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));

		assertThat(new JSONObject(rtn.getBody()),
				sameJSONObjectAs(new JSONObject(getFile("testResult/deletedPublication/sincePage3.json"))).allowingAnyArrayOrdering());
	}
}
