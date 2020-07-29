package etl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import check.CheckData;
import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import log.UpdateLog;
import mail.SendMail;
import model.InfoConfig;
import model.Log;
import tool.ConvertExcelToTxt;
import tool.FormatDelimiterFileTxt;
import tool.MoveFile;
import tool.TruncateTable;
import warehouse.LoadDataToWarehouse;
import log.LogStatus;
import log.LogUtil;

public class MainProcess {

	public static void loadDataWithConfigID(int idConfig) {
		// Get configuration information
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		String dataObject = infoConfig.getDataObject();
		String dbStagingName = infoConfig.getDbStagingName();
		String dbWarehouseName = infoConfig.getDbWarehouseName();
		String fileSuccessDir = infoConfig.getFileSuccessDir();
		String fileFailDir = infoConfig.getFileFailDir();
		String fieldName = infoConfig.getFieldName();
		String exclusiveField = infoConfig.getExclusiveField();
		String fieldDelimiter = infoConfig.getFieldDelimiter();
		String dateExpired = infoConfig.getDtExpired();
		String tbStagingName = "tb_staging_" + dataObject;
		String tbWarehouseTempName = "tb_wh_temp_" + dataObject;
		// Note errorLogs
		StringBuffer contentError = new StringBuffer();
		contentError.append("[idConfig = " + idConfig + "]\n--------------------\n");
		// Get connection with databaseStaging
		Connection connectionStaging = MySQLConnectionUtils.getConnection(infoConfig, dbStagingName);
		PreparedStatement ps;
		// Start process
		System.out.println("\n\n[EXECUTE ETL PROCESS] [dataObject = " + dataObject + "] [idConfig = " + idConfig + "]\n");
		System.out.println("<p1> [Begin load data into Staging]...");
		System.out.println("[Get information dataFiles]\n");
		ArrayList<Log> listLog = LogUtil.getListLog(idConfig);
		for (Log fileData : listLog) { // Get each file information from Log
			System.out.println("-----------------------------------------------------------------");
			int idLog = fileData.getIdLog();
			String fileLocalPath = fileData.getFileLocalPath();
			String dataFileName = fileData.getFileName();
			String dataFileType = fileData.getFileType();

			String fileLocalFullPath = fileLocalPath + "/" + dataFileName + dataFileType;

			// Create file path for failFile or successFile
			String successDir = fileSuccessDir + "/" + dataFileName + dataFileType;
			String failDir = fileFailDir + "/" + dataFileName + dataFileType;
			
			int dataLinesInTable = 0;
			
			// Connect to database Staging
//			Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbStagingName);
//			PreparedStatement ps;
			
			// Load file with some fileTypes using SWITCH CASE
			System.out.println("[Begin load data file] [idLog = " + idLog + "] " + dataFileName + dataFileType);
			
			String currentFile = "";
			switch (dataFileType) {
			case ".txt":
				String txtFilePathTC = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/txtConvert/" + dataFileName + "_tc_" + idLog + ".txt";
				try {
					FormatDelimiterFileTxt.formatFileTxt(fileLocalFullPath, txtFilePathTC, fieldDelimiter);
				}
				catch (IOException e) {
					System.out.println("<---> ERROR [Format txt_file with delimiter ; ]: " + e.getMessage());
					contentError.append("[idLog = " + idLog + "] [Format txt_file with delimiter ; ]: " + e.getMessage() + "\n");
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF); //
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}
				currentFile = txtFilePathTC;
				break;
			case ".csv":
				currentFile = fileLocalFullPath;
				break;
			case ".xls":
			case ".xlsx":
				String txtFilePath = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/txtConvert/" + dataFileName + "_ec_" + idLog + ".txt";
				try {
					ConvertExcelToTxt.convertExcelToTxt(fileLocalFullPath, txtFilePath, fieldDelimiter);
				}
				catch (IOException | NullPointerException e) {
					System.out.println("<---> ERROR [Convert excel_file to txt_file]: " + e.getMessage());
					contentError.append("[idLog = " + idLog + "] [Convert excel_file to txt_file]: " + e.getMessage() + "\n");
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF); //
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}
				currentFile = txtFilePath;
				break;
			default:
				System.out.println("<---> ERROR [Load data into Staging] Cannot load with this fileType: " + dataFileType);
				contentError.append("[idLog = " + idLog + "] [Cannot load with this fileType]: " + dataFileType + "\n");
				UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
				MoveFile.moveFile(fileLocalFullPath, failDir);
				continue;
			}
			
			/*
			 Load data in file with current file path 
			 */
			
			int dataLinesInFile = CheckData.numberDataLinesIgnoreFirst(currentFile);
			
			String sql = "LOAD DATA INFILE '" + currentFile + "' " 
					+ "INTO TABLE " + tbStagingName
					+ " CHARACTER SET 'utf8' " 
					+ "FIELDS TERMINATED BY '" + fieldDelimiter + "' "
					+ "ENCLOSED BY '\"' " 
					+ "LINES TERMINATED BY '\\r\\n' " 
					+ "IGNORE 1 LINES " 
					+ "(" + fieldName + ");";
			try {
				ps = connectionStaging.prepareStatement(sql);
				ps.executeUpdate();
//				connection.close();

				// Count data lines after extract
				dataLinesInTable = CheckData.numberDataLinesInTable(connectionStaging, dbStagingName, tbStagingName);
				System.out.println("[Data lines after extract to Staging] [" + dataFileName + dataFileType + "]: " + dataLinesInTable);
				// Update log when load data success or fail
				if (dataLinesInTable == dataLinesInFile) {
					// Begin load data to Warehouse temp
					LoadDataToWarehouseTemp.loadDataToWarehouseTemp(connectionStaging, dbStagingName, fieldName, tbStagingName, tbWarehouseTempName, dateExpired);
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.TR);
					MoveFile.moveFile(fileLocalFullPath, successDir);
				} else {
					TruncateTable.truncateTable(connectionStaging, dbStagingName, tbStagingName);
					System.out.println("<---> ERROR [Missing data after extract to staging]: " + dataFileName + dataFileType);
					contentError.append("[idLog = " + idLog + "] [Missing data after extract to staging]: " + dataFileName + dataFileType + "\n");
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}
			} catch (SQLException e) {
				TruncateTable.truncateTable(connectionStaging, dbStagingName, tbStagingName);
				System.out.println("<---> ERROR [Load data into Staging] [Data file: " + dataFileName + dataFileType + "]: " + e.getMessage());
				contentError.append("[idLog = " + idLog + "] [Load data into Staging] [Data file: " + dataFileName + dataFileType + "]: " + e.getMessage() + "\n");
				UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
				MoveFile.moveFile(fileLocalFullPath, failDir);
				continue;
			}
			UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.SU);
			TruncateTable.truncateTable(connectionStaging, dbStagingName, tbStagingName);
		}
		// Close connection
		try {
			connectionStaging.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Close connection]: " + e.getMessage());
		}
		SendMail.sendMail(contentError.toString());
		System.out.println("\n<p2> [Begin load data into Warehouse...]");
		String fieldsInWH = "s_key," + fieldName + ",date_expired";
		String tarTableWH = "tb_wh_" + dataObject;	
		LoadDataToWarehouse.loadDataToWarehouse(infoConfig, dbStagingName, dbWarehouseName, tbWarehouseTempName, tarTableWH, fieldsInWH, exclusiveField);
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
	}
}
