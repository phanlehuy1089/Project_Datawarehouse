package model;

public class Log {
	int idLog;
	int idConfig;
	String fileLocalPath;
	String fileName;
	String fileType;
	String fileDelimiter;
	int numberOfRecords;
	String logTimestamp;
	String fileStatus;
	public Log() {
		super();
	}
	public Log(int idLog, int idConfig, String fileLocalPath, String fileName, String fileType, String fileDelimiter,
			int numberOfRecords, String logTimestamp, String fileStatus) {
		super();
		this.idLog = idLog;
		this.idConfig = idConfig;
		this.fileLocalPath = fileLocalPath;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileDelimiter = fileDelimiter;
		this.numberOfRecords = numberOfRecords;
		this.logTimestamp = logTimestamp;
		this.fileStatus = fileStatus;
	}
	public int getIdLog() {
		return idLog;
	}
	public void setIdLog(int idLog) {
		this.idLog = idLog;
	}
	public int getIdConfig() {
		return idConfig;
	}
	public void setIdConfig(int idConfig) {
		this.idConfig = idConfig;
	}
	public String getFileLocalPath() {
		return fileLocalPath;
	}
	public void setFileLocalPath(String fileLocalPath) {
		this.fileLocalPath = fileLocalPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileDelimiter() {
		return fileDelimiter;
	}
	public void setFileDelimiter(String fileDelimiter) {
		this.fileDelimiter = fileDelimiter;
	}
	public int getNumberOfRecords() {
		return numberOfRecords;
	}
	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}
	public String getLogTimestamp() {
		return logTimestamp;
	}
	public void setLogTimestamp(String logTimestamp) {
		this.logTimestamp = logTimestamp;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	@Override
	public String toString() {
		return "Log [idLog = " + idLog + ", idConfig = " + idConfig + ", fileLocalPath = " + fileLocalPath + ", fileName = "
				+ fileName + ", fileType = " + fileType + ", fileDelimiter = " + fileDelimiter + ", numberOfRecords = "
				+ numberOfRecords + ", logTimestamp = " + logTimestamp + ", fileStatus = " + fileStatus + "]";
	}
	
	
	
}
