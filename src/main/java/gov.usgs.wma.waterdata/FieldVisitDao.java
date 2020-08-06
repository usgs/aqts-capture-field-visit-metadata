package gov.usgs.wma.waterdata;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

@Component
public class FieldVisitDao {
	private static final Logger LOG = LoggerFactory.getLogger(FieldVisitDao.class);

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Value("classpath:sql/datumConvertedValues.sql")
	private Resource datumConvertedValues;

	@Value("classpath:sql/readings.sql")
	private Resource readings;

	@Transactional
	public void doDatumConvertedValues(RequestObject request) {
		doUpdate(request, datumConvertedValues);
	}

	@Transactional
	public void doReadings(RequestObject request) {
		doUpdate(request, readings);
	}

	@Transactional
	protected void doUpdate(RequestObject request, Resource resource) {
		try {
			jdbcTemplate.update(getSql(resource), request.getId(), request.getPartitionNumber());
		} catch (DataAccessException e) {
			LOG.error(
					"Error executing SQL for request:  " + request,
					e);
			throw new RuntimeException(e);
		}
	}

	protected String getSql(Resource resource) {
		String sql = null;
		try {
			sql = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
		} catch (IOException e) {
			LOG.error("Unable to get SQL statement", e);
			throw new RuntimeException(e);
		}
		return sql;
	}
}
