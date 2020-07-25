package tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MoveFile {

	public static boolean moveFile(String sourcePath, String targetPath) {
		boolean fileMoved = true;
		try {
			Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			fileMoved = false;
			System.out.println("<---> ERROR [Move file]: " + e.getMessage());
		}
		System.out.println("[Move file] from [" + sourcePath + "] to [" + targetPath + "] success!");
		return fileMoved;
	}

	public static void main(String[] args) {
		moveFile("D:\\1\\sinhvien\\sinhvien_chieu_nhom4.txt", "D:\\1\\sinhvien_chieu_nhom4.txt");
	}
}
