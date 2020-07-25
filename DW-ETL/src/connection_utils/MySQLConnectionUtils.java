package connection_utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import control.DBControlTool;
import model.InfoConfig;

public class MySQLConnectionUtils {
	public static Connection getConnection(InfoConfig infoConfig, String dbName) {
		String driverPathName = infoConfig.getClassForName();
		String portName = infoConfig.getPortName();
		int portNumber = infoConfig.getPortNumber();
		String hostName = infoConfig.getHostName();
		String userName = infoConfig.getUserName();
		String password = infoConfig.getPassword();
		Connection connection = null;
		try {
			Class.forName(driverPathName);
			String url = portName + "://" + hostName + ":" + portNumber + "/" + dbName;
			connection = DriverManager.getConnection(url, userName, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("<---> ERROR [Connect to database] [" + dbName + "]: " + e);
		}	
		return connection;
	}
	public static void main(String[] args) {
		InfoConfig infoConfig = DBControlTool.getInfoConfig(6);
		System.out.println(MySQLConnectionUtils.getConnection(infoConfig, "db_staging_etl"));
	}
	
}
