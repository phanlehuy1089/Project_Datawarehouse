package tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//import connection_utils.MySQLConnectionUtils;
//import model.InfoConfig;

public class TruncateTable {
	public static void truncateTable(Connection connection, String dbName, String tbName) {
//		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbName);
		String truncateSql = "TRUNCATE TABLE " + tbName;
		try {
			PreparedStatement ps = connection.prepareStatement(truncateSql);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Truncate table] [database: " + dbName + " - table: " + tbName + "]: " + e.getMessage());
		}	
	}
}
