package run;

import control.DBControlTool;
import download_file.CreateInfoDataFile;
import download_file.LoadFileWithSCP;
import etl.LoadFileToStaging;
import model.InfoConfig;

public class RunScript {
	public static void run(int idConfig) {
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		String dataObject = infoConfig.getData_object();
		System.out.println("\n\n<--------[LOAD DATA WITH OBJECT]: " + dataObject + " -------->\n");
		String methodGetData = infoConfig.getMethodGetData();
		switch (methodGetData) {
		case "download":
			System.out.println("[Begin download...]");
			LoadFileWithSCP.downloadAllFile(infoConfig);
			System.out.println("[Create list file data information]\n");
			CreateInfoDataFile.insertInfoFileToTableLog(idConfig);
			System.out.println("\n[Begin load data to STAGING]");
			LoadFileToStaging.loadDataToStaging(idConfig);
			break;
		case "local":
			System.out.println("[Create list file data information]");
			CreateInfoDataFile.insertInfoFileToTableLog(idConfig);
			System.out.println("[Begin load data to STAGING]");
			LoadFileToStaging.loadDataToStaging(idConfig);
			break;
		default:
			System.out.println(
					"<---> ERROR [Method get data]: Không thể lấy dữ liệu với phương thức này: " + methodGetData);
			break;
		}
	}

	public static void main(String[] args) {
		long millis1 = System.currentTimeMillis();
		run(6);
		long millis2 = System.currentTimeMillis();
		long distance = millis2 - millis1;

		System.out.println(
				"--------------------------------------\n[TỔNG THỜI GIAN THỰC HIỆN]: " + distance + " milliseconds");
	}
}
