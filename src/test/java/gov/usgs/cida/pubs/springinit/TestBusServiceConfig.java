package gov.usgs.cida.pubs.springinit;

import gov.usgs.cida.pubs.busservice.PublicationSeriesBusService;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.busservice.pw.PwPublicationBusService;
import gov.usgs.cida.pubs.domain.PublicationSeries;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestBusServiceConfig {

    @Bean
    public IPwPublicationBusService pwPublicationBusService() {
        return Mockito.mock(PwPublicationBusService.class);
    }

    @Bean
    public IBusService<PublicationSeries> publicationSeriesBusService() {
        return Mockito.mock(PublicationSeriesBusService.class);
    }

}
