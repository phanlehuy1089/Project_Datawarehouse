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
import log.LogStatus;
import log.LogUtil;

public class LoadFileToStaging {

	public static void loadDataToStaging(int idConfig) {
		// Lấy thông tin config
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		String dataObject = infoConfig.getData_object();
		String dbStagingName = infoConfig.getDbStagingName();
		String fileSuccessDir = infoConfig.getFileSuccessDir();
		String fileFailDir = infoConfig.getFileFailDir();
		String fieldName = infoConfig.getFieldName();
//		String fieldFormat = infoConfig.getFieldFormat();

		System.out.println("[LOAD DATA INTO STAGING]: tb_staging_" + dataObject + "\n--------------------------------------");
		ArrayList<Log> listLog = LogUtil.getListLog(idConfig);
		for (Log fileData : listLog) { // Lấy thông tin từng file trong Log
			System.out.println("--------------------------------------");
			int idLog = fileData.getIdLog();
			String fileLocalPath = fileData.getFileLocalPath();
			String dataFileName = fileData.getFileName();
			String dataFileType = fileData.getFileType();
			String dataFileDelimiter = fileData.getFileDelimiter();

			String fileLocalFullPath = fileLocalPath + "/" + dataFileName + dataFileType;

			// Tạo 2 đường dẫn để di chuyển file sau khi extract thành công hoặc thất bại
			String successDir = fileSuccessDir + "/" + dataFileName + dataFileType;
			String failDir = fileFailDir + "/" + dataFileName + dataFileType;

			int dataLinesInTable = 0;
			
			// Kết nối với Database Staging
			Connection connection = MySQLConnectionUtils.getConnection(infoConfig, dbStagingName);
			PreparedStatement ps;
			
			// Load file data với các định dạng khác nhau sử dụng switch case
			System.out.println("[Begin load data file] [idLog = " + idLog + "] " + dataFileName + dataFileType);
			
			String currentFile = "";
			switch (dataFileType) {
			case ".txt":
				String txtFilePathTC = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/txtConvert/" + dataFileName + "_tc_" + idLog + ".txt";
				try {
					FormatDelimiterFileTxt.formatFileTxt(fileLocalFullPath, txtFilePathTC, ";");
				}
				catch (IOException e) {
					System.out.println("<---> ERROR [Format txt_file with delimiter ; ]: " + e.getMessage());
					SendMail.sendMail("[idLog = " + idLog + "] [Format txt_file with delimiter ; ]: " + e.getMessage());
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
					ConvertExcelToTxt.convertExcelToTxt(fileLocalFullPath, txtFilePath, ";");
				}
				catch (IOException | NullPointerException e) {
					System.out.println("<---> ERROR [Convert excel_file to txt_file]: " + e.getMessage());
					SendMail.sendMail("[idLog = " + idLog + "] [Convert excel_file to txt_file]: " + e.getMessage());
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF); //
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}
				currentFile = txtFilePath;
				break;
			default:
				System.out.println(
						"<---> ERROR [Load data into Staging] Không thể load file với định dạng này: " + dataFileType);
				SendMail.sendMail("[idLog = " + idLog + "] [Không thể load file với định dạng này]: " + dataFileType);
				UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
				MoveFile.moveFile(fileLocalFullPath, failDir);
				continue;
			}
			
			/*
			 Load data in file with current file path 
			 */
			
			String sql = "LOAD DATA INFILE '" + currentFile + "' " 
					+ "INTO TABLE tb_staging_" + dataObject
					+ " CHARACTER SET 'utf8' " 
					+ "FIELDS TERMINATED BY '" + dataFileDelimiter + "' "
					+ "ENCLOSED BY '\"' " 
					+ "LINES TERMINATED BY '\\r\\n' " 
					+ "IGNORE 1 LINES " 
					+ "(" + fieldName + ");";
			try {
				ps = connection.prepareStatement(sql);
				ps.executeUpdate();
				connection.close();

				// Đếm số dòng data sau khi Extract
				dataLinesInTable = CheckData.numberDataLinesInTable(infoConfig, dbStagingName, "tb_staging_" + dataObject);
				System.out.println("[Data lines after extract to Staging] [" + dataFileName + dataFileType + "]: " + dataLinesInTable);
				// Update log khi load data vào staging thành công hoặc thất bại
				if (dataLinesInTable >= 20) {
					
					LoadDataToWarehouseTemp.loadDataToWarehouseTemp(dbStagingName, infoConfig, fieldName, "tb_staging_" + dataObject, "tb_wh_temp_" + dataObject);

					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.TR);
					MoveFile.moveFile(fileLocalFullPath, successDir);
				} else {
					TruncateTable.truncateTable(infoConfig, dbStagingName, "tb_staging_" + dataObject);
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
					SendMail.sendMail("[idLog = " + idLog + "] [Lost data]: " + dataFileName + dataFileType);
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}
			} catch (SQLException e) {
				TruncateTable.truncateTable(infoConfig, dbStagingName, "tb_staging_" + dataObject);
				UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
				System.out.println("<---> ERROR [Load data into Staging] [Data file: " + dataFileName + dataFileType + "]: " + e.getMessage());
				SendMail.sendMail("[idLog = " + idLog + "] [Load data into Staging] [Data file: " + dataFileName + dataFileType + "]: " + e.getMessage());
				MoveFile.moveFile(fileLocalFullPath, failDir);
				continue;
			}
			TruncateTable.truncateTable(infoConfig, dbStagingName, "tb_staging_" + dataObject);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
	}
}
