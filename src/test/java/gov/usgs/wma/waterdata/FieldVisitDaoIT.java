package gov.usgs.wma.waterdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileUrlResource;
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

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { DBTestConfig.class, FieldVisitDao.class })
@DatabaseSetup("classpath:/testData/jsonData/")
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, TransactionDbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader = FileSensingDataSetLoader.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({ DBTestConfig.class })
@DirtiesContext
public class FieldVisitDaoIT {

	@Autowired
	private FieldVisitDao fieldVisitDao;
	private RequestObject request;

	public static final Long JSON_DATA_ID_1 = 1l;
	public static final Long JSON_DATA_ID_3 = 3l;
	public static final Long JSON_DATA_ID_4 = 4l;
	public static final Integer PARTITION_NUMBER = 7;

	@BeforeEach
	public void beforeEach() {
		request = new RequestObject();
		request.setId(JSON_DATA_ID_1);
		request.setPartitionNumber(PARTITION_NUMBER);
	}

	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value = "classpath:/testResult/datumConvertedValues/", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void doDatumConvertedValuesTest() {
		assertDoesNotThrow(() -> {
			fieldVisitDao.doDatumConvertedValues(request);
		}, "should not have thrown an exception but did");
	}

	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value = "classpath:/testData/cleanseOutput/", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void doDatumConvertedValuesBadIdTest() {
		request.setId(JSON_DATA_ID_3);
		assertDoesNotThrow(() -> {
			fieldVisitDao.doDatumConvertedValues(request);
		}, "should not have thrown an exception but did");
	}

	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value = "classpath:/testResult/fieldVisitReadingsByLoc/", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void doReadingsTest() {
		assertDoesNotThrow(() -> {
			fieldVisitDao.doReadings(request);
		}, "should not have thrown an exception but did");
	}

	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value = "classpath:/testData/cleanseOutput/", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void doReadingsBadIdTest() {
		request.setId(JSON_DATA_ID_3);
		assertDoesNotThrow(() -> {
			fieldVisitDao.doReadings(request);
		}, "should not have thrown an exception but did");
	}

	@DatabaseSetup("classpath:/testData/cleanseOutput/")
	@ExpectedDatabase(value = "classpath:/testData/cleanseOutput/", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@Test
	public void doDatumConvertedValuesNoIdTest() {
		request.setId(JSON_DATA_ID_4);
		assertDoesNotThrow(() -> {
			fieldVisitDao.doDatumConvertedValues(request);
		}, "should not have thrown an exception but did");
	}

	@Test
	public void badResource() {
		Exception e = assertThrows(RuntimeException.class, () -> {
			fieldVisitDao.getSql(new FileUrlResource("classpath:sql/missing.sql"));
		}, "should have thrown a RuntimeException but did not");
		assertNotNull(e.getMessage());
		assertTrue(e.getMessage().startsWith("java.io.FileNotFoundException:"));
		assertTrue(e.getMessage().contains("missing.sql"));
	}

}
