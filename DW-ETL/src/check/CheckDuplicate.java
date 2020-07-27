package check;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import model.InfoConfig;

public class CheckDuplicate {
	public static void mergeDataDuplicate(InfoConfig infoConfig, String tbName, String fileName) {
		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, "db_control_etl");
		String sql = "DELETE l1 FROM "+tbName+" l1\r\n" + 
				"INNER JOIN "+tbName+" l2\r\n" + 
				"WHERE\r\n" + 
				"    l1.id_log > l2.id_log AND \r\n" + 
				"    l1."+fileName+" = l2."+fileName+" AND\r\n" + 
				"    l1.file_type = l2.file_type;";
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.execute();
				System.out.println("update");
				connection.close();
			} catch (SQLException e) {
				System.out.println("<---> ERROR [DOWNLOAD FILE] " + e.getMessage());
			}
		
	}
//	public static void main(String[] args) {
//		CheckDuplicate.mergeDataDuplicate(infoConfig, tbName, fileName);
//	}
}
