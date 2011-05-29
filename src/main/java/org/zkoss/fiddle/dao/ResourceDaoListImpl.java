package org.zkoss.fiddle.dao;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.fiddle.model.Resource;

public class ResourceDaoListImpl /*implements IResourceDao */ implements IResourceDao{

	private static ArrayList<Resource> list = new ArrayList<Resource>();
	private static Long lastIndex = 0L;

	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.dao.ResourceDao#list()
	 */
	public List<Resource> list() {
		return list;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.dao.ResourceDao#saveOrUdate(org.zkoss.fiddle.model.Resource)
	 */
	public void saveOrUdate(Resource m) {
		if (m.getId() != null) {
			for (Resource mes : list) {
				if (mes.getId() == m.getId()) {
					mes.setName(m.getName());
					mes.setContent(m.getContent());
					return;
				}
			}
			list.add(m);
			if (m.getId() > lastIndex)
				lastIndex = m.getId();
		} else {
			lastIndex++;
			m.setId(lastIndex);
			list.add(m);
		}
	}

	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.dao.ResourceDao#get(java.lang.Long)
	 */
	public Resource get(Long id) {
		for (Resource mes : list) {
			if (mes.getId() == id) {
				return mes;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.dao.ResourceDao#remove(org.zkoss.fiddle.model.Resource)
	 */
	public void remove(Resource m) {
		remove(m.getId());
	}

	/* (non-Javadoc)
	 * @see org.zkoss.fiddle.dao.ResourceDao#remove(java.lang.Long)
	 */
	public void remove(Long id) {
		List<Integer> removelist = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); ++i) {
			Resource mes = list.get(i);
			if (mes.getId() == id) {
				removelist.add(i);
			}
		}
		for (int i = 0; i < removelist.size(); ++i) {
			list.remove(removelist.get(i) - i);
		}
	}

	public List<Resource> listByCase(Long caseId) {
		// TODO Auto-generated method stub
		return null;
	}
}
