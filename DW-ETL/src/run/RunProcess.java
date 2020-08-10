package run;

import java.util.Scanner;

import control.DBControlTool;
import download_file.CreateInfoDataFile;
import download_file.LoadFileWithSCP;
import etl.MainProcess;
import model.InfoConfig;
import warehouse.TransDimToFact;

public class RunProcess {
	// Quy trình tiến trình được thực thi
	public static void run(int idConfig) {
		// Lấy thông tin Config thông qua idConfig
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		// Kiểm tra phương thức lấy dữ liệu (download hoặc local)
		String methodGetData = infoConfig.getMethodGetData();
		switch (methodGetData) {
		case "download":
			// Download
			System.out.println("\n[Begin Download...]");
			LoadFileWithSCP.downloadAllFile(infoConfig);
			// Tạo danh sách thông tin DataFile từ thư mục đã tải xuống
			System.out.println("[Create List File Data Information]");
			CreateInfoDataFile.insertInfoFileToTableLog(idConfig);
			// Thực thi tiến trình chính
			MainProcess.loadDataWithConfig(infoConfig);
			// Chuyển đổi Dim thành Fact
			System.out.println("\n[Transform Dim To Fact]\n-------------------------------\n");
			if (DBControlTool.checkFlag("download", "done") == true) {
				TransDimToFact.processTransDimToFact(infoConfig);
				System.out.println("Transform Dim To Fact Successfully!!");
			} else {
				System.out.println("Lack Of Data To Transform Dim To Fact");
			}
			break;
		case "local":
			// Tạo danh sách thông tin DataFile từ thư mục chứa file
			System.out.println("[Create List File Data Information]");
			CreateInfoDataFile.insertInfoFileToTableLog(idConfig);
			// Thực thi tiến trình chính
			MainProcess.loadDataWithConfig(infoConfig);
			// Chuyển đổi Dim thành Fact
			System.out.println("\n[Transform Dim To Fact]\n-------------------------------\n");
			if (DBControlTool.checkFlag("local", "done") == true) {
				TransDimToFact.processTransDimToFact(infoConfig);
				System.out.println("Transform Dim To Fact Successfully!!");
			} else {
				System.out.println("Lack Of Data To Transform Dim To Fact");
			}
			break;
		default:
			System.out.println("<---> ERROR [Method get data]: Cannot load data with this method: " + methodGetData);
			break;
		}
	}

	public static void main(String[] args) {
//		Scanner sc = new Scanner(System.in);
//		System.out.println("Enter ID Config: ");
//		int idConfig = sc.nextInt();
//		long t1 = System.currentTimeMillis();
//		RunProcess.run(idConfig);
//		long t2 = System.currentTimeMillis();
//		long distance = t2 - t1;
//		System.out.println("\n[PROCESS TOTAL TIME]: " + distance + " milliseconds");
		
		int idConfig;
		if (args.length > 0) {
			try {
				idConfig = Integer.parseInt(args[0]);
				System.out.println("[Checking ID Config...]");
				if (DBControlTool.checkIdConfig(idConfig) == true) {
					System.out.println("Checked ID Config: OK!");
					long millis1 = System.currentTimeMillis();
					RunProcess.run(idConfig);
					long millis2 = System.currentTimeMillis();
					long distance = millis2 - millis1;
					System.out.println("\n[PROCESS TOTAL TIME]: " + distance + " milliseconds");
				} else {
					System.out.println("Checked ID Config ["+idConfig+"]: FAIL - There is no corresponding idConfig in Database Control");
					System.exit(0);
				}
			} catch (NumberFormatException e) {
				System.err.println("Argument <idConfig> " + args[0] + " must be an integer.");
				System.exit(1);
			}
		}
	}
}
