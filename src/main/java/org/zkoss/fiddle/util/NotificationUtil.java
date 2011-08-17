package org.zkoss.fiddle.util;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.zk.ui.Session;

/**
 * A util to help me setup the notifications.
 *
 * @author TonyQ
 *
 */
public class NotificationUtil {

	public static List<String> getNotifications(Session session) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) session
				.getAttribute(FiddleConstant.SESSION_NOTIFICATIONS);
		if (list == null)
			return new ArrayList<String>();

		return list;
	}

	public static void updateNotifications(Session session,
			List<String> notifications) {
		session.setAttribute(FiddleConstant.SESSION_NOTIFICATIONS,
				notifications);
	}

	public static void clearNotifications(Session session) {
		session.removeAttribute(FiddleConstant.SESSION_NOTIFICATIONS);
	}
}
