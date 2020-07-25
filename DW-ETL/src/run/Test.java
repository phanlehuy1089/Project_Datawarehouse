package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection_utils.MySQLConnectionUtils;
import control.DBControlTool;
import model.InfoConfig;

public class Test {
	public static void main(String[] args) throws SQLException {
//		String sql = "INSERT INTO table2 (stt,ma_mh,ten_mh,so_tc,khoa_quan_ly) SELECT stt,ma_mh,ten_mh,so_tc,khoa_quan_ly FROM tb_wh_temp_monhoc";
		String sql = "LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/sinhvien_chieu_nhom4.csv' INTO TABLE tb_staging_sinhvien CHARACTER SET 'UTF8' FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\r\\n' IGNORE 1 LINES;";
		InfoConfig infoConfig = DBControlTool.getInfoConfig(6);
		Connection conn = MySQLConnectionUtils.getConnection(infoConfig, "db_staging_etl");
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		conn.close();
		
	}
}
