package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CrossRefBusService implements ICrossRefBusService {

    private static final Logger LOG = LoggerFactory.getLogger(CrossRefBusService.class);

    private static final String FIRST = "first";
    private static final String ADDITIONAL = "additional";

    protected final String crossRefProtocol;
    protected final String crossRefHost;
    protected final String crossRefUrl;
    protected final Integer crossRefPort;
    protected final String crossRefUser;
    protected final String crossRefPwd;
    protected final String numberedSeriesXml;
    protected final String unNumberedSeriesXml;
    protected final String personNameXml;
    protected final String pagesXml;
    protected final PubsEMailer pubsEMailer;

    @Autowired
    public CrossRefBusService(final String crossRefProtocol, final String crossRefHost,
    		final String crossRefUrl, final Integer crossRefPort, final String crossRefUser,
    		final String crossRefPwd, final String numberedSeriesXml, final String unNumberedSeriesXml,
    		final String personNameXml, final String pagesXml, final PubsEMailer pubsEMailer) {
    	this.crossRefProtocol = crossRefProtocol;
    	this.crossRefHost = crossRefHost;
    	this.crossRefUrl = crossRefUrl;
    	this.crossRefPort = crossRefPort;
    	this.crossRefUser = crossRefUser;
    	this.crossRefPwd = crossRefPwd;
    	this.numberedSeriesXml = numberedSeriesXml;
    	this.unNumberedSeriesXml = unNumberedSeriesXml;
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
        String xml = null;
        if (PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype())) {
            xml = buildBaseXml(pub, indexPage, numberedSeriesXml);
        } else {
            xml = buildBaseXml(pub, indexPage, unNumberedSeriesXml);
        }
        String batchId = xml.substring(xml.indexOf("<doi_batch_id>") + 14, xml.indexOf("</doi_batch_id>"));
        CrossRefLog log = new CrossRefLog(batchId, pub.getId(), xml);
        CrossRefLog.getDao().add(log);
        File temp = null;
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
        String rtn = xml;
        rtn = rtn.replace("{doi_batch_id}", getBatchId());
        rtn = rtn.replace("{submission_timestamp}", String.valueOf(new Date().getTime()));
        //TODO new dissemination date logic
//        if (null != pub.getPublicationMonth() && 0 < pub.getPublicationMonth().length()) {
//            rtn = rtn.replace("{dissemination_month}", "<month>" + pub.getPublicationMonth() + "</month>"); 
//        } else {
            rtn = rtn.replace("{dissemination_month}", ""); 
//        }
//        if (null != pub.getPublicationDay() && 0 < pub.getPublicationDay().length()) {
//            rtn = rtn.replace("{dissemination_day}", "<day>" + pub.getPublicationDay() + "</day>");
//        } else {
//            rtn = rtn.replace("{dissemination_day}", "");
//        }
        rtn = rtn.replace("{dissemination_year}", pub.getPublicationYear());
        rtn = rtn.replace("{contributers}", getContributors(pub));
        rtn = rtn.replace("{title}", pub.getTitle());
        rtn = rtn.replace("{pages}", getPages(pub));
        rtn = rtn.replace("{doi_name}", pub.getDoi());
        rtn = rtn.replace("{index_page}", indexPage);
        //TODO Verify this works as expected. (At least no NPE)
        PublicationSeries series = PublicationSeries.getDao().getById(pub.getSeriesTitle().getId());
        rtn = rtn.replace("{series_name}", series.getName());
        if (-1 != rtn.indexOf("{online_issn}")) {
            rtn = rtn.replace("{online_issn}", series.getOnlineIssn());
        }
        if (-1 != rtn.indexOf("{series_number}")) {
            rtn = rtn.replace("{series_number}", pub.getSeriesNumber());
        }
        return rtn;
    }

    protected String getBatchId() {
        return String.valueOf(new Date().getTime());
    }

    protected String getContributors(MpPublication pub) {
        StringBuilder rtn = new StringBuilder("");
        String sequence = "first";
        Collection<PublicationContributor<?>> authors = pub.getAuthors();
        if (!authors.isEmpty()) {
            for (PublicationContributor<?> author : authors) {
            	if (author.getContributor() instanceof PersonContributor) {
            		rtn.append(processPerson(author, sequence));
            	} else {
            		rtn.append(processCorporation(author, sequence));
            	}
            	sequence = "additional";
            	rtn.append("\n");
            }
        }

        Collection<PublicationContributor<?>> editors = pub.getEditors();
        if (!editors.isEmpty()) {
            for (PublicationContributor<?> editor : editors) {
            	if (editor.getContributor() instanceof PersonContributor) {
            		rtn.append(processPerson(editor, sequence));
            	} else {
            		rtn.append(processCorporation(editor, sequence));
            	}
            	sequence = "additional";
            	rtn.append("\n");
            }
        }
        return rtn.toString();
    }

    protected String processPerson(PublicationContributor<?> contributor, String sequence) {
    	String template = personNameXml;
    	if (firstContributor) {
    		template = template.replace("{sequence}", "first");
    	} else {
    		template = template.replace("{sequence}", "additional");
    	}
    	String[] parts = author.split(",");
    	if (0 < parts.length) {
    		template = template.replace("{surname}", parts[0].trim());
    	} else {
    		template = template.replace("{surname}", "");
    	}
    	if (1 < parts.length) {
    		template = template.replace("{given_name}", "<given_name>" + parts[1].trim() + "</given_name>");
    	} else {
    		template = template.replace("{given_name}", "");
    	}
    	if (2 < parts.length) {
    		template = template.replace("{suffix}", "<suffix>" + parts[2].trim() + "</suffix>");
    	} else {
    		template = template.replace("{suffix}", "");
    	}
    	return template;
    }

    protected String getPages(MpPublication pub) {
        String rtn = "";
        if (null != pub.getStartPage() && 0 < pub.getStartPage().length() 
                && null != pub.getEndPage() && 0 < pub.getEndPage().length()) {
            rtn = pagesXml.replace("{start_page}", pub.getStartPage().trim()).replace("{end_page}", pub.getEndPage().trim());
        }
        return rtn;
    }

    protected String getIndexPage(MpPublication pub) {
        String rtn = "";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("publicationId", pub.getId());
        params.put("linkType", LinkType.INDEX_PAGE);
        List<MpPublicationLink> links = MpPublicationLink.getDao().getByMap(params);
        if (!links.isEmpty()) {
            //Should only be one...
            rtn = links.get(0).getUrl();
        }
        return rtn;
    }

