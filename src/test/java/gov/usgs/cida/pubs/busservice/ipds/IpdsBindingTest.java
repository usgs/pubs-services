package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.util.HashSet;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.BaseSpringTest;

@Ignore
public class IpdsBindingTest extends BaseSpringTest {

    @Autowired
    public String authorsXml;

    @Autowired
    public IpdsBinding binding;

    @Test
    public void bindContributorsTest() throws SAXException, IOException, ParserConfigurationException {
        binding.bindContributors(authorsXml);
    }

}
