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
import tool.ConvertCsvToTxt;
import tool.ConvertExcelToTxt;
import tool.FormatDelimiterFileTxt;
import tool.MoveFile;
import tool.TruncateTable;
import transform.TransformData;
import warehouse.LoadDataToWarehouse;
import log.LogStatus;
import log.LogUtil;

public class MainProcess {

	public static void loadDataWithConfigID(int idConfig) {
		// Lấy thông tin Config thông qua idConfig
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		String dataObject = infoConfig.getDataObject();
		String dbStagingName = infoConfig.getDbStagingName();
		String dbWarehouseName = infoConfig.getDbWarehouseName();
		String fileSuccessDir = infoConfig.getFileSuccessDir();
		String fileFailDir = infoConfig.getFileFailDir();
		String fieldName = infoConfig.getFieldName();
		String exclusiveField = infoConfig.getExclusiveField(); // trường duy nhất để phân biệt đối tượng
		String fieldDelimiter = infoConfig.getFieldDelimiter();
		String dateExpired = infoConfig.getDtExpired();
		String tbStagingName = "tb_staging_" + dataObject;
		String tbWarehouseTempName = "tb_wh_temp_" + dataObject;
		// ContentError ghi lại lỗi trong quá trình thực hiện process
		StringBuffer contentError = new StringBuffer();
		contentError.append("[idConfig = " + idConfig + "]\n--------------------\n");
		// Kết nối tới Database Staging
		Connection connectionStaging = MySQLConnectionUtils.getConnection(infoConfig, dbStagingName);
		PreparedStatement ps;
		// Bắt đầu load data
		System.out.println("\n[EXECUTE ETL PROCESS] [dataObject = " + dataObject + "] [idConfig = " + idConfig + "]\n");
		System.out.println("<p1> [Load Data Into Staging]");
		// Lấy thông tin danh sách DataFile từ Log với trạng thái ER
		ArrayList<Log> listLog = LogUtil.getListLog(idConfig);
		// Kiểm tra danh sách rỗng
		if (listLog.size() > 0) {
			for (Log fileData : listLog) { // Lấy thông tin của từng File
				System.out.println("-----------------------------");
				int idLog = fileData.getIdLog();
				String fileLocalPath = fileData.getFileLocalPath();
				String dataFileName = fileData.getFileName();
				String dataFileType = fileData.getFileType();

				String fileLocalFullPath = fileLocalPath + "/" + dataFileName + dataFileType;

				// Thư mục đường dẫn chứa File đã load thành công hoặc thất bại
				String successDir = fileSuccessDir + "/" + dataFileName + dataFileType;
				String failDir = fileFailDir + "/" + dataFileName + dataFileType;
				
				int dataLinesInTable = 0;

				System.out.println("[Begin Load Data File] [idLog = " + idLog + "] " + dataFileName + dataFileType);
				// Đường dẫn chính thức sử dụng để Load
				String currentFile = "";
				// Load data với các định dạng khác nhau sử dụng SWITCH CASE
				switch (dataFileType) {
				case ".txt":
					String txtFilePathTC = "D:/MySQLFile/txtConvert/" + dataFileName + "_tc_" + idLog + ".txt";
					try {
						FormatDelimiterFileTxt.formatFileTxt(fileLocalFullPath, txtFilePathTC, fieldDelimiter);
					} catch (IOException e) {
						System.out.println("<---> ERROR [Format txt_file with delimiter ; ]: " + e.getMessage());
						contentError.append("[idLog = " + idLog + "] [Format txt_file with delimiter ; ]: "
								+ e.getMessage() + "\n");
						UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF); //
						MoveFile.moveFile(fileLocalFullPath, failDir);
						// trỏ đến file kế tiếp
						continue;
					}
					// Gán currentFile bằng file text đã được Format
					currentFile = txtFilePathTC;
					break;
				case ".csv":
					String txtFilePathCC = "D:/MySQLFile/txtConvert/" + dataFileName + "_cc_" + idLog + ".txt";
					
					String copyCsvPath = fileLocalPath+"/"+dataFileName+"_copy"+dataFileType;
					String txtPath = fileLocalPath+"/"+dataFileName+"_txt_converted"+dataFileType;
					try {
						ConvertCsvToTxt.convertCsvToTxt(fileLocalFullPath, copyCsvPath, txtPath, txtFilePathCC, ";");
					} catch (IOException e) {
						System.out.println("<---> ERROR [Format csv_file with delimiter ; ]: " + e.getMessage());
						contentError.append("[idLog = " + idLog + "] [Format csv_file with delimiter ; ]: "
								+ e.getMessage() + "\n");
						UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF); //
						MoveFile.moveFile(fileLocalFullPath, failDir);
						// trỏ đến file kế tiếp
						continue;
					}
					currentFile = txtFilePathCC;
					break;
				case ".xls":
				case ".xlsx":
					String txtFilePath = "D:/MySQLFile/txtConvert/" + dataFileName + "_ec_" + idLog + ".txt";
					try {
						ConvertExcelToTxt.convertExcelToTxt(fileLocalFullPath, txtFilePath, fieldDelimiter);
					} catch (IOException | NullPointerException e) {
						System.out.println("<---> ERROR [Convert excel_file to txt_file]: " + e.getMessage());
						contentError.append(
								"[idLog = " + idLog + "] [Convert excel_file to txt_file]: " + e.getMessage() + "\n");
						UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF); //
						MoveFile.moveFile(fileLocalFullPath, failDir);
						// trỏ đến file kế tiếp
						continue;
					}
					// Gán currentFile bằng file excel đã được convert
					currentFile = txtFilePath;
					break;
				default:
					// Thông báo lỗi với định dạng khác
					System.out.println(
							"<---> ERROR [Load data into Staging] Cannot load with this fileType: " + dataFileType);
					contentError
							.append("[idLog = " + idLog + "] [Cannot load with this fileType]: " + dataFileType + "\n");
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}

