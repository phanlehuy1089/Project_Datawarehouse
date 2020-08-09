package tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

public class ConvertCsvToTxt {
	public static void convertCsvToTxt(String csvPath, String copyCsvPath, String txtPath, String txtOutPath, String delimiter)
			throws IOException {
		File csvFile = new File(csvPath);
		File copyCsvFile = new File(copyCsvPath);
		File txtFile = new File(txtPath);
		File txtOutFile = new File(txtOutPath);

		Files.copy(csvFile.toPath(), copyCsvFile.toPath());

		copyCsvFile.renameTo(txtFile);

		BufferedReader br = null;
		BufferedWriter bw = null;
		StringBuilder data = new StringBuilder();

		br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"));
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtOutFile), "UTF-8"));
		String contentLine = null;
		while ((contentLine = br.readLine()) != null) {
			data.append(contentLine + "\r\n");
		}
		String content = data.toString().replaceAll(delimiter+delimiter, delimiter+"0"+delimiter);
		String content1 = content.replaceAll(delimiter+"\r\n", delimiter+"0\r\n");
		String content2 = content1.replaceAll("\r\n"+delimiter, "\r\n0"+delimiter);
		String content3 = content2.replaceAll(delimiter+delimiter, delimiter+"0"+delimiter);
//		System.out.println(content3);
		bw.write(content3);
		bw.flush();
		br.close();
		bw.close();
		
		txtFile.delete();
		
		System.out.println("[Convert] [CSV File] To [Text File]");
	}

	public static void main(String[] args) throws IOException {
		String csvPath = "D:\\A\\sinhvien_chieu_nhom16.csv";
		String copyCsvPath = "D:\\A\\sinhvien_chieu_nhom16_copy.csv";
		String txtPath = "D:\\A\\sinhvien_chieu_nhom16.txt";
		String txtOutPath = "D:\\A\\sinhvien_chieu_nhom16_cc.txt";
		long t1 = System.currentTimeMillis();
		convertCsvToTxt(csvPath, copyCsvPath, txtPath, txtOutPath, ";");
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
	}
}
