package gov.usgs.cida.pubs.validation.unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.Affiliation;

/**
 * Implemented custom Mock class due to Mockito when(...).thenReturn(...) having issues
 * handling a list containing multiple affiliation types. Need to have CostCenter and 
 * OutsideAffilaiation affiliation.
 */
public class MockAffiliationDao implements IDao<Affiliation<?>> {
	private List<Affiliation<?>> affialiationList = new ArrayList<>();
	private int getByMapCount = 0; // call count

	@Autowired
	public MockAffiliationDao() {
	}

	public void setAffialiationList(List<Affiliation<?>> affialiationList) {
		this.affialiationList = affialiationList;
	}

	public int getGetByMapCount() {
		return getByMapCount;
	}

	public void reset() {
		affialiationList.clear();
		getByMapCount = 0;
	}

	@Override
	public Integer add(Affiliation<?> domainObject) {
		return null;
	}

	@Override
	public Affiliation<?> getById(Integer domainID) {
		return null;
	}

	@Override
	public Affiliation<?> getById(String domainID) {
		return null;
	}

	@Override
	public List<Affiliation<?>> getByMap(Map<String, Object> filters) {
		getByMapCount++;
		return affialiationList;
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return null;
	}

	@Override
	public void update(Affiliation<?> domainObject) {
	}

	@Override
	public void delete(Affiliation<?> domainObject) {
	}

	@Override
	public void deleteById(Integer domainID) {
	}

	@Override
	public void deleteByParent(Integer parentID) {
	}

	@Override
	public Map<Integer, Map<String, Object>> uniqueCheck(Affiliation<?> domainObject) {
		return null;
	}

}
