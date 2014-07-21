package gov.usgs.cida.pubs.domain.ipds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicationMap {
    /**
     * using a map for dynamic mapping between IPDS Service XML and MyPubs fields.
     * if we used pojo fields then we would have to re-deploy on change.
     * the key is the IPDS field name and the value is the data associated from the XML
     */
    private final Map<String, String> values;


    /**
     * Since we are using a map to store values this allows us to simulate an immutable object.
     */
    private boolean isImmutable;

    /**
     * simple constructor
     */
    public PublicationMap () {
        values = new HashMap<String, String>();
    }

    /**
     * Returns the IPDS value from the give property name.
     * @param name
     * @return
     */
    public String get(String name) {
        return values.get(name);
    }

    public List<String> getFields() {
        return new ArrayList<String>( values.keySet() );
    }

    /**
     * Sets the property value for the give IPDS XML tag name.
     * Tag names to Pub fields mapping defined in the PUBS_IPDS_MAP table.
     * Values may be added, changed, and set until the object is locked.
     * This protects the values form accidental alteration after initial assignment.
     * 
     * @param name  IPDS XML tag name
     * @param value publication value for the given tag
     * @return
     */
    public String put(String name, String value) {
        if (isImmutable) {
            throw new IllegalStateException("Instance is immutable.");
        }
        if (value==null || value.trim().length()==0) {
            return null;
        }
        return values.put(name, value);
    }

    public String remove(String name) {
        if (isImmutable) {
            throw new IllegalStateException("Instance is immutable.");
        }
        return values.remove(name);
    }

    /**
     * Makes the instance read only or immutable.
     * Prevents accidental change after initial assignment.
     * Since we are using a map to store values this allows us to simulate an immutable object.
     */
    public PublicationMap setImmutable() {
        isImmutable = true;
        return this;
    }

    public boolean isImmutable() {
        return isImmutable();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
