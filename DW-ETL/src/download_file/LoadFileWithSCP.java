package download_file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import control.DBControlTool;
import model.InfoConfig;

@SuppressWarnings("unused")
public class LoadFileWithSCP {
	static {
		try {
			System.loadLibrary("chilkat");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}
	public static void downloadAllFile(InfoConfig infoConfig) {
		CkGlobal ck = new CkGlobal();
		ck.UnlockBundle("VUI LÒNG CHỜ....");
		CkSsh ssh = new CkSsh();
		// Kết nối đến sever
		String nasHostName = infoConfig.getNasHostName();
		int nasPortNumber = infoConfig.getNasPortNumber();
		boolean success = ssh.Connect(nasHostName, nasPortNumber);
		if (success != true) {
			System.out.println("Lỗi kết nối đến sever");
			return;
		}
		ssh.put_IdleTimeoutMs(5000);
		
		// Kiểm tra tài khoản
		String nasUserName = infoConfig.getNasUserName();
		String nasPass = infoConfig.getNasPassword();
		success = ssh.AuthenticatePw(nasUserName, nasPass);
		if (success != true) {
			System.out.println("sai tên đăng nhập hoặc mật khẩu");
			return;
		}
		CkScp scp = new CkScp();

		success = scp.UseSsh(ssh);
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}
		String dataObject = infoConfig.getDataObject();
		scp.put_SyncMustMatch("" + dataObject + "*.*");
		String nasDirectory = infoConfig.getNasDirectory();
		String localDirectory = infoConfig.getLocalDirectory();
		success = scp.SyncTreeDownload(nasDirectory, localDirectory, 2, false);
		if (success != true) {
			System.out.println("remoteDir or localDir không tồn tại");
			return;
		}
		System.out.println("Tải xuống thành công!\n");
		ssh.Disconnect();
	}

	@SuppressWarnings("static-access")
	public static void main(String argv[]) {
		InfoConfig infoConfig = DBControlTool.getInfoConfig(10);
		downloadAllFile(infoConfig);
		
	}
}


