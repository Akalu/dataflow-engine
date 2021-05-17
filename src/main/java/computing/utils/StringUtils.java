package computing.utils;

public class StringUtils {

	private StringUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getCompositeId(String id1, String id2) {
		return String.format("%s:%s", id1, id2);
	}

	public static String getFileNameFromUrl(String fileUrl) {
		return fileUrl.replaceAll(".+/", "");

	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

}
