package log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import control.ConnectDBControlUtils;
import model.Log;

public class UpdateLog {
	
	public static void updateLog(int idLog, int numberOfRecords, String status) {
		try {
			Connection conn = ConnectDBControlUtils.getDBControlConnection();
			String sqlUpdateLog = "UPDATE tb_log SET number_of_records = " + numberOfRecords + ", log_timestamp = current_timestamp(), file_status = '" + status + "' WHERE id_log = " + idLog + "";
			PreparedStatement ps = conn.prepareStatement(sqlUpdateLog);
			ps.executeUpdate();
			conn.close();
		}
		catch (SQLException e) {
			System.out.println("ERROR [Update Log]: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		
	}
}

