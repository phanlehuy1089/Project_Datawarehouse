package tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ConvertCsvToTxt {
	public static void convertCsvToTxt(String csvPath, String txtPath) {
		File csvFile = new File(csvPath);
		File txtFile = new File(txtPath);
		BufferedReader br = null;
		BufferedWriter bw = null;
		StringBuffer data = new StringBuffer();
		try {
			if (!(txtFile.exists())) {
				txtFile.createNewFile();
			}
			br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile), "UTF-8"));
			String contentLine = null;
			while ((contentLine = br.readLine()) != null) {
				data.append(contentLine + "\r\n");
			}
			String content = data.toString();
			bw.write(content);
			bw.flush();
			br.close();
			bw.close();
		}
		catch (IOException e) {
			System.out.println("<---> ERROR [Convert csv_file to txt_file]: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		String a = "D:\\A\\sinhvien_chieu_nhom4.csv";
		String b = "D:\\A\\sv4.txt";
		convertCsvToTxt(a, b);
	}
}
