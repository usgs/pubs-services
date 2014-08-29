package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import org.joda.time.LocalDate;

/**
 * @author drsteini
 *
 */
public abstract class MpBusService<D> extends BusService<D> {

    protected static String current_year = String.valueOf(new LocalDate().getYear());

    /**
     * Make sure the pw publication information exists in mp before working with it in mp.
     * @param prodId
     */
    protected void beginPublicationEdit(Integer prodId) {
        if (null != prodId) {
            //Look in MP to see if this key exists
            MpPublication mpPub = MpPublication.getDao().getById(prodId);
            if (null == mpPub) {
                //Didn't find it in MP, look in PW
                PwPublication pwPub = PwPublication.getDao().getById(prodId);
                if (null != pwPub) {
                    //There it is, copy it!
                    MpPublication.getDao().copyFromPw(prodId);
                    MpPublicationCostCenter.getDao().copyFromPw(prodId);
                    MpPublicationLink.getDao().copyFromPw(prodId);
                    MpPublicationContributor.getDao().copyFromPw(prodId);
                }
            }
        }
    }

}
