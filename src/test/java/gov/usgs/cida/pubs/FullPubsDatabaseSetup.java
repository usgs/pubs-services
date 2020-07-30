package gov.usgs.cida.pubs;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseSetup("classpath:/testData/publicationType.xml")
@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
@DatabaseSetup("classpath:/testData/publicationSeries.xml")
@DatabaseSetup("classpath:/testData/contributor/")
@DatabaseSetup("classpath:/testData/dataset.xml")
public @interface FullPubsDatabaseSetup {

}
