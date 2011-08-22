package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRating;
import org.zkoss.fiddle.visualmodel.RatingAmount;

public interface ICaseRatingdDao extends IDao<CaseRating> {
	public CaseRating findBy(final Case theCase, final String userName) ;

	public RatingAmount countAverage(final Case theCase) ;
}
