package warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection_utils.MySQLConnectionUtils;
import mail.SendMail;
import model.InfoConfig;

public class TransDimToFact {
	// tạo fact table tạm (cho table đăng ký)
	// INSERT INTO db_warehouse_etl.fact_temp_dangky (s_key,ma_sv,ma_lh,ngay_dk) SELECT s_key,ma_sv,ma_lh,ngay_dk FROM db_warehouse_etl.tb_wh_dangky;
	public static void cloneFactTable(Connection connection, String dbWarehouseName, String srcTable, String tempFactTable, String factTempFields) {
		String clone_fact = "INSERT INTO "+dbWarehouseName+"."+tempFactTable+" ("+factTempFields+") SELECT "+factTempFields+" FROM "+dbWarehouseName+"."+srcTable;
		try {
			PreparedStatement ps = connection.prepareStatement(clone_fact);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Clone fact table]: " + e.getMessage());
			SendMail.sendMail("ERROR [Clone fact table]: " + e.getMessage());
		}	
	}
	// chuyển dim thành fact trong table fact tạm
	// UPDATE db_warehouse_etl.fact_temp_dangky AS ft SET ft.ma_lh = (SELECT dim.s_key FROM db_warehouse_etl.tb_wh_lophoc AS dim WHERE dim.ma_lh = ft.ma_lh);
	public static void transDimToFact(Connection connection, String dbWarehouseName, String tempFactTable, String dimTableName, String exclusiveField) {
		String sql = "UPDATE "+dbWarehouseName+"."+tempFactTable+" AS ft SET ft."+exclusiveField+" = (SELECT dim.s_key FROM "+dbWarehouseName+"."+dimTableName+" AS dim WHERE dim."+exclusiveField+" = ft."+exclusiveField+")";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("<---> ERROR [Transfrom Dim ("+dimTableName+"."+exclusiveField+") to Fact]: " + e.getMessage());
			SendMail.sendMail("ERROR [Transfrom Dim ("+dimTableName+"."+exclusiveField+") to Fact]: " + e.getMessage());
		}
	}
	// Chuyển data từ table fact tạm sang table fact chính thức
	// INSERT INTO db_warehouse_etl.fact_dangky (sk,sk_sinhvien,sk_lophoc,sk_ngay_dk) SELECT s_key,ma_sv,ma_lh,ngay_dk FROM db_warehouse_etl.fact_temp_dangky
	public static void transFactTempToFact(Connection connection, String dbWarehouseName, String tempFactTable, String factTable, String factTempFields, String factFields) {
		String sql = "INSERT INTO "+dbWarehouseName+"."+factTable+" ("+factFields+") SELECT "+factTempFields+" FROM "+dbWarehouseName+"."+tempFactTable;
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Trans Data From ("+tempFactTable+") To "+factTable+"]: " + e.getMessage());
			SendMail.sendMail("ERROR [Trans Data From ("+tempFactTable+") To "+factTable+"]: " + e.getMessage());
		}
		
	}
	// 
	public static void processTransDimToFact(InfoConfig infoConfig) {
		/*
		 * TRANSFORM DIM TO FACT
		 */
		String dbWarehouseName = "db_warehouse_etl";
		String tempFactTable = "fact_temp_dangky";
		String factTable = "fact_dangky";
		String factTempFields = "s_key,ma_sv,ma_lh,ngay_dk";
		String factFields = "sk,sk_sinhvien,sk_lophoc,sk_ngay_dk";
		String srcTable = "tb_wh_dangky";
		String dimSinhVien = "tb_wh_sinhvien";
		String dimLopHoc = "tb_wh_lophoc";
		String dimDate = "date_dim";
		// 1. Tạo kết nối tới Database Warehouse
		Connection connectionWarehouse = MySQLConnectionUtils.getConnection(infoConfig, dbWarehouseName);
		// 2. Tạo table Fact tạm (tb_wh_dangky -> fact_temp_dangky)
		TransDimToFact.cloneFactTable(connectionWarehouse, dbWarehouseName, srcTable, tempFactTable, factTempFields);
		// 3. Chuyển dim thành fact trong table Fact tạm
		TransDimToFact.transDimToFact(connectionWarehouse, dbWarehouseName, tempFactTable, dimSinhVien, "ma_sv");
		TransDimToFact.transDimToFact(connectionWarehouse, dbWarehouseName, tempFactTable, dimLopHoc, "ma_lh");
		TransDimToFact.transDimToFact(connectionWarehouse, dbWarehouseName, tempFactTable, dimDate, "ngay_dk");
		// 4. Chuyển data từ table Fact tạm sang Fact chính thức
		TransDimToFact.transFactTempToFact(connectionWarehouse, dbWarehouseName, tempFactTable, factTable, factTempFields, factFields);
		// 5. Đóng kết nối
		try {
			connectionWarehouse.close();
		} catch (SQLException e) {
			System.out.println("<---> ERROR [Close connection]: " + e.getMessage());
		}
	}
}
