/**
 * Thanks to http://martypitt.wordpress.com/2012/11/05/custom-json-views-with-spring-mvc-and-jackson/ for this pattern. 
 * @author drsteini
 *
 */

package gov.usgs.cida.pubs.json;

import gov.usgs.cida.pubs.domain.intfc.BaseView;
import gov.usgs.cida.pubs.domain.intfc.DataView;

//@Data
public class PojoView implements DataView {

    private final Object data;
    private final Class<? extends BaseView> view;
    public PojoView(Object result, Class<? extends BaseView> viewClass) {
        data = result;
        view = viewClass;
    }

    @Override
    public boolean hasView() {
        return true;
    }

    @Override
    public Class<? extends BaseView> getView() {
        // TODO Auto-generated method stub
        return view;
    }
    @Override
    public Object getData() {
        // TODO Auto-generated method stub
        return data;
    }

}