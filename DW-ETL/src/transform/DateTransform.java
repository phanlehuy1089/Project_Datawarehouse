package transform;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTransform {
	public static void transformDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		String dateFormat = formatter.format(date);
		System.out.println("Ngày đã được định dạng : " + dateFormat);
	}
	public static void main(String[] args) {
		transformDate();
	}
}
