package gov.usgs.cida.pubs.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;

@Component
public class CrossRefLog extends BaseDomain<CrossRefLog> {

    private static IDao<CrossRefLog> crossRefLogDao;

    private String batchId;
    private String prodId;
    private String crossrefXml;

    public CrossRefLog() {
    }

    public CrossRefLog(final String inBatchId, final Integer inProdId, final String inCrossref) {
        batchId = inBatchId;
        prodId = String.valueOf(inProdId);
        crossrefXml = inCrossref;
    }

    public String getBatchId() {
        return batchId;
    }
    public void setBatchId(final String inBatchId) {
        batchId = inBatchId;
    }

    public String getProdId() {
        return prodId;
    }
    public void setProdId(final String inProdId) {
        prodId = inProdId;
    }

    public String getCrossrefXml() {
        return crossrefXml;
    }
    public void setCrossrefXml(final String inCrossrefXml) {
        crossrefXml = inCrossrefXml;
    }

    public static IDao<CrossRefLog> getDao() {
        return crossRefLogDao;
    }

    @Autowired
    @Qualifier("crossRefLogDao")
    public void setCrossRefLogDao(final IDao<CrossRefLog> inCrossRefLogDao) {
        crossRefLogDao = inCrossRefLogDao;
    }

}
