package org.zkoss.fiddle.visualmodel;

import java.util.List;

import org.zkoss.fiddle.model.Tag;

public class TagCloudVO {

	private int _min;

	private int _max;

	private double _divisor;

	private int _levels;

	public TagCloudVO(List<Tag> list) {
		this(list, 4);
	}

	public TagCloudVO(List<Tag> list, int levels) {
		Integer min = null;
		Integer max = null;

		_levels = levels;

		if (list.size() == 0) {
			throw new IllegalArgumentException("list is empty");
		}

		for (Tag t : list) {
			int val = t.getAmount().intValue();
			if (min == null || min > val)
				min = val;

			if (max == null || max < val)
				max = val;
		}

		_min = min;
		_max = max;

		if (_min == _max)
			_divisor = 1;
		else
			_divisor = (max - min) / (double) _levels;
	}
	
	public int getLevel(int amount) {
		return  ((int)((amount - _min) / _divisor));
	}

	public int getMin() {
		return _min;
	}

	public int getMax() {
		return _max;
	}

	public int getLevels() {
		return _levels;
	}
}