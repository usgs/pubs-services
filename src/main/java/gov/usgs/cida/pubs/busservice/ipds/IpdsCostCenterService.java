package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.CostCenter;

@Service
public class IpdsCostCenterService {

	private static final Logger LOG = LoggerFactory.getLogger(IpdsCostCenterService.class);

	private final IpdsParserService parser;
	private final IpdsWsRequester requester;
	private final IBusService<CostCenter> costCenterBusService;

	@Autowired
	public IpdsCostCenterService(IpdsParserService parser, IpdsWsRequester requester, @Qualifier("costCenterBusService") IBusService<CostCenter> costCenterBusService) {
		this.parser = parser;
		this.requester = requester;
		this.costCenterBusService = costCenterBusService;
	}

	public CostCenter getCostCenter(final int ipdsId) throws SAXException, IOException {
		CostCenter costCenter = null;
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.IPDS_ID, ipdsId);
		List<CostCenter> costCenters = CostCenter.getDao().getByMap(filters);
		if (costCenters.size() > 1) {
			LOG.warn("Multiple costCenters found for ipdsId: " + ipdsId);
		}
		if (!costCenters.isEmpty()) {
			costCenter = costCenters.get(0);
		}
		return costCenter;
	}

	public CostCenter createCostCenter(final int ipdsId) throws SAXException, IOException {
		CostCenter costCenter = null;
		String costCenterXml = requester.getCostCenter(ipdsId, ipdsId);
		Document doc = parser.makeDocument(costCenterXml);
		costCenter = new CostCenter();
		costCenter.setText(parser.getFirstNodeText(doc.getDocumentElement(), Schema.TITLE));
		costCenter.setIpdsId(ipdsId);
		costCenter = costCenterBusService.createObject(costCenter);
		return costCenter;
	}
}