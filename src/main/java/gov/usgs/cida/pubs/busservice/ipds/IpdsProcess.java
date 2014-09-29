package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class IpdsProcess implements IIpdsProcess {

    private final ICrossRefBusService crossRefBusService;
    private final IpdsBinding binder;
    private final IpdsWsRequester requester;
    private final IMpPublicationBusService pubBusService;

    private int additions = 0;

    private int errors = 0;

    @Autowired
    public IpdsProcess(final ICrossRefBusService crossRefBusService,
            final IpdsBinding binder,
            final IpdsWsRequester requester,
            final IMpPublicationBusService pubBusService) {
        this.crossRefBusService = crossRefBusService;
        this.binder = binder;
        this.requester = requester;
        this.pubBusService = pubBusService;
    }

    public String processLog(final ProcessType inProcessType, final int logId) {
        StringBuilder rtn = new StringBuilder();
        int totalEntries = 0;
        additions = 0;
        errors = 0;

        List<PubMap> pubs = IpdsMessageLog.getDao().getFromIpds(logId);

        for (PubMap pub : pubs) {
            totalEntries++;
            rtn.append(pub.get(IpdsMessageLog.IPNUMBER) + ":" + processPublication(inProcessType, pub));
        }

        String counts = "Summary:\n\tTotal Entries: " + totalEntries + "\n\tPublications Added: " + additions + "\n\tErrors Encountered: " + errors + "\n";

        rtn.insert(0, counts);

        return rtn.toString();
    }

    protected String processPublication(final ProcessType inProcessType, final PubMap inPub) {
        MpPublication pub = binder.bindPublication(inPub);
        pub.setIpdsReviewProcessState(inProcessType.getIpdsValue());

        //Check for existing data in MyPubs land - use the first hit if any found.
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("IPDS_ID", pub.getIpdsId());
        List<MpPublication> existingPubs = MpPublication.getDao().getByMap(filters);
        MpPublication existingPub = null == existingPubs ? null : existingPubs.isEmpty() ? null : existingPubs.get(0);

        StringBuilder rtn = new StringBuilder("");

        if (okToProcess(inProcessType, pub, existingPub)) {
            //We only keep the prodID from the original MP record. The delete is to make sure we kill all child objects.
            if (null != existingPub) {
                pub.setId(existingPub.getId());
                pubBusService.deleteObject(existingPub.getId());
            };

            // get contributors from web service
            String contributorXml = requester.getContributors(pub.getIpdsId());
            try {
                Collection<MpPublicationContributor> contributors = binder.bindContributors(contributorXml);
                //TODO refactor contributors on the publication so we can just add them all
                Collection<PublicationContributor<?>> authors = new ArrayList<>();
                Collection<PublicationContributor<?>> editors = new ArrayList<>();
                for (Iterator<MpPublicationContributor> contributorsIter = contributors.iterator(); contributorsIter.hasNext();) {
                    MpPublicationContributor contributor = contributorsIter.next();
                    if (ContributorType.AUTHORS.equals(contributor.getContributorType().getId())) {
                        authors.add(contributor);
                    } else {
                        editors.add(contributor);
                    }
                }
                pub.setAuthors(authors);
                pub.setEditors(editors);
            } catch (Exception e) {
                rtn.append("\n\tTrouble getting authors/editors: " + e.getMessage());
                errors++;
            }

            //get contributingOffice from web service
            try {
                Affiliation<?> costCenter = binder.getOrCreateCostCenter(inPub);
                if (null != costCenter) {
                    PublicationCostCenter<?> pubCostCenter = new MpPublicationCostCenter();
                    pubCostCenter.setCostCenter((CostCenter) costCenter);
                    List<PublicationCostCenter<?>> pccs = new ArrayList<PublicationCostCenter<?>>();
                    pccs.add(pubCostCenter);
                    pub.setCostCenters(pccs);
                }
            } catch (Exception e) {
                rtn.append("\n\tTrouble getting cost center: " + e.getMessage());
                errors++;
            }

            //get notes from web service
            String notesXml = requester.getNotes(pub.getIpdsId());
            Set<String> notesTags = new HashSet<String>();
            notesTags.add("NoteComment");
            try {
                PublicationMap notes = binder.bindNotes(notesXml, notesTags);
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

            pub.setLinks(binder.bindPublishedURL(inPub));

            MpPublication rtnPub = pubBusService.createObject(pub);

            if (null == rtnPub.getValidationErrors() || rtnPub.getValidationErrors().isEmpty()) {
                additions++;
                rtn.append("\n\tAdded to MyPubs as ProdId: " + rtnPub.getId());
                if (null != inProcessType && ProcessType.SPN_PRODUCTION == inProcessType) {
                    rtn.append(updateIpdsWithDoi(rtnPub));
                } else if (null != inProcessType && ProcessType.DISSEMINATION == inProcessType
                			&& (PubsUtilities.isUsgsNumberedSeries(rtnPub.getPublicationSubtype())
                					|| PubsUtilities.isUsgsUnnumberedSeries(rtnPub.getPublicationSubtype()))
                            && (null != rtnPub.getDoi() && 0 < rtnPub.getDoi().length())) {
                	crossRefBusService.submitCrossRef(rtnPub);
                }
            } else {
                rtn.append("\n\t" + rtnPub.getValidationErrors().toString());
            }

        } else {
            rtn.append("\n\t" + "IPDS record not processed (" + inProcessType + ")-");
            if (null != pub.getPublicationType()) {
            	rtn.append(" Publication Type: " + pub.getPublicationType().getText());
            }
            if (null != pub.getPublicationSubtype()) {
            	rtn.append(" PublicationSubtype: " + pub.getPublicationSubtype().getText());
            }
            if (null != pub.getSeriesTitle()) {
            	rtn.append(" Series: " + pub.getSeriesTitle().getText());
            }
            rtn.append(" Process State: " + pub.getIpdsReviewProcessState() + " DOI: " + pub.getDoi());
        }

        return rtn.append("\n\n").toString();
    }

	protected boolean okToProcess(final ProcessType inProcessType, final MpPublication pub,
			final MpPublication existingPub) {
		if (null != inProcessType && null != pub && null != pub.getPublicationType()) {
			switch (inProcessType) {
			case DISSEMINATION:
				return okToProcessDissemination(pub, existingPub);
			case SPN_PRODUCTION:
				return okToProcessSpnProduction(pub);
			default:
				break;
			}
		}
		return false;
	}

	protected boolean okToProcessDissemination(final MpPublication pub, final MpPublication existingPub) {
		if (null != pub) {
			if (PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype())
				&& null == pub.getSeriesTitle()) {
				//Do not process USGS numbered series without an actual series.
				return false;
			} else if (null == existingPub || null == existingPub.getIpdsReviewProcessState()
					|| ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(existingPub.getIpdsReviewProcessState())) {
				//It is ok to process a publication already in our system if has no review state or
				//was in the SPN Production state. (Or if it is not already in our system).
				return true;
			}
		}
		return false;
	}

	protected boolean okToProcessSpnProduction(final MpPublication pub) {
		if (null != pub) {
			if (StringUtils.isNotEmpty(pub.getDoi())) {
				//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
				return false;
			} else if (null == pub.getIpdsReviewProcessState() || !ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(pub.getIpdsReviewProcessState())) {
				//Skip if not in SPN Production (shouldn't happen as we are querying SPN Production only)
				return false;
//TODO remove if we really don't have these
//        	} else if (PublicationType.USGS_UNNUMBERED_SERIES.contentEquals(String.valueOf(pubType.getId()))) {
//          	//Process all USGS unnumbered series
//          	return true;
			} else if (PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype())) {
//            	if (null != pub.getSeries()
//              	      && pub.getSeries().contentEquals("Administrative Report")) {
//                	//Skip the administrative series
//                	return false;
//          } else {
			//Process all other USGS numbered series
				return true;
			}
		}
		return false;
	}

	protected String updateIpdsWithDoi(final MpPublication inPub) {
        String result = requester.updateIpdsDoi(inPub);
        if (null == result || result.contains("ERROR")) {
            errors++;
        }
        return result;
    }

}
