package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IpdsProcess implements IIpdsProcess {

    private ICrossRefBusService crossRefBusService;

    protected IpdsWsRequester requester;

    private int additions = 0;

    private int errors = 0;

    private IMpPublicationBusService pubBusService;

    private IBusService<MpPublicationLink> linkBusService;

    private Map<String, String> pubTypeMap;

    public String processLog(final ProcessType inProcessType, final int logId) throws Exception {
        StringBuilder rtn = new StringBuilder();
        int totalEntries = 0;
        additions = 0;
        errors = 0;

        List<MpPublication> pubs = IpdsMessageLog.getDao().getFromIpds(logId);

        for (MpPublication pub : pubs) {
            totalEntries++;
            rtn.append(pub.getIpdsId() + ":" + processPublication(inProcessType, pub));
        }

        String counts = "Summary:\n\tTotal Entries: " + totalEntries + "\n\tPublications Added: " + additions + "\n\tErrors Encountered: " + errors + "\n";

        rtn.insert(0, counts);

        return rtn.toString();
    }

    protected String processPublication(final ProcessType inProcessType, final MpPublication inPub) {
        MpPublication pub = inPub;
        pub.setIpdsReviewProcessState(inProcessType.getIpdsValue());

        //Check for existing data in MyPubs land - use the first hit if any found.
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("ipdsId", inPub.getIpdsId());
        List<MpPublication> existingPubs = MpPublication.getDao().getByMap(filters);
        MpPublication existingPub = null == existingPubs ? null : 0 == existingPubs.size() ? null : existingPubs.get(0);

        StringBuilder rtn = new StringBuilder("");

        PublicationType pubType = getMyPublicationType(pub);

        if (okToProcess(inProcessType, pubType, pub, existingPub)) {
            //We only keep the prodID from the original MP record.
            if (null != existingPub) {
                pub.setId(existingPub.getId());
                pubBusService.deleteObject(existingPub);
            };
            //TODO new type/subtype/series logic
//            pub.setPublicationTypeId(String.valueOf(pubType.getId()));
//            pub.setPublicationType(pubType.getName());

            // get authors from web service
            final String authXml = requester.getAuthors(pub.getIpdsId());
            IpdsBinding binder = new IpdsBinding(new HashSet<String>());
            try {
                PublicationMap authors = binder.bindAuthors(authXml);
                //TODO new author logic
//                pub.setAuthorDisplay(authors.get("AuthorNameText"));
//                pub.setEditor(authors.get("EditorNameText"));
            } catch (Exception e) {
                rtn.append("\n\tTrouble getting authors/editors: " + e.getMessage());
                errors++;
            }

            //get contributingOffice from web service
            //TODO new costcenter logic
//            String costCenterXml = requester.getCostCenter(pub.getContributingOffice(), pub.getIpdsId());
//            Set<String> costCenterTags = new HashSet<String>();
//            costCenterTags.add("Name");
//            IpdsBinding ccBinder = new IpdsBinding(costCenterTags);
//            try {
//                PublicationMap costCenter = ccBinder.bindCostCenter(costCenterXml);
//                pub.setContributingOffice(costCenter.get("Name"));
//            } catch (Exception e) {
//                rtn.append("\n\tTrouble getting contributing office: " + e.getMessage());
//                errors++;
//            }

            //get notes from web service
            String notesXml = requester.getNotes(pub.getIpdsId());
            Set<String> notesTags = new HashSet<String>();
            notesTags.add("NoteComment");
            IpdsBinding noteBinder = new IpdsBinding(notesTags);
            try {
                PublicationMap notes = noteBinder.bindNotes(notesXml);
                if (null != notes.get("NoteComment")
                        && 0 < notes.get("NoteComment").length()) {
                    if (null != pub.getNotes() && 0 < pub.getNotes().length()) {
                        pub.setNotes(pub.getNotes() + "\n\t" + notes.get("NoteComment"));
                    } else {
                        pub.setNotes(notes.get("NoteComment"));
                    }
                }
            } catch (Exception e) {
                rtn.append("\n\tTrouble getting note comment: " + e.getMessage());
                errors++;
            }

            MpPublication rtnPub = pubBusService.createObject(pub);
            if (null == rtnPub.getValidationErrors() || rtnPub.getValidationErrors().isEmpty()) {
                additions++;
                rtn.append("\n\tAdded to MyPubs as ProdId: " + rtnPub.getId());
                rtn.append(handlePublishedURL(rtnPub.getId(), pub));
                if (null != inProcessType && ProcessType.SPN_PRODUCTION == inProcessType) {
                    rtn.append(updateIpdsWithDoi(rtnPub));
                } else if (null != inProcessType && ProcessType.DISSEMINATION == inProcessType) {
                    //TODO new type/subtype/series logic
//                    if ((PublicationType.USGS_NUMBERED_SERIES.contentEquals(rtnPub.getPublicationTypeId())
//                            || PublicationType.USGS_UNNUMBERED_SERIES.contentEquals(rtnPub.getPublicationTypeId()))
//                            && (null != rtnPub.getDoiName() && 0 < rtnPub.getDoiName().length())) {
//                        crossRefBusService.submitCrossRef(rtnPub);
//                    }

                }
            } else {
                rtn.append("\n\t" + rtnPub.getValidationErrors().toString());
            }

        } else {
            rtn.append("\n\t" + "IPDS record not processed (" + inProcessType + ")- Publication Type: ")
            //TODO new type/subtype/series logic
//            .append(pub.getPublicationType() + " Series: " + pub.getSeries())
            .append(" Process State: " + pub.getIpdsReviewProcessState() + " DOI: " + pub.getDoiName());
        }

        return rtn.append("\n\n").toString();
    }

    protected boolean okToProcess(final ProcessType inProcessType, final PublicationType pubType, final MpPublication pub,
            final MpPublication existingPub) {
        boolean rtn = false;
        if (null != inProcessType && null != pubType && null != pub) {
            //TODO new type/subtype/series logic
//            switch (inProcessType) {
//            case DISSEMINATION:
//                if (PublicationType.USGS_NUMBERED_SERIES.contentEquals(String.valueOf(pubType.getId()))
//                        && null != pub.getSeries()
//                        && pub.getSeries().contentEquals("Administrative Report")) {
//                    //Do not process administrative reports or USGS numbered series without an actual series.
//                    rtn = false;
//                } else {
//                    if (null == existingPub || null == existingPub.getIpdsReviewProcessState() 
//                            || ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(existingPub.getIpdsReviewProcessState())) {
//                        //It is ok to process a publication already in our system if has no review state or
//                        //was in the SPN Production state. (Or if it is not already in our system).
//                        rtn = true;
//                    } else {
//                        //Do not process if already in our system (with a Dissemination state).
//                        rtn = false;
//                    }
//                }
//                break;
//            case SPN_PRODUCTION:
//                if (null != pub.getDoiName()) {
//                    //Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
//                    rtn = false;
//                } else
//                    if (null == pub.getIpdsReviewProcessState() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(pub.getIpdsReviewProcessState())) {
//                    //Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
//                    rtn = false;
//                } else if (PublicationType.USGS_UNNUMBERED_SERIES.contentEquals(String.valueOf(pubType.getId()))) {
//                    //Process all USGS unnumbered series
//                    rtn = true;
//                } else if (PublicationType.USGS_NUMBERED_SERIES.contentEquals(String.valueOf(pubType.getId()))) {
//                    if (null != pub.getSeries()
//                            && pub.getSeries().contentEquals("Administrative Report")) {
//                        //Skip the administrative series
//                        rtn = false;
//                    } else {
//                        //Process all other USGS numbered series
//                        rtn = true;
//                    }
//                }
//                break;
//            default:
//                break;
//            }
        }
        return rtn;
    }

    protected String handlePublishedURL(final Integer pubId, final MpPublication pub) {
        StringBuilder rtn = new StringBuilder("");
        //transform the PublishedURL - note that this is an abuse of the BASIC_SEARCH column!!! 
        //BASIC_SEARCH will be overwritten with the correct values when the pub is published...
        //We pull the URL from the structure "URL, DisplayText"
        //TODO handle this correctly
//        if (null != pub
//                && null != pub.getBasicSearch()) {
//            String[] publishedUrls = pub.getBasicSearch().split(",");
//            if (0 < publishedUrls.length
//                    && 0 < publishedUrls[0].length()) {
//                MpPublicationLink link = new MpPublicationLink();
//                link.setPublicationId(pubId);
//                link.setUrl(publishedUrls[0]);
//                link.setLinkType(LinkType.getDao().getById(LinkType.INDEX_PAGE));
//                MpPublicationLink rtnLink = linkBusService.createObject(link);
//                if (null == rtnLink.getValidationErrors() || rtnLink.getValidationErrors().isEmpty()) {
//                    rtn.append("\n\tAdded linkId: " + rtnLink.getId());
//                } else {
//                    rtn.append("\n\t" + rtnLink.getValidationErrors().toString());
//                }
//            }
//        }
        return rtn.toString();
    }

    protected PublicationType getMyPublicationType(final MpPublication pub) {
        PublicationType pt = null;
        if (null != pub
                && null != pub.getPublicationType()
                && null != pubTypeMap
                && pubTypeMap.containsKey(pub.getPublicationType())
                && null != pubTypeMap.get(pub.getPublicationType())) {
            String ptId = pubTypeMap.get(pub.getPublicationType());
            pt = PublicationType.getDao().getById(ptId);
        }
        return pt;
    }

    protected String updateIpdsWithDoi(final MpPublication inPub) {
        String result = requester.updateIpdsDoi(inPub);
        if (null == result || result.contains("ERROR")) {
            errors++;
        }
        return result;
    }

    /**
     * Set the requester.
     * @param IpdsWsRequester .
     */
    public void setIpdsWsRequester(final IpdsWsRequester inIpdsWsRequester) {
        requester = inIpdsWsRequester;
    }

    /**
     * Set the service.
     * @param inMpPublicationBusService .
     */
    public void setMpPublicationBusService(final IMpPublicationBusService inMpPublicationBusService) {
        pubBusService = inMpPublicationBusService;
    }

    public void setCrossRefBusService(final ICrossRefBusService inCrossRefBusService) {
        crossRefBusService = inCrossRefBusService;
    }

    public void setPubTypeMap(final Map<String, String> inPubTypeMap) {
        pubTypeMap = inPubTypeMap;
    }

    public void setMpLinkDimBusService(final IBusService<MpPublicationLink> inLinkBusService) {
        linkBusService = inLinkBusService;
    }

}
