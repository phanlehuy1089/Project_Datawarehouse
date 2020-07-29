package tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//import control.DBControlTool;
//import model.InfoConfig;

public class CreateTable {
	public static void createTable(Connection connection, String dbName, String tbName, String fieldName, String fieldFormat) {
		
//		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbName);
		
		String[] fieldNameArr = fieldName.split(",");
		String[] fieldFormatArr = fieldFormat.split(",");
		
		// create field
		String field = "";
		for (int i = 0; i < fieldNameArr.length; i++) {
			field += fieldNameArr[i] + " " + fieldFormatArr[i] + ", ";
		}
		// create query create table
		String sql = "CREATE TABLE IF NOT EXISTS " + dbName + "." + tbName + " (" + field + "PRIMARY KEY(" + fieldNameArr[0] +"))";		
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Create table] " + e.getMessage());
		}
	}
	public static void main(String[] args) {
//		InfoConfig infoConfig = DBControlTool.getInfoConfig(6);
//		String dbName = infoConfig.getDbStagingName();
//		String fieldName = infoConfig.getFieldName();
//		String fieldFormat = infoConfig.getFieldFormat();
//		createTable(connection ,dbName, "haha", fieldName, fieldFormat);
	}
}
