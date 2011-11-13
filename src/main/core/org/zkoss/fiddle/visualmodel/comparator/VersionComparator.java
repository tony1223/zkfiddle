package org.zkoss.fiddle.visualmodel.comparator;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

	public int compare(String version, String otherversion) {

		if (version == null || otherversion == null) {
			throw new IllegalArgumentException("objects shouldn't be null");
		}
		if (version.indexOf("FL") != -1 && (otherversion.indexOf("FL") == -1)) {
			return 1;
		} else if ((version.indexOf("FL") == -1) && (otherversion.indexOf("FL") != -1)) {
			return -1;
		}

		String[] versiontokens = version.split("\\.");
		String[] otherversiontokens = otherversion.split("\\.");

		for (int i = 0; i < versiontokens.length && i < otherversiontokens.length; ++i) {
			String num1 = versiontokens[i].trim();
			String num2 = otherversiontokens[i].trim();

			if (num1.matches("[0-9+]") && num2.matches("[0-9]+")) {

				int comp = Integer.valueOf(num2).compareTo(Integer.valueOf(num1));

				if (comp != 0)
					return comp;
			} else {
				break;
			}
		}

		return 0;
	}

}
