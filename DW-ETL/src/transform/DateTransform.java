package transform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.DateUtil;

public class DateTransform {
	public static String transformDateInt(int dateInt) {
		SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy");
		Date javaDate = DateUtil.getJavaDate((double) dateInt);
		String dateFormat = formatter.format(javaDate);
		return dateFormat;
	}
	public static String transformDateString(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy");
		Date javaDate = null;
		String dateFormat = null;
		try {
			javaDate = formatter.parse(dateString);
			dateFormat = formatter.format(javaDate);
		} catch (ParseException e) {
			System.out.println("<---> ERROR [Transform date from String dd/MM/yyyy]: " + e.getMessage());
		}
		return dateFormat;
	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(transformDateString("08/5/2020"));
	}
}
