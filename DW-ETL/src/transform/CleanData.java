package transform;

public class CleanData {
	public static String removeSpace(String data) {
		return data.trim();
	}
	public static int replaceNumeric() {
		return -1;
	}
	public static String replaceNominal() {
		return "?";
	}
	public static void main(String[] args) {
		String s = "    ab  c  ";
		System.out.println(removeSpace(s));
	}
}
