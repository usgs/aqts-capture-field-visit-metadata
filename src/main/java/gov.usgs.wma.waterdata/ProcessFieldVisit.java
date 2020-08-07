package gov.usgs.wma.waterdata;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessFieldVisit implements Function<RequestObject, ResultObject> {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessFieldVisit.class);

	private FieldVisitDao fieldVisitDao;

	@Autowired
	public ProcessFieldVisit(FieldVisitDao fieldVisitDao) {
		this.fieldVisitDao = fieldVisitDao;
	}

	@Override
	@Transactional
	public ResultObject apply(RequestObject request) {
		return processRequest(request);
	}

	@Transactional
	protected ResultObject processRequest(RequestObject request) {
		Long jsonDataId = request.getId();
		LOG.debug("json_data_id: {}", jsonDataId);
		ResultObject result = new ResultObject();

		fieldVisitDao.doReadings(request);
		fieldVisitDao.doDatumConvertedValues(request);

		result.setId(jsonDataId);

		return result;
	}
}
