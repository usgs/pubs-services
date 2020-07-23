package gov.usgs.cida.pubs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, AffiliationDao.class})
public class UsernameLengthIT extends BaseIT {

	@Test
	@DatabaseSetup("classpath:/testData/usernameLength/")
	@ExpectedDatabase(
			value="classpath:/testData/usernameLength/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED
			)
	public void checkUsernameLength() {
		//Letting dbunit do all the work.
		//ipds_message_log, ipds_process_log, and pw_publication_store are obsolete and not being tested.
		//The following tables are only populated via liquibase and not included.
		//	To do so would pollute the database.
		//		contributor_type
		//		link_file_type
		//		link_type
		//		mp_list
		//		publishing_service_center
		//The following tables are also populated via liquibase, but also modified in the tests.
		//	This is probably a bad practice, but does make the tests stable if the tsv was to change...
		//		ipds_pubs_type_conv
		//		publication_subtype
		//		publication_type
		//pubs.audit_trail is obsolete and only contains historical information. pubs_audit.pubs_audit_trail
		//	is the current audit table and uses a text column for the username values.
	}

}