//    public void setCrossRefProtocol(String crossRefProtocol) {
//        this.crossRefProtocol = crossRefProtocol;
//    }
//
//    public void setCrossRefHost(String crossRefHost) {
//        this.crossRefHost = crossRefHost;
//    }
//
//    public void setCrossRefUrl(String crossRefUrl) {
//        this.crossRefUrl = crossRefUrl;
//    }
//
//    public void setCrossRefPort(Integer crossRefPort) {
//        this.crossRefPort = crossRefPort;
//    }
//
//    public void setCrossRefUser(String crossRefUser) {
//        this.crossRefUser = crossRefUser;
//    }
//
//    public void setCrossRefPwd(String crossRefPwd) {
//        this.crossRefPwd = crossRefPwd;
//    }
//
//    public void setNumberedSeriesXml(String numberedSeriesXml) {
//        this.numberedSeriesXml = numberedSeriesXml;
//    }
//
//    public void setUnNumberedSeriesXml(String unNumberedSeriesXml) {
//        this.unNumberedSeriesXml = unNumberedSeriesXml;
//    }
//
//    public void setPersonNameXml(String personNameXml) {
//        this.personNameXml = personNameXml;
//    }
//
//    public void setPagesXml(String pagesXml) {
//        this.pagesXml = pagesXml;
//    }
//    public void setPubsEMailer(PubsEMailer pubsEMailer) {
//        this.pubsEMailer = pubsEMailer;
//    }

}
