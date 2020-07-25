package model;

public class InfoConfig {
	int idConfig;
	String methodGetData;
	String nasHostName;
	int nasPortNumber;
	String nasUserName;
	String nasPassword;
	String nasDirectory;
	String localDirectory;
	String classForName;
	String portName;
	int portNumber;
	String hostName;
	String userName;
	String password;
	String dbStagingName;
	String dbWarehouseName;
	String data_object;
	String fieldName;
	String fieldFormat;
	String fileSuccessDir;
	String fileFailDir;
	String dtExpired;

	public InfoConfig() {
		super();
	}

	public InfoConfig(int idConfig, String methodGetData, String nasHostName, int nasPortNumber, String nasUserName,
			String nasPassword, String nasDirectory, String localDirectory, String classForName, String portName,
			int portNumber, String hostName, String userName, String password, String dbStagingName,
			String dbWarehouseName, String data_object, String fieldName, String fieldFormat, String fileSuccessDir,
			String fileFailDir, String dtExpired) {
		super();
		this.idConfig = idConfig;
		this.methodGetData = methodGetData;
		this.nasHostName = nasHostName;
		this.nasPortNumber = nasPortNumber;
		this.nasUserName = nasUserName;
		this.nasPassword = nasPassword;
		this.nasDirectory = nasDirectory;
		this.localDirectory = localDirectory;
		this.classForName = classForName;
		this.portName = portName;
		this.portNumber = portNumber;
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
		this.dbStagingName = dbStagingName;
		this.dbWarehouseName = dbWarehouseName;
		this.data_object = data_object;
		this.fieldName = fieldName;
		this.fieldFormat = fieldFormat;
		this.fileSuccessDir = fileSuccessDir;
		this.fileFailDir = fileFailDir;
		this.dtExpired = dtExpired;
	}

	public int getIdConfig() {
		return idConfig;
	}

	public void setIdConfig(int idConfig) {
		this.idConfig = idConfig;
	}

	public String getMethodGetData() {
		return methodGetData;
	}

	public void setMethodGetData(String methodGetData) {
		this.methodGetData = methodGetData;
	}

	public String getNasHostName() {
		return nasHostName;
	}

	public void setNasHostName(String nasHostName) {
		this.nasHostName = nasHostName;
	}

	public int getNasPortNumber() {
		return nasPortNumber;
	}

	public void setNasPortNumber(int nasPortNumber) {
		this.nasPortNumber = nasPortNumber;
	}

	public String getNasUserName() {
		return nasUserName;
	}

	public void setNasUserName(String nasUserName) {
		this.nasUserName = nasUserName;
	}

	public String getNasPassword() {
		return nasPassword;
	}

	public void setNasPassword(String nasPassword) {
		this.nasPassword = nasPassword;
	}

	public String getNasDirectory() {
		return nasDirectory;
	}

	public void setNasDirectory(String nasDirectory) {
		this.nasDirectory = nasDirectory;
	}

	public String getLocalDirectory() {
		return localDirectory;
	}

	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	public String getClassForName() {
		return classForName;
	}

	public void setClassForName(String classForName) {
		this.classForName = classForName;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbStagingName() {
		return dbStagingName;
	}

	public void setDbStagingName(String dbStagingName) {
		this.dbStagingName = dbStagingName;
	}

	public String getDbWarehouseName() {
		return dbWarehouseName;
	}

	public void setDbWarehouseName(String dbWarehouseName) {
		this.dbWarehouseName = dbWarehouseName;
	}

	public String getData_object() {
		return data_object;
	}

	public void setData_object(String data_object) {
		this.data_object = data_object;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldFormat() {
		return fieldFormat;
	}

	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	public String getFileSuccessDir() {
		return fileSuccessDir;
	}

	public void setFileSuccessDir(String fileSuccessDir) {
		this.fileSuccessDir = fileSuccessDir;
	}

	public String getFileFailDir() {
		return fileFailDir;
	}

	public void setFileFailDir(String fileFailDir) {
		this.fileFailDir = fileFailDir;
	}

	public String getDtExpired() {
		return dtExpired;
	}

	public void setDtExpired(String dtExpired) {
		this.dtExpired = dtExpired;
	}

	@Override
	public String toString() {
		return "[InfoConfig] \n idConfig = " + idConfig + "\n methodGetData = " + methodGetData + "\n nasHostName = "
				+ nasHostName + "\n nasPortNumber = " + nasPortNumber + "\n nasUserName = " + nasUserName
				+ "\n nasPassword = " + nasPassword + "\n nasDirectory = " + nasDirectory + "\n localDirectory = "
				+ localDirectory + "\n classForName = " + classForName + "\n portName = " + portName
				+ "\n portNumber = " + portNumber + "\n hostName = " + hostName + "\n userName = " + userName
				+ "\n password = " + password + "\n dbStagingName = " + dbStagingName + "\n dbWarehouseName = "
				+ dbWarehouseName + "\n data_object = " + data_object + "\n fieldName = " + fieldName
				+ "\n fieldFormat = " + fieldFormat + "\n fileSuccessDir = " + fileSuccessDir + "\n fileFailDir = "
				+ fileFailDir + "\n dtExpired = " + dtExpired;
	}

}
