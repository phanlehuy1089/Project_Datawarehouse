package tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FormatDelimiterFileTxt {
	public static void formatFileTxt(String txtSourcePath, String txtTargetPath, String delimiter) throws IOException {
		File fileSourcePath = new File(txtSourcePath);
		File fileTargetPath = new File(txtTargetPath);
		BufferedReader br = null;
		BufferedWriter bw = null;

		if (!(fileTargetPath.exists())) {
			fileTargetPath.createNewFile();
		}
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileSourcePath), "UTF-8"));
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileTargetPath), "UTF-8"));
		StringBuffer data = new StringBuffer();
		String contentLine = null;
		while ((contentLine = br.readLine()) != null) {
			data.append(contentLine + "\r\n");
		}
		String content = data.toString().replaceAll("\\|", delimiter);
		bw.write(content);
		bw.flush();
		br.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		long t1 = System.currentTimeMillis();
		formatFileTxt("D:\\A\\sinhvien_sang_nhom8.txt", "D:\\A\\sinhvien_sang_nhom8_2.txt", ";");
		long t2 = System.currentTimeMillis();
		System.out.println("\n" + (t2 - t1));
	}
}
