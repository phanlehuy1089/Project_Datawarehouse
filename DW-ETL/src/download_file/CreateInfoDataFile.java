package download_file;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import log.LogStatus;
import model.InfoConfig;

public class CreateInfoDataFile {
	
	public static ArrayList<String> listFileName(InfoConfig infoConfig) {
		ArrayList<String> listFileName = new ArrayList<String>();
		String localDir = infoConfig.getLocalDirectory();
		File dir = new File(localDir);
		File[] listFile = dir.listFiles();
		for (File file : listFile) {
			listFileName.add(file.getName());
		}
		return listFileName;
	}
	
	public static void insertInfoFileToTableLog(int idConfig) {
		
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		
		String localDir = infoConfig.getLocalDirectory();
//		idConfig = infoConfig.getIdConfig();
		
		ArrayList<String> listFileInfo = CreateInfoDataFile.listFileName(infoConfig);
		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, "db_control_etl");
		String sql = "INSERT INTO tb_log VALUES(?, ?, ?, ?, ?, ?, ?, current_timestamp(), ?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			for (String fileInfo : listFileInfo) {
				
				File file = new File(localDir + "/" + fileInfo);
				
				String loadStatus;
				String[] fileArr = fileInfo.split("\\."); // chia [filename][filetype]
				ps.setInt(1, 0);
				ps.setInt(2, idConfig);
				ps.setString(3, localDir);
				ps.setString(4, fileArr[0]);
				ps.setString(5, "." + fileArr[1]);
				ps.setString(6, ";");
				ps.setInt(7, 0);
				if (file.exists()) {
					loadStatus = LogStatus.ER;
				}
				else {
					loadStatus = LogStatus.DF; // download fail
				}
				ps.setString(8, loadStatus);
				ps.executeUpdate();
			}
			System.out.println("[Get list file data information from folder: [" + localDir + "] insert into table Log]");
			connection.close();
		} catch (SQLException e) {
			
			System.out.println("<---> ERROR [Get list file data information from folder: " + localDir + "]: " + e.getMessage());
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
	    insertInfoFileToTableLog(6);
		
		
//	     System.out.println(fileArr[1]);
//	     System.out.println(fileArr[2]);
		
//	        System.out.println("String[] list():\n");
//	 
//	        String[] paths = dir.list();
//	 
//	        for (String path : paths) {
//	            System.out.println(path);
//	        }
	}
}
