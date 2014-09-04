package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CrossRefBusService implements ICrossRefBusService {

    private static final Logger LOG = LoggerFactory.getLogger(CrossRefBusService.class);

    public static final String FIRST = "first";
    public static final String ADDITIONAL = "additional";

    protected final String crossRefProtocol;
    protected final String crossRefHost;
    protected final String crossRefUrl;
    protected final Integer crossRefPort;
    protected final String crossRefUser;
    protected final String crossRefPwd;
    protected final String numberedSeriesXml;
    protected final String unNumberedSeriesXml;
    protected final String personNameXml;
    protected final String organizationNameXml;
    protected final String pagesXml;
    protected final PubsEMailer pubsEMailer;

    @Autowired
    public CrossRefBusService(
    		@Qualifier("crossRefProtocol")
    		final String crossRefProtocol,
    		@Qualifier("crossRefHost")
    		final String crossRefHost,
    		@Qualifier("crossRefUrl")
    		final String crossRefUrl,
    		@Qualifier("crossRefPort")
    		final Integer crossRefPort,
    		@Qualifier("crossRefUser")
    		final String crossRefUser,
    		@Qualifier("crossRefPwd")
    		final String crossRefPwd,
    		@Qualifier("numberedSeriesXml")
    		final String numberedSeriesXml,
    		@Qualifier("unNumberedSeriesXml")
    		final String unNumberedSeriesXml,
    		@Qualifier("organizationNameXml")
    		final String organizationNameXml,
    		@Qualifier("personNameXml")
    		final String personNameXml,
    		@Qualifier("pagesXml")
    		final String pagesXml,
    		final PubsEMailer pubsEMailer) {
    	this.crossRefProtocol = crossRefProtocol;
    	this.crossRefHost = crossRefHost;
    	this.crossRefUrl = crossRefUrl;
    	this.crossRefPort = crossRefPort;
    	this.crossRefUser = crossRefUser;
    	this.crossRefPwd = crossRefPwd;
    	this.numberedSeriesXml = numberedSeriesXml;
    	this.unNumberedSeriesXml = unNumberedSeriesXml;
    	this.organizationNameXml = organizationNameXml;
    	this.personNameXml = personNameXml;
    	this.pagesXml = pagesXml;
    	this.pubsEMailer = pubsEMailer;
    }

    @Override
    public void submitCrossRef(final MpPublication mpPublication) {
        String indexPage = getIndexPage(mpPublication);
        if (null != indexPage && 0 < indexPage.length()) {
            LOG.debug("Posting to http://"+ crossRefHost + ":" + crossRefPort);

            StringBuilder url = new StringBuilder(crossRefUrl).append("?operation=doMDUpload&login_id=")
                    .append(crossRefUser).append("&login_passwd=").append(crossRefPwd).append("&area=live");

            HttpResponse rtn = null;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url.toString());
            HttpHost httpHost = new HttpHost(crossRefHost, crossRefPort, crossRefProtocol);

            String fileName = buildXml(mpPublication, indexPage);

            try {
                FileBody file = new FileBody(new File(fileName), ContentType.TEXT_XML, mpPublication.getIndexId() + ".xml");
                HttpEntity httpEntity = MultipartEntityBuilder.create()
                        .addPart("fname", file)
                        .build();
                httpPost.setEntity(httpEntity);
                rtn = httpClient.execute(httpHost, httpPost, new BasicHttpContext());
            } catch (Exception e) {
                e.printStackTrace();
                pubsEMailer.sendMail("Unexpected error in POST to crossref", e.getMessage());
            }

            if (HttpStatus.SC_OK != rtn.getStatusLine().getStatusCode()) {
                LOG.info("not cool" + rtn.getStatusLine().getStatusCode());
                pubsEMailer.sendMail("Unexpected error in POST to crossref", rtn.getStatusLine().toString());
            }
        }
    }

    protected String buildXml(final MpPublication pub, final String indexPage) {
        File temp = null;
        if (null == pub || null == indexPage || null == pub.getIndexId()) {
        	return null;
        }
        String xml = null;
        if (PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype())) {
            xml = buildBaseXml(pub, indexPage, numberedSeriesXml);
        } else {
            xml = buildBaseXml(pub, indexPage, unNumberedSeriesXml);
        }
        String batchId = xml.substring(xml.indexOf("<doi_batch_id>") + 14, xml.indexOf("</doi_batch_id>"));
        CrossRefLog log = new CrossRefLog(batchId, pub.getId(), xml);
        CrossRefLog.getDao().add(log);
        try {
            temp = File.createTempFile(pub.getIndexId(), ".xml");
            LOG.debug("TEMP FILE IS:" + temp.getAbsolutePath());
            temp.deleteOnExit();
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(xml);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            pubsEMailer.sendMail("Unexpected error in building xml for crossref", e.getMessage());
        }
        return temp.getAbsolutePath();
    }

    protected String buildBaseXml(final MpPublication pub, final String indexPage, final String xml) {
    	if (null == pub || null == indexPage || null == xml) {
    		return "";
    	} else {
	        String rtn = xml;
	        rtn = replacePlaceHolder(rtn, "{doi_batch_id}", getBatchId());
	        rtn = replacePlaceHolder(rtn, "{submission_timestamp}", String.valueOf(new Date().getTime()));
	        //TODO new dissemination date logic
	//        if (null != pub.getPublicationMonth() && 0 < pub.getPublicationMonth().length()) {
	//            rtn = rtn.replace("{dissemination_month}", "<month>" + pub.getPublicationMonth() + "</month>"); 
	//        } else {
	            rtn = replacePlaceHolder(rtn, "{dissemination_month}", ""); 
	//        }
	//        if (null != pub.getPublicationDay() && 0 < pub.getPublicationDay().length()) {
	//            rtn = rtn.replace("{dissemination_day}", "<day>" + pub.getPublicationDay() + "</day>");
	//        } else {
	            rtn = replacePlaceHolder(rtn, "{dissemination_day}", "");
	//        }
	        rtn = replacePlaceHolder(rtn, "{dissemination_year}", pub.getPublicationYear());
	        rtn = replacePlaceHolder(rtn, "{contributers}", getContributors(pub));
	        rtn = replacePlaceHolder(rtn, "{title}", pub.getTitle());
	        rtn = replacePlaceHolder(rtn, "{pages}", getPages(pub));
	        rtn = replacePlaceHolder(rtn, "{doi_name}", pub.getDoi());
	        rtn = replacePlaceHolder(rtn, "{index_page}", indexPage);
	        if (null != pub.getSeriesTitle()) {
	        	if (null != pub.getSeriesTitle().getName()) {
	        		rtn = replacePlaceHolder(rtn, "{series_name}", pub.getSeriesTitle().getName());
	        	} else {
	        		rtn = replacePlaceHolder(rtn, "{series_name}", "");
	        	}
	        	if (null != pub.getSeriesTitle().getName()) {
	        		rtn = replacePlaceHolder(rtn, "{online_issn}", pub.getSeriesTitle().getOnlineIssn());
	        	} else {
	        		rtn = replacePlaceHolder(rtn, "{online_issn}", "");
	        	}
	        } else {
	        	rtn = replacePlaceHolder(rtn, "{series_name}", "");
	        	rtn = replacePlaceHolder(rtn, "{online_issn}", "");
	        }
            rtn = replacePlaceHolder(rtn, "{series_number}", pub.getSeriesNumber());
	        return rtn;
    	}
    }

    protected String replacePlaceHolder(String rawString, String placeHolder, String replaceWith) {
    	if (null == rawString) {
    		return "";
    	}
        if (null == placeHolder || -1 == rawString.indexOf(placeHolder)) {
        	return rawString;
        } else {
        	if (null == replaceWith) {
        		return rawString.replace(placeHolder, "");
        	} else {
        		return rawString.replace(placeHolder, replaceWith);
        	}
        }
    }

    protected String getBatchId() {
        return String.valueOf(new Date().getTime());
    }

    protected String getContributors(MpPublication pub) {
        StringBuilder rtn = new StringBuilder("");
        //This process requires that the contributors are in rank order.
        //And that the contributor is valid.
        if (null != pub) {
	        String sequence = FIRST;
	        Collection<PublicationContributor<?>> authors = pub.getAuthors();
	        if (null != authors && !authors.isEmpty()) {
	            for (PublicationContributor<?> author : authors) {
	            	if (author.getContributor() instanceof PersonContributor) {
	            		rtn.append(processPerson(author, sequence));
	            	} else {
	            		rtn.append(processCorporation(author, sequence));
	            	}
	            	sequence = ADDITIONAL;
	            	rtn.append("\n");
	            }
	        }
	
	        Collection<PublicationContributor<?>> editors = pub.getEditors();
	        if (null != editors && !editors.isEmpty()) {
	            for (PublicationContributor<?> editor : editors) {
	            	if (editor.getContributor() instanceof PersonContributor) {
	            		rtn.append(processPerson(editor, sequence));
	            	} else {
	            		rtn.append(processCorporation(editor, sequence));
	            	}
	            	sequence = ADDITIONAL;
	            	rtn.append("\n");
	            }
	        }
        }
        return rtn.toString();
    }

    protected String processPerson(PublicationContributor<?> pubContributor, String sequence) {
    	PersonContributor<?> contributor = (PersonContributor<?>) pubContributor.getContributor();
    	String template = personNameXml;
		template = template.replace("{sequence}", sequence);
		template = template.replace("{contributor_type}", getContributorType(pubContributor));
    	if (StringUtils.isNotEmpty(contributor.getFamily())) {
    		template = template.replace("{surname}", contributor.getFamily());
    	} else {
    		template = template.replace("{surname}", "");
    	}
    	if (StringUtils.isNotEmpty(contributor.getGiven())) {
    		template = template.replace("{given_name}", "<given_name>" + contributor.getGiven() + "</given_name>");
    	} else {
    		template = template.replace("{given_name}", "");
    	}
    	if (StringUtils.isNotEmpty(contributor.getSuffix())) {
    		template = template.replace("{suffix}", "<suffix>" + contributor.getSuffix() + "</suffix>");
    	} else {
    		template = template.replace("{suffix}", "");
    	}
    	return template;
    }

    protected String processCorporation(PublicationContributor<?> pubContributor, String sequence) {
    	CorporateContributor contributor = (CorporateContributor) pubContributor.getContributor();
    	String template = organizationNameXml;
		template = template.replace("{sequence}", sequence);
		template = template.replace("{contributor_type}", getContributorType(pubContributor));
    	if (StringUtils.isNotEmpty(contributor.getOrganization())) {
    		template = template.replace("{organization}", contributor.getOrganization());
    	} else {
    		template = template.replace("{organization}", "");
    	}
    	return template;
    }

    protected String getContributorType(PublicationContributor<?> pubContributor) {
    	if (null != pubContributor && null != pubContributor.getContributorType()
    			&& StringUtils.isNotEmpty(pubContributor.getContributorType().getText())) {
    		return pubContributor.getContributorType().getText().toLowerCase().replaceAll("s$", "");
    	} else {
    		return "";
    	}
    }

    protected String getPages(MpPublication pub) {
        String rtn = "";
        if (null != pub && StringUtils.isNoneEmpty(pub.getStartPage())
                && StringUtils.isNotEmpty(pub.getEndPage())) {
            rtn = pagesXml.replace("{start_page}", pub.getStartPage().trim()).replace("{end_page}", pub.getEndPage().trim());
        }
        return rtn;
    }

    protected String getIndexPage(MpPublication pub) {
        String rtn = "";
        if (null != pub && null != pub.getLinks()) {
        	Collection<PublicationLink<?>> links = pub.getLinks();
        	for (Iterator<PublicationLink<?>> linksIter = links.iterator(); linksIter.hasNext();) {
	        	PublicationLink<?> link = linksIter.next();
	        	if (null != link.getLinkType() && LinkType.INDEX_PAGE.equals(link.getLinkType().getId())) {
	        		rtn = link.getUrl();
	        	}
        	}
        }
        return rtn;
    }

}
