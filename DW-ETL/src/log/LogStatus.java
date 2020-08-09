package log;

public class LogStatus {
	public static final String DF = "DF"; // download file fail
	public static final String ER = "ER"; // download file success | extract ready
	public static final String EF = "EF"; // extract failed
	public static final String TR = "TR"; // transform ready | extract to staging success
	public static final String TF = "TF"; // transform fail
	public static final String LR = "LR"; // load file to data warehouse ready | transform data success
	public static final String SU = "SU"; // load file to data warehouse successfully
	
	public static void main(String[] args) {
		System.out.println(LogStatus.DF);
	}
}

