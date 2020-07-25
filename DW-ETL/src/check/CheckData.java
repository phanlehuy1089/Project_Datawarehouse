package check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connection_utils.MySQLConnectionUtils;
import model.InfoConfig;

public class CheckData {
	public static int numberDataLinesIgnoreFirst(String filePath) {
		int dataLines = 0;
		BufferedReader br = null;
		try {
			File file = new File(filePath);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String contentLine = null;
			while ((contentLine = br.readLine()) != null) {
				dataLines++;
			}
			br.close();
		} catch (IOException e) {
			System.out.println("<---> ERROR [Check number of data lines before extract to Staging]: " + e.getMessage());
		}
		return dataLines - 1;
	}

	public static int numberDataLinesInTable(InfoConfig infoConfig, String dbName, String tbName) {
		int dataLinesInTable = 0;
		try {
			Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbName);
			Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT * FROM " + tbName;
			ResultSet rSet = s.executeQuery(sql);
			rSet.last();
			dataLinesInTable = rSet.getRow();
			rSet.beforeFirst();
			connection.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Check number of data lines after extract to Staging]: " + e.getMessage());
		}
		return dataLinesInTable;
	}

	public static boolean checkSum(InfoConfig infoConfig, String dbName, String tbName, String filePath) {
		boolean result = false;
		int dataLinesInFile = CheckData.numberDataLinesIgnoreFirst(filePath);
		int dataLinesInTable = CheckData.numberDataLinesInTable(infoConfig, dbName, tbName);
		if (dataLinesInTable == dataLinesInFile) {
			System.out.println("[Number of data lines after extract to Staging]: " + dataLinesInTable);
			System.out.println("[Check sum] = true");
			result = true;
		} else {
			System.out.println("<---> ERROR [Check sum] Number of data lines after extract to Staging is missing!");
			System.out.println("<---> ERROR [Check sum] = false");
		}
		return result;
	}

}
