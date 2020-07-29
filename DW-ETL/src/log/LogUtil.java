package log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import control.ConnectDBControlUtils;
import mail.SendMail;
import model.Log;

public class LogUtil {
	public static ArrayList<Log> getListLog(int idConfig) {
		ArrayList<Log> listLog = new ArrayList<Log>();
		Connection connection = null;
		try {
			connection = ConnectDBControlUtils.getDBControlConnection();
			String sql = "SELECT * FROM tb_log WHERE id_config = ? AND file_status = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, idConfig);
			ps.setString(2, "ER");
			ResultSet rSet = ps.executeQuery();
			while (rSet.next()) {
				Log log = new Log();
				log.setIdLog(rSet.getInt("id_log"));
				log.setIdConfig(rSet.getInt("id_config"));
				log.setFileLocalPath(rSet.getString("file_local_path"));
				log.setFileName(rSet.getString("file_name"));
				log.setFileType(rSet.getString("file_type"));
				log.setFileDelimiter(rSet.getString("file_delimiter"));
				log.setNumberOfRecords(rSet.getInt("number_of_records"));
				log.setLogTimestamp(rSet.getString("log_timestamp"));
				log.setFileStatus(rSet.getString("file_status"));
				listLog.add(log);
			}
			connection.close();
		}
		catch (SQLException e) {
			System.out.println("<---> ERROR [Get information from table Log]: " + e.getMessage());
			SendMail.sendMail("<---> ERROR [Get information from table Log]: " + e.getMessage());
		}
		return listLog;
	}
	public static void main(String[] args) {
		ArrayList<Log> listLog = LogUtil.getListLog(6);
		for (Log l : listLog) {
			System.out.println(l);
		}
	}
}
