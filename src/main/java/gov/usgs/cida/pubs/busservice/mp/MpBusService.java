package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

public abstract class MpBusService<D> extends BusService<D> {

    /**
     * Make sure the pw publication information exists in mp before working with it in mp.
     * @param publicationId
     */
    protected void beginPublicationEdit(Integer publicationId) {
        if (null != publicationId) {
            //Look in MP to see if this key exists
            MpPublication mpPub = MpPublication.getDao().getById(publicationId);
            if (null == mpPub) {
                //Didn't find it in MP, look in PW
                PwPublication pwPub = PwPublication.getDao().getById(publicationId);
                if (null != pwPub) {
                    //There it is, copy it!
                    MpPublication.getDao().copyFromPw(publicationId);
                    MpPublicationCostCenter.getDao().copyFromPw(publicationId);
                    MpPublicationLink.getDao().copyFromPw(publicationId);
                    MpPublicationContributor.getDao().copyFromPw(publicationId);
                }
            }
           	MpPublication.getDao().lockPub(publicationId);
        }
    }

}
