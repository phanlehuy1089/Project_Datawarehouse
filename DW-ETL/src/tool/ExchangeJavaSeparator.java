package tool;

public class ExchangeJavaSeparator {
	public static String exchangeSeparator(String path) {
		String javaPath = path.replace("/", "\\\\");
		return javaPath;
	}
	public static void main(String[] args) {
		System.out.println(exchangeSeparator("C:/ProgramData/MySQL/MySQL Server 8.0/Uploads"));
	}
}
