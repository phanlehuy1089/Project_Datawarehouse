package transform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import model.InfoConfig;

public class TransformData {
	public static void transformData(Connection connection, String dbName, String dataObject) throws SQLException {
		String fullTableName = dbName+".tb_staging_"+dataObject;
		String fieldDateName = "";
		switch (dataObject) {
		case "sinhvien":
			fieldDateName = "ngay_sinh";
			break;
		case "dangky":
			fieldDateName = "ngay_dk";
			break;
		default:
			break;
		}
		// Format date dạng YYYY-mm-dd sang dd/mm/YYYY
		String regExFormDate1 = "^[0-9]{4}-(1[0-2]|0[1-9])-(3[0-1]|[1-2][0-9]|0[1-9])$";
		Pattern pattern1 = Pattern.compile(regExFormDate1);
		String sqlFormDate1 = "UPDATE "+fullTableName+" SET "+fieldDateName+" = date_format(str_to_date("+fieldDateName+",'%Y-%m-%d'),'%d/%m/%Y') WHERE "+fieldDateName+" <> '?'";
		// Format date dạng d/m/YYYY sang dd/mm/YYYY
		String regExFormDate2 = "^(3[0-1]|[1-2][0-9]|[1-9])/(1[0-2]|[1-9])/[0-9]{4}$";
		Pattern pattern2 = Pattern.compile(regExFormDate2);
		String sqlFormDate2 = "UPDATE "+fullTableName+" SET "+fieldDateName+" = date_format(str_to_date("+fieldDateName+",'%e/%c/%Y'),'%d/%m/%Y') WHERE "+fieldDateName+" <> '?'";
		// Format date dạng mm/dd/YYYY sang dd/mm/YYYY
		String regExFormDate3 = "^(1[0-2]|0[1-9])/(3[0-1]|[1-2][0-9]|0[1-9])/[0-9]{4}$";
		Pattern pattern3 = Pattern.compile(regExFormDate3);
		String sqlFormDate3 = "UPDATE "+fullTableName+" SET "+fieldDateName+" = date_format(str_to_date("+fieldDateName+",'%m/%d/%Y'),'%d/%m/%Y') WHERE "+fieldDateName+" <> '?'";
		
		// 
		String sqlCheckDate = "SELECT "+fieldDateName+" FROM "+fullTableName+" LIMIT 1";
		PreparedStatement ps = connection.prepareStatement(sqlCheckDate);
		ResultSet rs = ps.executeQuery();
		String currDate = "";
		while (rs.next()) {
			currDate = rs.getString(fieldDateName);
			System.out.println("Date format: " + currDate);
		}
		Matcher matcher1 = pattern1.matcher(currDate);
		boolean form1 = matcher1.matches();
		Matcher matcher2 = pattern2.matcher(currDate);
		boolean form2 = matcher2.matches();
		Matcher matcher3 = pattern3.matcher(currDate);
		boolean form3 = matcher3.matches();
		
		if (form1 == true) {
			ps.executeUpdate(sqlFormDate1);
			System.out.println("Formated Date In Field: " + fieldDateName);
		}
		else if (form2 == true) {
			ps.executeUpdate(sqlFormDate2);
			System.out.println("Formated Date In Field: " + fieldDateName);
		}
		else if (form3 == true) {
			ps.executeUpdate(sqlFormDate3);
			System.out.println("Formated Date In Field: " + fieldDateName);
		}
		else {
			System.out.println("Field Date In This Data File Doesn't Need To Format");
		}
	}
	public static void main(String[] args) throws SQLException {
//		InfoConfig infoConfig = DBControlTool.getInfoConfig(7);
//		String dbName = "db_staging_etl";
//		String dataObject = infoConfig.getDataObject();
//		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbName);
////		transformData(connection, dbName, dataObject);
//		
//		String sql = "SELECT ngay_sinh FROM db_staging_etl.tb_wh_temp_sinhvien LIMIT 1";
//		PreparedStatement ps = connection.prepareStatement(sql);
//		ResultSet rs = ps.executeQuery();
//		while (rs.next()) {
//			System.out.println(rs.getString("ngay_sinh"));
//		}
		
//		connection.close();
		
		String regExFormDate1 = "^[0-9]{4}-(1[0-2]|0[1-9])-(3[0-1]|[1-2][0-9]|0[1-9])$";
		Pattern pattern1 = Pattern.compile(regExFormDate1);
		Matcher matcher1 = pattern1.matcher("1999-06-05");
		boolean form1 = matcher1.matches();
		System.out.println(form1);
	}
}
