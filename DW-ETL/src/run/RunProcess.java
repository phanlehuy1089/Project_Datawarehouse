package run;

import control.DBControlTool;
import download_file.CreateInfoDataFile;
import download_file.LoadFileWithSCP;
import etl.MainProcess;
import model.InfoConfig;
import warehouse.TransDimToFact;

public class RunProcess {
	public static void run(int idConfig) {
		InfoConfig infoConfig = DBControlTool.getInfoConfig(idConfig);
		String methodGetData = infoConfig.getMethodGetData();
		switch (methodGetData) {
		case "download":
			System.out.println("\n[Begin Download...]");
			LoadFileWithSCP.downloadAllFile(infoConfig);
			System.out.println("[Create List File Data Information]");
			CreateInfoDataFile.insertInfoFileToTableLog(idConfig);
			// Execute main process
			MainProcess.loadDataWithConfigID(idConfig);
			// Transform Dim to Fact
			System.out.println("\n[Transform Dim To Fact]\n-------------------------------\n");
			if (DBControlTool.checkFlag("download", "done") == true) {
				TransDimToFact.processTransDimToFact(infoConfig);
				System.out.println("Transform Dim To Fact Successfully!!");
			} else {
				System.out.println("Lack Of Data To Transform Dim To Fact");
			}
			break;
		case "local":
			System.out.println("[Create List File Data Information]");
			CreateInfoDataFile.insertInfoFileToTableLog(idConfig);
			// Execute main process
			MainProcess.loadDataWithConfigID(idConfig);
			// Transform Dim to Fact
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
		int firstArg;
		if (args.length > 0) {
			try {
				firstArg = Integer.parseInt(args[0]);
				long millis1 = System.currentTimeMillis();
				run(firstArg); // run with idConfig
				long millis2 = System.currentTimeMillis();
				long distance = millis2 - millis1;
				System.out.println("\n[PROCESS TOTAL TIME]: " + distance + " milliseconds");
			} catch (NumberFormatException e) {
				System.err.println("Argument <idConfig> " + args[0] + " must be an integer.");
				System.exit(1);
			}
		}
	}
}