				/*
				 * Load data in file with current file path
				 */
				
				// Đếm số dòng dữ liệu từ File gốc (currentFile)
				int dataLinesInFile = CheckData.numberDataLinesIgnoreFirst(currentFile);
				// Load dữ liệu trực tiếp từ currentFile vào table Staging và các trường tương ứng
				String sql = "LOAD DATA INFILE '" + currentFile + "' " + "INTO TABLE " + tbStagingName
						+ " CHARACTER SET 'utf8' " + "FIELDS TERMINATED BY '" + fieldDelimiter + "' "
						+ "ENCLOSED BY '\"' " + "LINES TERMINATED BY '\\r\\n' " + "IGNORE 1 LINES " + "(" + fieldName
						+ ");";
				try {
					ps = connectionStaging.prepareStatement(sql);
					ps.executeUpdate();

					// Đếm số dòng dữ liệu sau khi extract (từ table Staging tương ứng)
					dataLinesInTable = CheckData.numberDataLinesInTable(connectionStaging, dbStagingName,
							tbStagingName);
					System.out.println("[Data lines after extract to Staging]: "
							+ dataLinesInTable);
					// CheckSum - so sánh, đếm dữ liệu có bị thiếu hay không
					if (dataLinesInTable == dataLinesInFile) {
						// Nếu đủ thì tiến hành Transform dữ liệu và load data từ table Staging vào table warehouse tạm tương ứng
//						UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.TR);
						// Transform
						try {
							TransformData.transformData(connectionStaging, dbStagingName, dataObject);
						}
						catch (SQLException e) {
							System.out.println("<---> ERROR [Transform Data]: " + e.getMessage());
							contentError.append("[idLog = " + idLog + "] [Transform Data] [" + dataFileName + dataFileType + "]: " + e.getMessage() + "\n");
//							UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.TF);
						}
						// Load dữ liệu vào Warehouse tạm
						LoadDataToWarehouseTemp.loadDataToWarehouseTemp(connectionStaging, dbStagingName, fieldName,
								tbStagingName, tbWarehouseTempName, dateExpired);
						
						System.out.println("Load Data File Success!");
						MoveFile.moveFile(fileLocalFullPath, successDir);
					} else {
						// Nếu data bị thiếu thì Truncate table Staging, trỏ đến file kế tiếp
						TruncateTable.truncateTable(connectionStaging, dbStagingName, tbStagingName);
						System.out.println(
								"<---> ERROR [Missing data after extract to staging]: " + dataFileName + dataFileType);
						contentError.append("[idLog = " + idLog + "] [Missing data after extract to staging]: "
								+ dataFileName + dataFileType + "\n");
						UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
						MoveFile.moveFile(fileLocalFullPath, failDir);
						continue;
					}
				} catch (SQLException e) {
					// Lỗi extract dữ liệu vào Staging, truncate Staging và trỏ đến file kế tiếp
					TruncateTable.truncateTable(connectionStaging, dbStagingName, tbStagingName);
					System.out.println("<---> ERROR [Load data into Staging] [Data file: " + dataFileName + dataFileType
							+ "]: " + e.getMessage());
					contentError.append("[idLog = " + idLog + "] [Load data into Staging] [Data file: " + dataFileName
							+ dataFileType + "]: " + e.getMessage() + "\n");
					UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.EF);
					MoveFile.moveFile(fileLocalFullPath, failDir);
					continue;
				}
				// Sau khi extract thành công thì update trạng thái là SU
				UpdateLog.updateLog(idLog, dataLinesInTable, LogStatus.SU);
				// Truncate table Staging để xử lý file kế tiếp
				TruncateTable.truncateTable(connectionStaging, dbStagingName, tbStagingName);
			}
		} else {
			// Nếu trong danh sách không có thông tin dataFile nào
			System.out.println("No File Data Found!!");
			// Dừng tiến trình
			System.exit(0);
		}
		// Đóng kết nối với Database Staging
		try {
			connectionStaging.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Close connection]: " + e.getMessage());
		}

		// Bắt đầu load dữ liệu sang Database Warehouse
		System.out.println("\n<p2> [Load Data Into Warehouse]\n-------------------------------");
		// Thêm trường s_key và dt_expired
		String fieldsInWH = "s_key," + fieldName + ",date_expired";
		String tarTableWH = "tb_wh_" + dataObject;
		// Chuyển data từ Table Warehouse Temp sang Table Warehouse chính thức
		LoadDataToWarehouse.loadDataToWarehouse(infoConfig, dbStagingName, dbWarehouseName, tbWarehouseTempName,
				tarTableWH, fieldsInWH, exclusiveField);
		
		// Update flag sau khi thực hiện process cho mỗi config
		DBControlTool.updateConfigFlag(idConfig, "done");
		
		// Gửi thông báo lỗi vào email
		System.out.println("[Send ERROR message..]");
		SendMail.sendMail(contentError.toString());
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

	}
}
