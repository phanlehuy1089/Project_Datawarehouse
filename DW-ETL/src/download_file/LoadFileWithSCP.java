package download_file;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import control.DBControlTool;
import model.InfoConfig;

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
		ck.UnlockBundle("WAIT....");
		CkSsh ssh = new CkSsh();
		// Connect to server
		String nasHostName = infoConfig.getNasHostName();
		int nasPortNumber = infoConfig.getNasPortNumber();
		boolean success = ssh.Connect(nasHostName, nasPortNumber);
		if (success != true) {
			System.out.println("NAS: <Connected to Server failed!>");
			return;
		}
		ssh.put_IdleTimeoutMs(5000);
		
		// Check account
		String nasUserName = infoConfig.getNasUserName();
		String nasPass = infoConfig.getNasPassword();
		success = ssh.AuthenticatePw(nasUserName, nasPass);
		if (success != true) {
			System.out.println("NAS: <Wrong userName or password>");
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
			System.out.println("<---> ERROR [Download] remoteDir or localDir is not exist");
			return;
		}
		System.out.println("Download success!");
		ssh.Disconnect();
	}

	public static void main(String argv[]) {
		InfoConfig infoConfig = DBControlTool.getInfoConfig(1);
		downloadAllFile(infoConfig);
		
	}
}


