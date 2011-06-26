package org.zkoss.fiddle.dao.api;

import java.util.List;

public interface IDao<T> {

	public abstract List<T> list();

	public abstract void saveOrUdate(T m);

	public abstract T get(Long id);

	public abstract void remove(T m);

	public abstract void remove(Long id);

}
