package gov.usgs.cida.pubs.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JmsExceptionListener implements ExceptionListener {

    private static final Log LOG = LogFactory.getLog(JmsExceptionListener.class);

    public void onException( final JMSException e ) {
        LOG.info(e);
    }

}
