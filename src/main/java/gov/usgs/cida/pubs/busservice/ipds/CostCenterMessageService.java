package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.utility.PubsEscapeXML10;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
@Deprecated
public class CostCenterMessageService implements IIpdsService {

	private static final Logger LOG = LoggerFactory.getLogger(CostCenterMessageService.class);

	private final IpdsParserService parser;
	private final IpdsWsRequester requester;

	@Autowired
	CostCenterMessageService(final IpdsParserService parser, final IpdsWsRequester requester) {
		this.parser = parser;
		this.requester = requester;
	}

	@Override
	@Transactional
	public void processIpdsMessage(final String nothing) {
		String inMessageText = requester.getIpdsCostCenterXml();
		IpdsMessageLog newMessage = new IpdsMessageLog();
		newMessage.setMessageText(PubsEscapeXML10.ESCAPE_XML10.translate(inMessageText));
		newMessage.setProcessType(ProcessType.COST_CENTER);
		IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

		String processingDetails = processLog(inMessageText);

		msg.setProcessingDetails(processingDetails);
		IpdsMessageLog.getDao().update(msg);
	}

	protected String processLog(String log) {
		StringBuilder rtn = new StringBuilder();
		int totalEntries = 0;
		int additions = 0;
		int errors = 0;

		try {
			Document doc = parser.makeDocument(log);

			NodeList entries = doc.getElementsByTagName("m:properties");
			for (int n=0; n<entries.getLength(); n++) {
				if (entries.item(n).getNodeType() == Node.ELEMENT_NODE) {
					totalEntries++;
					CostCenter costCenter = new CostCenter();
					//This is really all we should ever get..
					Element element = (Element) entries.item(n);
					String ipdsId = parser.getFirstNodeText(element, "d:Id");
					String name = parser.getFirstNodeText(element, "d:Name");
					Map<String, Object> filters = new HashMap<>();
					filters.put(PublicationDao.IPDS_ID, ipdsId);
					List<CostCenter> costCenters = CostCenter.getDao().getByMap(filters);
					//TODO what if we get more than one?
					if (costCenters.isEmpty()) {
						costCenter.setIpdsId(ipdsId);
						costCenter.setText(name);
						CostCenter.getDao().add(costCenter);
						additions++;
					} else {
						costCenter = (CostCenter) costCenters.get(0);
						costCenter.setText(name);
						CostCenter.getDao().update(costCenter);
					}
				}
			}
		} catch (Exception e) {
			String msg = "Trouble getting costCenters: ";
			LOG.info(msg, e);
			rtn.append("\n\t").append(msg).append(e.getMessage());
			errors++;
		}

		String counts = "Summary:\n\tTotal Entries: " + totalEntries + "\n\tCostCenters Added: " + additions + "\n\tErrors Encountered: " + errors + "\n";

		rtn.insert(0, counts);

		return rtn.toString();
	}
}