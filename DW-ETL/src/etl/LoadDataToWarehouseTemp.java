package etl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection_utils.MySQLConnectionUtils;
import model.InfoConfig;

public class LoadDataToWarehouseTemp {
	public static void loadDataToWarehouseTemp(String dbName, InfoConfig infoConfig, String fieldName, String sourceTable, String targetTable) {
		String dtExpired = infoConfig.getDtExpired();
		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbName);
		String sql = "INSERT INTO " + targetTable + " (s_key," + fieldName + ",date_expired) "
				+ "SELECT 0," + fieldName + ",'" + dtExpired + "' FROM " + sourceTable;
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Load data to Warehouse temp]: " + e.getMessage());
		}
	}
}