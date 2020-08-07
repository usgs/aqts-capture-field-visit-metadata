package gov.usgs.wma.waterdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@SpringBootTest(
		webEnvironment=WebEnvironment.NONE,
		classes={DBTestConfig.class, FieldVisitDao.class, ProcessFieldVisit.class})
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		TransactionDbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader=FileSensingDataSetLoader.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional(propagation=Propagation.NOT_SUPPORTED)
@Import({DBTestConfig.class})
@DirtiesContext
public class ProcessFieldVisitIT {

	@Autowired
	private ProcessFieldVisit processFieldVisit;

	@DatabaseSetup("classpath:/testData/jsonData/")
	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value="classpath:/testResult/datumConvertedValues/", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(value="classpath:/testResult/fieldVisitReadingsByLoc/", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void fullTest() {
		RequestObject request = new RequestObject();
		request.setId(FieldVisitDaoIT.JSON_DATA_ID_1);
		request.setPartitionNumber(FieldVisitDaoIT.PARTITION_NUMBER);
		ResultObject result = processFieldVisit.apply(request);
		assertNotNull(result);
		assertEquals(1, result.getId());
	}

	@DatabaseSetup("classpath:/testData/jsonData/")
	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value="classpath:/testData/cleanseOutput/", assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void badIdTest() {
		RequestObject request = new RequestObject();
		request.setId(FieldVisitDaoIT.JSON_DATA_ID_4);
		request.setPartitionNumber(FieldVisitDaoIT.PARTITION_NUMBER);
		processFieldVisit.apply(request);

		request.setId(FieldVisitDaoIT.JSON_DATA_ID_3);
		processFieldVisit.apply(request);
	}
}
