/**
 * Thanks to http://martypitt.wordpress.com/2012/11/05/custom-json-views-with-spring-mvc-and-jackson/ for this pattern. 
 */

package gov.usgs.cida.pubs.json;

import gov.usgs.cida.pubs.json.view.intfc.IBaseView;
import gov.usgs.cida.pubs.json.view.intfc.IDataView;

public class PojoView implements IDataView {

    private final Object data;
    private final Class<? extends IBaseView> view;
    public PojoView(Object result, Class<? extends IBaseView> viewClass) {
        data = result;
        view = viewClass;
    }

    @Override
    public boolean hasView() {
        return true;
    }

    @Override
    public Class<? extends IBaseView> getView() {
        // TODO Auto-generated method stub
        return view;
    }
    @Override
    public Object getData() {
        // TODO Auto-generated method stub
        return data;
    }

}