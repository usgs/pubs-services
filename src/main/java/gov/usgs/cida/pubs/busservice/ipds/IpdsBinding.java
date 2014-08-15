package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Note: when binging author this uses the AuthorsNameText and EditorsNameText names for IPDS 
 * and should be mapped to the same field in MyPubs. Whiles is technically a transform within
 * the binding - it felt like the correct location. Of course we could move this if seen fit.
 * 
 * @author David U
 *
 */
public class IpdsBinding {

    private static final Logger LOG = LoggerFactory.getLogger(IpdsBinding.class);

    @Autowired()
    @Qualifier("personContributorBusService")
    public IBusService<PersonContributor<?>> contributorBusService;

//   @Autowired
    //TODO this should be an affiliation
    public IBusService<Affiliation<?>> affiliationBusService;

    public static final String NAMESPACE  = "http://schemas.microsoft.com/ado/2007/08/dataservices";

    private DocumentBuilder builder;

//    /**
//     * maps IPDS tags to PUBS fields
//     * Map<tag,field>
//     */
//    private final Set<String> tagsOfInterest; // TODO log if not one-to-one before given here

    public IpdsBinding() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        builder = factory.newDocumentBuilder();
    }

    public PublicationMap bindCostCenter(String costCenterXml, Set<String> tagsOfInterest) throws SAXException, IOException {
        PublicationMap costCenter = new PublicationMap();
        bindGeneral(costCenter, costCenterXml, tagsOfInterest);
        return costCenter;
    }

    public PublicationMap bindNotes(String notesXml, Set<String> tagsOfInterest) throws SAXException, IOException {
        PublicationMap notes = new PublicationMap();
        Document doc= makeDocument(notesXml);

        for (String tagName : tagsOfInterest) {
            StringBuilder valueText = new StringBuilder();
            NodeList nodes = doc.getElementsByTagNameNS(NAMESPACE, tagName);
            if (nodes.getLength() < 1) {
                // TODO do we want to soft with no log or hard fail with pLog
                continue;
            }
            for (int i = 0; i < nodes.getLength(); i++) {
                valueText.append(nodes.item(i).getTextContent().trim()).append("|");
            }
            if (0 < valueText.length()) {
                notes.put(tagName, valueText.toString());
            }
        }
        return notes;
    }

    protected void bindGeneral(PublicationMap map, String ipdsXml, Set<String> tagsOfInterest) throws SAXException, IOException {
        Document doc= makeDocument(ipdsXml);

        for (String tagName : tagsOfInterest) {
            NodeList nodes = doc.getElementsByTagNameNS(NAMESPACE, tagName);
            if (nodes.getLength() < 1) {
                // TODO do we want to soft with no log or hard fail with pLog
                continue;
            }
            if (nodes.getLength() > 1) {
                // TODO log too many values but take the first
            }
            String value = nodes.item(0).getTextContent().trim();
            map.put(tagName, value);
        }
    }

    public Collection<MpPublicationContributor> bindContributors(String authorsXml) throws SAXException, IOException {
        Collection<MpPublicationContributor> ipds = new ArrayList<>();
        Document doc = makeDocument(authorsXml);

        NodeList entries = doc.getElementsByTagName("m:properties");
        for (int n=0; n<entries.getLength(); n++) {
            ipds.add(buildPublicationContributor(entries.item(n)));
        }

        //TODO - check rank for dups and clear from the contributorType if we find any within a contributorType.
        return ipds;
    }

    protected MpPublicationContributor buildPublicationContributor(final Node entry) {
        MpPublicationContributor rtn = new MpPublicationContributor();
        LOG.debug("\nCurrent Element :" + entry.getNodeName());

        if (entry.getNodeType() == Node.ELEMENT_NODE) {
            //This is really all we should ever get..
            Element element = (Element) entry;
            Contributor<?> person;
            String ipdsContributorId = element.getElementsByTagName("d:AuthorNameId").item(0).getTextContent();
            if (null == ipdsContributorId) {
                person = getOrCreateNonUsgsContributor(element);
            } else {
                person = getOrCreateUsgsContributor(element, ipdsContributorId);
            }
            rtn.setContributor(person);
            if ("Author".equalsIgnoreCase(PubsUtilities.getNodeText(element, "d:ContentType"))) {
                rtn.setContributorType(ContributorType.getDao().getById(ContributorType.AUTHORS));
            } else {
                rtn.setContributorType(ContributorType.getDao().getById(ContributorType.EDITORS));
            }
            rtn.setRank(PubsUtilities.parseInteger(PubsUtilities.getNodeText(element, "d:Rank")));
        }
        return rtn;
    }

    protected Contributor<?> getOrCreateNonUsgsContributor(final Element element) {
        Contributor<?> person;
        Map<String, Object> filters = new HashMap<>();
        String contributorName = element.getElementsByTagName("d:AuthorNameText").item(0).getTextContent();
        String[] nameParts = contributorName.split(",");
        if (0 < nameParts.length) {
            filters.put("given", nameParts[0].trim());
        }
        if (1 < nameParts.length) {
            filters.put("family", nameParts[1].trim());
        }
        List<Contributor<?>> people = Contributor.getDao().getByMap(filters);
        //TODO what if we get more than one?
        if (0 < people.size()) {
            person = people.get(0);
            //TODO - should we update the information on file?
        } else {
            person = createNonUsgsContributor(element, filters.get("family").toString(), filters.get("given").toString());
        }
        return person;
    }

    protected Contributor<?> createNonUsgsContributor(final Element element, final String family, final String given) {
        OutsideContributor person = new OutsideContributor();
        person.setFamily(family);
        person.setGiven(given);
        person.setAffiliation(getOrCreateNonUsgsAffiliation(element.getElementsByTagName("d:NonUSGSAffiliation").item(0).getTextContent()));
        return contributorBusService.createObject(person);
    }

    protected Affiliation<?> getOrCreateNonUsgsAffiliation(final String name) {
        Affiliation<?> affiliation;
        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        List<Affiliation<?>> affiliations = OutsideAffiliation.getDao().getByMap(filters);
        //TODO what if we get more than one?
        if (0 < affiliations.size()) {
            affiliation = (OutsideAffiliation) affiliations.get(0);
        } else {
            affiliation = createNonUsgsAffiliation(name);
        }
        return affiliation;
    }

    protected Affiliation<?> createNonUsgsAffiliation(final String name) {
        Affiliation<?> affiliation = new OutsideAffiliation();
        affiliation.setName(name);
        return affiliationBusService.createObject(affiliation);
    }

    protected Contributor<?> getOrCreateUsgsContributor(final Element element, final String ipdsContributorId) {
        Contributor<?> person;
        Map<String, Object> filters = new HashMap<>();

        filters.put("ipdsContributorId", ipdsContributorId);
        List<Contributor<?>> people = Contributor.getDao().getByMap(filters);
        //We get at most one hit on the ipdsContributorId, if we don't get any we need to create the contributor.
        if (0 < people.size()) {
            person = people.get(0);
            //TODO - should we update the information on file?
        } else {
            person = createUsgsContributor(element, ipdsContributorId);
        }
        return person;
    }

    protected Contributor<?> createUsgsContributor(final Element element, final String ipdsContributorId) {
        //TODO call ipds for the information
        UsgsContributor person = new UsgsContributor();
//        person.setFamily(filters.get("family").toString());
//        person.setGiven(filters.get("given").toString());
        person.setAffiliation(getOrCreateUsgsAffiliation(element));
        return contributorBusService.createObject(person);
    }

    protected Affiliation<?> getOrCreateUsgsAffiliation(final Element element) {
        Affiliation<?> affiliation;
        Map<String, Object> filters = new HashMap<>();
        filters.put("ipdsId", element.getElementsByTagName("d:CostCenterId").item(0).getTextContent());
        List<Affiliation<?>> affiliations = CostCenter.getDao().getByMap(filters);
        //TODO what if we get more than one?
        if (0 < affiliations.size()) {
            affiliation = (CostCenter) affiliations.get(0);
        } else {
            affiliation = createUsgsAffiliation(filters.get("ipdsId").toString());
        }
        return affiliation;
    }

    protected Affiliation<?> createUsgsAffiliation(final String ipdsId) {
        //TODO call ipds for the information
        Affiliation<?> affiliation = new CostCenter();
        affiliation.setId(ipdsId);
        return affiliationBusService.createObject(affiliation);
    }

    protected Document makeDocument(final String xmlStr) throws SAXException, IOException {
        Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
        return doc;
    }

}
