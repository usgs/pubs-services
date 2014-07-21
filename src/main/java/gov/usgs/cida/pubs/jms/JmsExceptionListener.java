package gov.usgs.cida.pubs.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class JmsExceptionListener implements ExceptionListener {
    public void onException( final JMSException e )
    {
        e.printStackTrace();
    }

}
