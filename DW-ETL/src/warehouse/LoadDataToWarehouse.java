package warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection_utils.MySQLConnectionUtils;
import mail.SendMail;
import model.InfoConfig;

public class LoadDataToWarehouse {
	// Chuyển data từ table Warehouse tạm sang table Warehouse chính thức tương ứng
	public static void loadDataToWarehouse(InfoConfig infoConfig, String srcDatabase, String tarDatabase, String srcTable, String tarTable, String fields, String exclusiveField) {
		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, tarDatabase);
		String sql = "INSERT INTO " + tarDatabase + "." + tarTable + " (" + fields + ",date_last_change) SELECT " + fields + ",current_timestamp() FROM " + srcDatabase + "." + srcTable + " WHERE " + exclusiveField + " <> '0' GROUP BY " + exclusiveField;
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeUpdate();
			connection.close();
			System.out.println("Loaded Data Into Warehouse\n");
		} catch (SQLException e) {
			SendMail.sendMail("<---> ERROR [Load data to Warehouse] " + "[" + srcDatabase + "." + srcTable + " --> " + tarDatabase + "." + tarTable + "]: " + e.getMessage());
			System.out.println("<---> ERROR [Load data to Warehouse]: " + e.getMessage());
		}
	}
}
