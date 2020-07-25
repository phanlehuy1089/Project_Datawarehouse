package check;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import model.InfoConfig;

public class CheckDuplicate {
	public static void mergeDataDuplicate(InfoConfig infoConfig, String dbName, String tbName, String fieldNameGroup) {
		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbName);
		String sql = "SELECT * FROM " + dbName + "." + tbName + " WHERE " + fieldNameGroup + " <> 0 GROUP BY " + fieldNameGroup;
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeQuery();
			
			
		} catch (SQLException e) {
			
		}
		
	}
	public static void main(String[] args) {
		String s = "|";
		System.out.println(s);
	}
}
