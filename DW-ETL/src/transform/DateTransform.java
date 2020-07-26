package transform;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.DateUtil;

public class DateTransform {
	public static String transformDate(int dateInt) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date javaDate = DateUtil.getJavaDate((double) dateInt);
		String dateFormat = formatter.format(javaDate);
		return dateFormat;
	}
	public static void main(String[] args) {
		System.out.println(transformDate(35214));
	}
}
