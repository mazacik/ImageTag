package application.misc;

public abstract class CompareUtil {
	public static double getStringSimilarity(String s1, String s2) {
		String helper = s2;
		for (char c : s1.toCharArray()) {
			s2 = s2.replaceFirst(c + "", "");
		}
		
		double difference = helper.length() - s2.length();
		return difference / helper.length();
	}
}
