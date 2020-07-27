package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDBControlUtils {
	public static Connection getDBControlConnection() {
		String hostName = "localhost";
		String userName = "root";
		String password = "1089";
		String dbName = "db_control_etl";
		Connection connection = null;
		try {
			String url = "jdbc:mysql://" + hostName + ":3306/" + dbName;
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, userName, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("<---> ERROR [Connect to database Control]: " + e.getMessage());
		} 
		return connection;
	}
	
	public static void main(String[] args) {
		Connection connection = ConnectDBControlUtils.getDBControlConnection();
		System.out.println("Connection name: " + connection);	
	}
}
