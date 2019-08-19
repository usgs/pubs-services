package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.domain.sipp.IpdsBureauApproval;

public final class IpdsBureauApprovalHelper {

	private IpdsBureauApprovalHelper() {
	}

	public static IpdsBureauApproval getIpdsBureauApproval(String ipNumber) {
		IpdsBureauApproval ipdsBureauApproval = new IpdsBureauApproval();
		ipdsBureauApproval.setIpNumber(ipNumber);
		return ipdsBureauApproval;
	}
}
