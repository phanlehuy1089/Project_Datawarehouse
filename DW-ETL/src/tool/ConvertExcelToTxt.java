package tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import transform.DateTransform;

public class ConvertExcelToTxt {

	public static void main(String[] args) throws IOException {
		final String excelFilePath = "D:\\A\\sinhvien_chieu_nhom3.xlsx";
		String txtFilePath = "D:\\A\\sinhvien_chieu_nhom3_text.txt";
//		final String excelFilePath = "D:\\A\\dangky_chieu_nhom4_2020.xlsx";
//		String txtFilePath = "D:\\A\\dangky_chieu_nhom4_2020.txt";
		convertExcelToTxt(excelFilePath, txtFilePath, ";");
	}

	@SuppressWarnings("unused")
	public static void convertExcelToTxt(String excelFilePath, String txtFilePath, String delimiter)
			throws IOException {

		File txtFile = new File(txtFilePath);
		txtFile.createNewFile();

		StringBuffer data = new StringBuffer();
		FileOutputStream txtFileOut = new FileOutputStream(txtFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(txtFileOut, "UTF-8"));

		// Get file
		InputStream inputStream = new FileInputStream(new File(excelFilePath));
		// Get workbook
		Workbook workbook = getWorkbook(inputStream, excelFilePath);
		// Get sheet
		Sheet sheet = workbook.getSheetAt(0);

		int maxNumOfCells = sheet.getRow(0).getLastCellNum();
		// Get all rows
		Iterator<Row> iterator = sheet.iterator();

		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
//			if (nextRow.getRowNum() == 0) {
//				// Ignore header
//				continue;
//			}

			for (int cellCounter = 0; cellCounter < maxNumOfCells; cellCounter++) { // Loop through cells
				if (nextRow.getCell(cellCounter) == null) {
					Cell newCell = nextRow.createCell(cellCounter);
				} else {
					Cell newCell = nextRow.getCell(cellCounter);
				}

			}

			// Get all cells
			Iterator<Cell> cellIterator = nextRow.cellIterator();

			// Read cells and set value for book object
			while (cellIterator.hasNext()) {
				// Read cell
				Cell cell = cellIterator.next();
				if (cell != null) {
					switch (cell.getCellTypeEnum()) {
					case BLANK:
					case _NONE:
					case ERROR:
						data.append(delimiter + "0");
						break;
					case STRING:
						if (cell.getStringCellValue() == null || cell.getStringCellValue().equals("")) {
							data.append(delimiter + "0");
						} else {
							data.append(delimiter + cell.getStringCellValue());
						}
						break;
					case NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							String dateFormatted = DateTransform.transformDateInt((int) cell.getNumericCellValue());
							data.append(delimiter + dateFormatted);
						} else {
							data.append(delimiter + (int) cell.getNumericCellValue());
						}
						break;
					case BOOLEAN:
						data.append(delimiter + cell.getBooleanCellValue());
						break;
					case FORMULA:
						Workbook wbFormula = cell.getSheet().getWorkbook();
						FormulaEvaluator evaluator = wbFormula.getCreationHelper().createFormulaEvaluator();
						data.append(delimiter + evaluator.evaluate(cell).getStringValue());
						break;
					default:
						data.append(delimiter + "0");
						break;
					}
				}
			}
			data.append('\n');
		}

		String crlfData = data.toString().replaceAll("\n" + delimiter, "\r\n"); // Convert LF to CRLF
//		String perfectData = crlfData.replaceAll("\r\n\\?", "\r\n0");

		StringBuilder perfectDataSB = new StringBuilder(crlfData);
		perfectDataSB.deleteCharAt(0);

		bw.write(perfectDataSB.toString());
		bw.flush(); //

		workbook.close();
		inputStream.close();
		bw.close();
		
		System.out.println("[Convert] [Excel File] To [Text File]");
	}

	// Get Workbook
	private static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file " + excelFilePath);
		}
		return workbook;
	}
}
