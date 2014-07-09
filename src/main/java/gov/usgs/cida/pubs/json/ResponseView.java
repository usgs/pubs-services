/**
 * Thanks to http://martypitt.wordpress.com/2012/11/05/custom-json-views-with-spring-mvc-and-jackson/ for this pattern. 
 * @author drsteini
 *
 */

package gov.usgs.cida.pubs.json;

import gov.usgs.cida.pubs.domain.intfc.BaseView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseView {
    public Class<? extends BaseView> value();
}
