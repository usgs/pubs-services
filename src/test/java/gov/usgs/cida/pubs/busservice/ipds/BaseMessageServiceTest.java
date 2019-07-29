package gov.usgs.cida.pubs.busservice.ipds;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.jms.MessagePayload;

public abstract class BaseMessageServiceTest extends BaseIT {

	@MockBean
	protected IpdsProcess ipdsProcess;

	@MockBean
	protected IpdsWsRequester requester;

	public static final String EXPECTED_MESSAGE_TEXT = "<root><asOfDate>2013-10-31</asOfDate><priorToDate>2013-12-31</priorToDate><context>" + IpdsProcessTest.TEST_IPDS_CONTEXT + "</context></root>";

	public void setUp() throws Exception {
		when(requester.getSpnProduction(any())).thenAnswer(getAnswer());
		when(requester.getIpdsProductXml(any())).thenAnswer(getAnswer());
		when(ipdsProcess.processLog(any(ProcessType.class), anyInt(), anyString())).thenReturn("Did Processing");
	}

	public Answer<String> getAnswer() {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				if (args[0] instanceof MessagePayload) {
					MessagePayload payload = (MessagePayload) args[0];
					return "<root><asOfDate>" + payload.getAsOfString() + "</asOfDate><priorToDate>" + payload.getPriorToString() + "</priorToDate><context>" + payload.getContext() + "</context></root>";
				} else {
					return "<root>bad mojo</root>";
				}
			}
		};
	}

	public MessagePayload getPayload() {
		MessagePayload payload = new MessagePayload();
		payload.setAsOfDate("2013-10-31");
		payload.setPriorToDate("2013-12-31");
		payload.setContext(IpdsProcessTest.TEST_IPDS_CONTEXT);
		return payload;
	}

}
