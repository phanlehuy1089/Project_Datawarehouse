package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.InfoConfig;

public class DBControlTool {
	public static InfoConfig getInfoConfig(int idConfig) {
		InfoConfig infoConfig = new InfoConfig();
		Connection connection = null;
		try {
			connection = ConnectDBControlUtils.getDBControlConnection();
			String sql = "SELECT * FROM tb_config WHERE id_config = " + idConfig;
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				infoConfig.setIdConfig(rs.getInt("id_config"));
				infoConfig.setMethodGetData(rs.getString("method_get_data"));
				infoConfig.setNasHostName(rs.getString("nas_host_name"));
				infoConfig.setNasPortNumber(rs.getInt("nas_port_number"));
				infoConfig.setNasUserName(rs.getString("nas_user_name"));
				infoConfig.setNasPassword(rs.getString("nas_password"));
				infoConfig.setNasDirectory(rs.getString("nas_directory"));
				infoConfig.setLocalDirectory(rs.getString("local_directory"));
				infoConfig.setClassForName(rs.getString("class_forname"));
				infoConfig.setPortName(rs.getString("port_name"));
				infoConfig.setPortNumber(rs.getInt("port_number"));
				infoConfig.setHostName(rs.getString("host_name"));
				infoConfig.setUserName(rs.getString("user_name"));
				infoConfig.setPassword(rs.getString("password"));
				infoConfig.setDbStagingName(rs.getString("db_staging_name"));
				infoConfig.setDbWarehouseName(rs.getString("db_warehouse_name"));
				infoConfig.setDataObject(rs.getString("data_object"));
				infoConfig.setFieldName(rs.getString("field_name"));
				infoConfig.setFieldFormat(rs.getString("field_format"));
				infoConfig.setExclusiveField(rs.getString("exclusive_field"));
				infoConfig.setFieldDelimiter(rs.getString("field_delimiter"));
				infoConfig.setFileSuccessDir(rs.getString("file_success_dir"));
				infoConfig.setFileFailDir(rs.getString("file_fail_dir"));
				infoConfig.setDtExpired(rs.getString("dt_expired"));
				infoConfig.setFlag(rs.getString("flag"));
			}
			connection.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Get configuration information]: " + e.getMessage());
		}
		return infoConfig;
	}
	
	public static void updateConfigFlag(int idConfig, String flag) {
		String sql = "UPDATE tb_config SET flag = '"+flag+"' WHERE id_config = " + idConfig;
		Connection connection = ConnectDBControlUtils.getDBControlConnection();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Update Config Flag]: " + e.getMessage());
		}
	}
	
	public static boolean checkFlag(String methodGetData, String flag) {
		boolean result = false;
		String sql = "SELECT * FROM tb_config WHERE method_get_data = '"+methodGetData+"' AND flag = '"+flag+"' AND (data_object = 'dangky' OR data_object = 'sinhvien' OR data_object = 'lophoc')";
		Connection connection = ConnectDBControlUtils.getDBControlConnection();
		PreparedStatement ps;
		int count = 0;
		try {
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				count++;
			}
			connection.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Check Config Flag]: " + e.getMessage());
		}
		if (count == 3) {
			result = true;
		}		
		return result;
	}
	
	public static void main(String[] args) {
//		System.out.println(DBControlTool.getInfoConfig(1));
		System.out.println(checkFlag("local", "done"));
	}
}
