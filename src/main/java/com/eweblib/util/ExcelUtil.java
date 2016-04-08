package com.eweblib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.eweblib.bean.BaseEntity;

public class ExcelUtil {

	Workbook wb = null;
	InputStream fis = null;
	OutputStream fos = null;
	private String path = null;
	private File file = null;
	List<String[]> dataList = new ArrayList<String[]>(100);

	public ExcelUtil() {
	}

	public ExcelUtil(InputStream is) {
		try {
			wb = WorkbookFactory.create(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ExcelUtil(File file) {
		try {
			this.file = file;
			fis = new FileInputStream(file);
			wb = WorkbookFactory.create(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ExcelUtil(String path) {
		try {
			this.path = path;
			fis = new FileInputStream(path);
			// fos = new FileOutputStream(path);
			wb = WorkbookFactory.create(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<String[]> getAllData(int sheetIndex) {
		dataList = new ArrayList<String[]>(100);
		Sheet sheet = wb.getSheetAt(sheetIndex);

		for (Row row : sheet) {
			Cell cell = null;
			String[] singleRow = null;
			if (row.getLastCellNum() > 0) {
				singleRow = new String[row.getLastCellNum()];
				for (int i = 0; i < row.getLastCellNum(); i++) {
					cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BLANK:
						singleRow[i] = "";
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						singleRow[i] = Boolean.toString(cell.getBooleanCellValue());
						break;

					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							singleRow[i] = String.valueOf(cell.getDateCellValue());
						} else {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							String temp = cell.getStringCellValue();
							//
							if (temp.indexOf(".") > -1) {
								singleRow[i] = String.valueOf(new Double(temp)).trim();
							} else {
								singleRow[i] = temp.trim();
							}
						}
						break;
					case Cell.CELL_TYPE_STRING:
						singleRow[i] = cell.getStringCellValue().trim();
						break;
					case Cell.CELL_TYPE_ERROR:
						singleRow[i] = "";
						break;
					case Cell.CELL_TYPE_FORMULA:
						cell.setCellType(Cell.CELL_TYPE_STRING);
						singleRow[i] = cell.getStringCellValue();
						if (singleRow[i] != null) {
							singleRow[i] = singleRow[i].replaceAll("#N/A", "").trim();
						}
						break;
					default:
						singleRow[i] = "";
						break;
					}
				}

				dataList.add(singleRow);
			}
		}

		return dataList;
	}

	public int getRowNum(int sheetIndex) {
		Sheet sheet = wb.getSheetAt(sheetIndex);
		return sheet.getLastRowNum();
	}

	public int getColumnNum(int sheetIndex) {

		Sheet sheet = wb.getSheetAt(sheetIndex);
		Row row = sheet.getRow(0);
		if (row != null && row.getLastCellNum() > 0) {
			return row.getLastCellNum();
		}
		return 0;
	}

	public int getNumberOfSheets() {
		return wb.getNumberOfSheets();
	}

	public String getSheetName(int index) {
		return wb.getSheetAt(index).getSheetName();
	}

	public String[] getRowData(int sheetIndex, int rowIndex) {
		getAllData(sheetIndex);
		String[] dataArray = null;
		if (rowIndex > this.getRowNum(sheetIndex)) {
			return dataArray;
		} else {
			dataArray = new String[this.getColumnNum(sheetIndex)];
			return this.dataList.get(rowIndex);
		}

	}

	public String[] getColumnData(int sheetIndex, int colIndex) {
		String[] dataArray = null;
		if (colIndex > this.getColumnNum(sheetIndex)) {
			return dataArray;
		} else {
			if (this.dataList != null && this.dataList.size() > 0) {
				dataArray = new String[this.getRowNum(sheetIndex) + 1];
				int index = 0;
				for (String[] rowData : dataList) {
					if (rowData != null) {
						dataArray[index] = rowData[colIndex];
						index++;
					}
				}
			}
		}
		return dataArray;

	}

	public void addRow(int sheetIndex, String[] row, int rownum) throws Exception {
		Sheet sheet = null;

		try {
			sheet = wb.getSheetAt(sheetIndex);
		} catch (Exception e) {
		}
		if (sheet == null) {
			sheet = (HSSFSheet) wb.createSheet();
		}

		Row addedRow = sheet.createRow(rownum);
		Cell cell = null;

		int colnum = this.getColumnNum(sheetIndex);
		int count = colnum < row.length ? colnum : row.length;
		for (int i = 0; i < row.length; i++) {
			cell = addedRow.createCell(i);
			cell.setCellValue(row[i]);
		}

		fos = new FileOutputStream(this.file);
		wb.write(this.fos);
		fos.close();
	}

	public void createFile(File f) {
		wb = new HSSFWorkbook();
		try {

			if (f.exists()) {
				f.delete();
			}
			f.getParentFile().mkdirs();
			FileOutputStream fileOut = new FileOutputStream(f);
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static String getRowColumnValue(String[] row, Map<String, Integer> keyMap, String title) {

		if (keyMap.get(title) == null) {
			return null;
		}

		if (keyMap.get(title) >= row.length) {
			return null;
		}

		return row[keyMap.get(title)].trim();
	}

	public static String createExcelListFile(List<Map<String, Object>> listMapData, String[] colunmTitleHeaders, String[] colunmHeaders, File f) {
		if (f.exists()) {
			f.delete();
		}
		f.getParentFile().mkdirs();

		ExcelUtil eu = new ExcelUtil();
		eu.createFile(f);
		eu = new ExcelUtil(f);

		int i = 0;
		try {
			eu.addRow(0, colunmTitleHeaders, i);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Map<String, Object> map : listMapData) {
			int length = colunmHeaders.length;
			String rowsData[] = new String[length];

			int index = 0;
			for (String key : colunmHeaders) {
				if (map.get(key) == null) {
					rowsData[index] = "";
				} else {
					rowsData[index] = map.get(key).toString();
				}
				index++;
			}
			try {
				eu.addRow(0, rowsData, ++i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return f.getAbsolutePath();
	}

    public static <T extends BaseEntity> String createExcelListFileByEntity(List<T> list, String[] colunmTitleHeaders, String[] colunmHeaders, String f) {
        return createExcelListFileByEntity(list, colunmTitleHeaders, colunmHeaders, new File(f));
    }
	
	public static <T extends BaseEntity> String createExcelListFileByEntity(List<T> list, String[] colunmTitleHeaders, String[] colunmHeaders, File f) {
		
		List<Map<String, Object>> listMapData  =new ArrayList<Map<String, Object>>();
		if(list!=null){
			for(T entity: list){
				listMapData.add(entity.toMap());
			}
		}
		
		return createExcelListFile(listMapData, colunmTitleHeaders, colunmHeaders, f);
		
	}
}
