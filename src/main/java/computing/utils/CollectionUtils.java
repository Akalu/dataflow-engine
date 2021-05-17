package computing.utils;

import java.util.Set;

public class CollectionUtils {

	private CollectionUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean inSet(String str, Set<String> set) {
		if ("".equals(str) || str == null) {
			return false;
		}
		for (String val : set) {
			if (str.contains(val)) {
				return true;
			}
		}
		return false;
	}
}
