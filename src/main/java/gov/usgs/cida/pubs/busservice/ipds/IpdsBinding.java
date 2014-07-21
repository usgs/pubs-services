package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.domain.ipds.PublicationMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * Note: when binging author this uses the AuthorsNameText and EditorsNameText names for IPDS 
 * and should be mapped to the same field in MyPubs. Whiles is technically a transform within
 * the binding - it felt like the correct location. Of course we could move this if seen fit.
 * 
 * @author David U
 *
 */
public class IpdsBinding {

    public static final String NAMESPACE  = "http://schemas.microsoft.com/ado/2007/08/dataservices";
    public static String SEPARATOR = ";";

    static class Author {
        int order;
        String name;
    }
    static class AuthorSet extends TreeSet<Author> {
        private static final long serialVersionUID = -4126867439704001904L;
        public AuthorSet() {
            super(new Comparator<Author>() {
                @Override
                public int compare(Author a, Author b) {
                    return a.order<b.order?-1:a.order>b.order?1:a.name.compareTo(b.name);
                }
            });
        }
    }

    /**
     * maps IPDS tags to PUBS fields
     * Map<tag,field>
     */
    private final Set<String> tagsOfInterest; // TODO log if not one-to-one before given here

    public IpdsBinding(Set<String> tagsOfInterest) {
        this.tagsOfInterest = tagsOfInterest;
    }

    public PublicationMap bindCostCenter(String costCenterXml) throws SAXException, IOException {
        PublicationMap costCenter = new PublicationMap();
        bindGeneral(costCenter, costCenterXml);
        return costCenter;
    }

    public PublicationMap bindNotes(String notesXml) throws SAXException, IOException {
        PublicationMap notes = new PublicationMap();
        Document doc= makeDocument(notesXml);

        for (String tagName : tagsOfInterest) {
            StringBuilder valueText = new StringBuilder();
            NodeList nodes = doc.getElementsByTagNameNS(NAMESPACE, tagName);
            if (nodes.getLength() < 1) {
                // TODO do we want to soft with no log or hard fail with pLog
                continue;
            }
            for (int i = 0; i < nodes.getLength(); i++) {
                valueText.append(nodes.item(i).getTextContent().trim()).append("|");
            }
            if (0 < valueText.length()) {
                notes.put(tagName, valueText.toString());
            }
        }
        return notes;
    }

    protected void bindGeneral(PublicationMap map, String ipdsXml) throws SAXException, IOException {
        Document doc= makeDocument(ipdsXml);

        for (String tagName : tagsOfInterest) {
            NodeList nodes = doc.getElementsByTagNameNS(NAMESPACE, tagName);
            if (nodes.getLength() < 1) {
                // TODO do we want to soft with no log or hard fail with pLog
                continue;
            }
            if (nodes.getLength() > 1) {
                // TODO log too many values but take the first
            }
            String value = nodes.item(0).getTextContent().trim();
            map.put(tagName, value);
        }
    }

    public PublicationMap bindAuthors(String authorsXml) throws SAXException, IOException {
        PublicationMap ipds = new PublicationMap();
        Document doc= makeDocument(authorsXml);

        SortedSet<Author> authors = new AuthorSet();
        SortedSet<Author> editors = new AuthorSet();

        // for each author entry in doc
        NodeList names = doc.getElementsByTagNameNS(NAMESPACE,"AuthorNameText");
        NodeList ranks = doc.getElementsByTagNameNS(NAMESPACE,"Rank");
        NodeList types = doc.getElementsByTagNameNS(NAMESPACE,"ContentType");

        for (int n=0; n<names.getLength(); n++) {
            // create a new author name and rank
            Author person = new Author();

            person.name   = names.item(n).getTextContent();
            try {
                person.order  = Integer.parseInt( ranks.item(n).getTextContent() );
            } catch (Exception e) {
                // TODO should this be logged to pLog
                person.order = 99;
            }
            // add to authors or editors by contentType
            if ("Author".equalsIgnoreCase( types.item(n).getTextContent() )) {
                authors.add(person);
            } else {
                // TODO do we have other types
                editors.add(person);
            }
        }

        // convert authors and editors to ordered comma separated strings
        String authorNames = concatNames(authors);
        String editorNames = concatNames(editors);

        // add authors and editors to object
        ipds.put("AuthorNameText", authorNames);
        ipds.put("EditorNameText", editorNames);
        return ipds;
    }

    protected String concatNames(Collection<Author> authors) {
        StringBuilder concat = new StringBuilder();

        String sep="";
        for (Author author : authors) {
            concat.append(sep).append(author.name.trim());
            sep=SEPARATOR;
        }

        return concat.toString();
    }

    protected Document makeDocument(String xml) throws SAXException, IOException {
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        InputSource source = new InputSource(stream);
        DOMParser   parser = new DOMParser();
        parser.parse(source);
        Document doc= parser.getDocument();
        return doc;
    }

}
