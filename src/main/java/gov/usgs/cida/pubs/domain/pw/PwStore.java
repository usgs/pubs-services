package gov.usgs.cida.pubs.domain.pw;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.json.View;

public class PwStore extends BaseDomain<PwStore> implements Serializable {

	private static final long serialVersionUID = 8330907350609635194L;

	@JsonIgnore
	private Integer pwStoreId;

	@JsonView(View.PW.class)
	private Integer publicationId;

	@JsonView(View.PW.class)
	private String store;

	@JsonView(View.PW.class)
	private boolean available;

	@JsonView(View.PW.class)
	private BigDecimal price;
	
	public Integer getPublicationId() {
		return publicationId;
	}
	public void setPublicationId(Integer publicationId) {
		this.publicationId = publicationId;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
