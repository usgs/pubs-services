package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.View;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;

public class PublicationInteraction extends BaseDomain<PublicationInteraction> implements Serializable {

	private static final long serialVersionUID = -3684046139875959389L;

	@JsonView(View.PW.class)
	private Publication<?> subject;
	
	@JsonView(View.PW.class)
	private Predicate predicate;
	
	@JsonView(View.PW.class)
	private Publication<?> object;

	public Publication<?> getSubject() {
		return subject;
	}

	public void setSubject(final Publication<?> inSubject) {
		subject = inSubject;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(final Predicate inPredicate) {
		predicate = inPredicate;
	}

	public Publication<?> getObject() {
		return object;
	}

	public void setObject(final Publication<?> inObject) {
		object = inObject;
	}
	
}
