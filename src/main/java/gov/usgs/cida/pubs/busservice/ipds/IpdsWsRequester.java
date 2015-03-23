package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsProcessLog;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.utility.PubsEscapeXML10;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IpdsWsRequester {

    private static final Logger LOG = LoggerFactory.getLogger(IpdsWsRequester.class);

    public static final String URL_PREFIX = "/_vti_bin/listdata.svc/";
    public static final String DOI_XML_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                    "<DigitalObjectIdentifier xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" " +
                    "xmlns=\"http://schemas.microsoft.com/ado/2007/08/dataservices\"";
    public static final String DOI_XML_SUFFIX = "</DigitalObjectIdentifier>";
    public static final String NULL_DOI = " m:null=\"true\"/>";
    private static final String IPDS_PROTOCOL = "https";
    private static final int IPDS_PORT = 443;

    private final String ipdsEndpoint;
    private final NTCredentials credentials;
    private final PubsEMailer pubsEMailer;

    private BasicHttpContext httpContext;

    @Autowired
    public IpdsWsRequester(final String ipdsEndpoint,
            final NTCredentials credentials,
            final PubsEMailer pubsEMailer) {
        this.ipdsEndpoint = ipdsEndpoint;
        this.credentials = credentials;
        this.pubsEMailer = pubsEMailer;
    }

    public String getIpdsProductXml(final String asOf) {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("InformationProductsArchive()?$filter=Modified+ge+datetime'")
        .append(asOf)
        .append("'");

        return getIpdsXml(url.toString(), null);
    }

    protected String getContributors(final String ipds) {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("IPDSAuthors()?$filter=startswith(IPNumber,'")
        .append(ipds).append("')");

        return getIpdsXml(url.toString(), ipds);
    }

    protected String getContributor(final String ipds) {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("UserInformationList(")
        .append(ipds).append(")");

        return getIpdsXml(url.toString(), ipds);
    }


    protected String getCostCenter(final String costCenterId, final String ipds) {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("CostCenters(").append(costCenterId).append(")");

        return getIpdsXml(url.toString(), ipds);
    }


    protected String getNotes(final String ipds) {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("IPDSNotes()?$filter=startswith(IPNumber,'")
        .append(ipds).append("')");

        return getIpdsXml(url.toString(), ipds);
    }

    public String getSpnProduction(final String asOf) {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("InformationProduct()?$filter=(((IPDSReviewProcessStateValue%20eq%20'")
        .append(ProcessType.SPN_PRODUCTION.getIpdsValueEncoded()).append("')")
        .append("%20and%20(ProductTypeValue%20eq%20'USGS%20Series'))%20and%20(DigitalObjectIdentifier%20eq%20null))")
        .append("%20and%20(Modified+ge+datetime'")
        .append(asOf)
        .append("')");
        return getIpdsXml(url.toString(), null);
    }

    protected String getIpdsXml(final String url, final String ipdsId)  {
        String xml = null;

        HttpResponse response = doGet(url);
        try {
            HttpEntity entity = response.getEntity();
            xml = EntityUtils.toString(entity);
            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
            LOG.info(e.getMessage());
            pubsEMailer.sendMail("Unexpected error in mypubsJMS.getIpdsXml", e.getMessage());
        }

        LOG.debug(xml);

        if (null != ipdsId) {
            IpdsProcessLog log = new IpdsProcessLog();
            log.setIpdsNumber(ipdsId);
            log.setMessage(PubsEscapeXML10.ESCAPE_XML10.translate(xml));
            log.setUri(url);
            IpdsProcessLog.getDao().add(log);
        }
        return xml;
    }

    protected HttpResponse doGet(final String url) {
        HttpResponse rtn = null;
        HttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            rtn = httpClient.execute(getHttpHost(), httpGet, httpContext);
        } catch (Exception e) {
            LOG.info(e.getMessage());
            pubsEMailer.sendMail("Unexpected error in mypubsJMS.doGet", e.getMessage());
        }

        return rtn;
    }

    protected HttpClient getHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        ((HttpClientBuilder) httpClientBuilder).setDefaultCredentialsProvider(credentialsProvider);
        return (HttpClient) httpClientBuilder.build();
    }

    protected HttpHost getHttpHost() {
        return new HttpHost(ipdsEndpoint, IPDS_PORT, IPDS_PROTOCOL);
    }

    protected String updateIpdsDoi(MpPublication inPub) {
        StringBuilder rtn = new StringBuilder("");
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("InformationProduct(")
        .append(inPub.getIpdsInternalId()).append(")/DigitalObjectIdentifier");

        HttpResponse getResponse = doGet(url.toString());
        if (null != getResponse && null != getResponse.getStatusLine()) {
            int statusCode = getResponse.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {

                if (getResponse.containsHeader("Etag")) {
                    StringBuilder content = new StringBuilder(DOI_XML_PREFIX);
                    if (null == inPub.getDoi()) {
                        content.append(NULL_DOI);
                    } else {
                        content.append(">").append(inPub.getDoi()).append(DOI_XML_SUFFIX);
                    }

                    String etag = getResponse.getFirstHeader("Etag").getValue();
                    try {
                        EntityUtils.consume(getResponse.getEntity());
                    } catch (IOException e) {
                        LOG.info(e.getMessage());
                        rtn.append("\n\tERROR: ").append(e.getMessage());
                    }
                    try {
                        HttpEntity httpEntity = new StringEntity(content.toString());

                        HttpPut httpPut = new HttpPut(url.toString());
                        httpPut.addHeader("If-Match", etag);
                        httpPut.addHeader("Content-Type", "text/xml");
                        httpPut.setEntity(httpEntity);
                        HttpClient httpclient = getHttpClient();

                        HttpResponse response = httpclient.execute(getHttpHost(), httpPut, new BasicHttpContext());

                        if (null != response && null != response.getStatusLine() && response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                            rtn.append("\n\tDOI updated in IPDS: ").append(inPub.getDoi());
                        } else {
                            rtn.append("\n\tERROR: Bad Response from httpPut: ").append(url.toString()).append(": ") 
                                    .append((null == response ? "No Response" : ("Status Code : " + response.getStatusLine().getStatusCode())));
                        }
                        try {
                            if (null != response) {
                                EntityUtils.consume(response.getEntity());
                            }
                        } catch (IOException e) {
                            LOG.info(e.getMessage());
                            rtn.append("\n\tERROR: ").append(e.getMessage());
                        }
                    } catch (Exception e) {
                        LOG.info(e.getMessage());
                        rtn.append("\n\tERROR: ").append(e.getMessage());
                    }
                } else {
                    rtn.append("\n\tERROR: Unable to get Etag for:").append(url.toString()).append(" - Missing Etag header.");
                }
            } else {
                rtn.append("\n\tERROR: Unable to get Etag for:").append(url.toString()).append(" - Status Code:").append(statusCode);
            }
        } else {
            rtn.append("\n\tERROR: Unable to get Etag for:").append(url.toString()).append(" - null response or status line.");
        }
        try {
            if (null != getResponse) {
                EntityUtils.consume(getResponse.getEntity());
            }
        } catch (IOException e) {
            LOG.info(e.getMessage());
            rtn.append("\n\tERROR: ").append(e.getMessage());
        }
        return rtn.toString();
    }

	public String getIpdsCostCenterXml() {
        StringBuilder url = new StringBuilder(URL_PREFIX)
        .append("CostCenters()");

        return getIpdsXml(url.toString(), null);
	}

}
