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
				infoConfig.setFileSuccessDir(rs.getString("file_success_dir"));
				infoConfig.setFileFailDir(rs.getString("file_fail_dir"));
				infoConfig.setDtExpired(rs.getString("dt_expired"));
			}
			connection.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Get configuration information]: " + e.getMessage());
		}
		return infoConfig;
	}

	public static void main(String[] args) {
		System.out.println(DBControlTool.getInfoConfig(6));
	}
}
