package download_file;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import check.CheckDuplicate;
import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import log.LogStatus;
import model.InfoConfig;

public class CreateInfoDataFile {
	// Liệt kê danh sách file từ Local Directory
	public static ArrayList<String> listFileName(InfoConfig infoConfig) {
		// Tạo danh sách chứa tên file (bao gồm định dạng)
		ArrayList<String> listFileName = new ArrayList<String>();
		String localDir = infoConfig.getLocalDirectory();
		File dir = new File(localDir);
		File[] listFile = dir.listFiles();
		for (File file : listFile) {
			listFileName.add(file.getName());
		}
		return listFileName;
	}
	
	// Insert danh sách thông tin file vào Log với idConfig tương ứng
	public static void insertInfoFileToTableLog(int idConfig) {

		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		String fieldDelimiter = infoConfig.getFieldDelimiter();
		
		String localDir = infoConfig.getLocalDirectory();
		
		// Lấy danh sách tên file
		ArrayList<String> listFileInfo = CreateInfoDataFile.listFileName(infoConfig);
		// Kết nối tới Database Control
		Connection connection = MySQLConnectionUtils.getConnection(infoConfig, "db_control_etl");
		// Insert vào Log và ghi lại thời gian hiện tại (current_time)
		String sql = "INSERT INTO tb_log VALUES(?, ?, ?, ?, ?, ?, ?, current_timestamp(), ?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			for (String fileInfo : listFileInfo) {
				// Ghép đường dẫn và tên file
				File file = new File(localDir + "/" + fileInfo);

				String loadStatus;
				String[] fileArr = fileInfo.split("\\."); // split [fileName].[fileType]
				ps.setInt(1, 0); // idLog auto increment
				ps.setInt(2, idConfig);
				ps.setString(3, localDir);
				ps.setString(4, fileArr[0]);
				ps.setString(5, "." + fileArr[1]);
				ps.setString(6, fieldDelimiter);
				ps.setInt(7, 0);
				if (file.exists()) {
					loadStatus = LogStatus.ER;
				} else {
					loadStatus = LogStatus.DF; // download fail
				}
				ps.setString(8, loadStatus);
				ps.executeUpdate();
			}
			connection.close();
		} catch (SQLException e) {
			System.out.println(
					"<---> ERROR [Get list file data information from folder: " + localDir + "]: " + e.getMessage());
		}

	}

	public static void main(String[] args) throws IOException {
		InfoConfig ic = DBControlTool.getInfoConfig(6);
		insertInfoFileToTableLog(6);
		CheckDuplicate.mergeDataDuplicate("tb_log"	, "file_name");
	}
}
