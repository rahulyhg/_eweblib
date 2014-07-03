package com.eweblib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.RichTextString;

/**
 * 共分为六部完成根据模板导出excel操作：<br/>
 * 第一步、设置excel模板路径（setSrcPath）<br/>
 * 第二步、设置要生成excel文件路径（setDesPath）<br/>
 * 第三步、设置模板中哪个Sheet列（setSheetName）<br/>
 * 第四步、获取所读取excel模板的对象（getSheet）<br/>
 * 第五步、设置数据（分为6种类型数据：setCellStrValue、setCellDateValue、setCellDoubleValue、
 * setCellBoolValue、setCellCalendarValue、setCellRichTextStrValue）<br/>
 * 第六步、完成导出 （exportToNewFile）<br/>
 * 
 * @author Administrator
 * 
 */
public class ExcelTemplateUtil {
	private String srcXlsPath = "";// // excel模板路径
	private String desXlsPath = "";
	private String sheetName = "";
	POIFSFileSystem fs = null;
	HSSFWorkbook wb = null;
	HSSFSheet sheet = null;

	/**
	 * 第一步、设置excel模板路径
	 * 
	 * @param srcXlsPath
	 */
	public void setSrcPath(String srcXlsPath) {
		this.srcXlsPath = srcXlsPath;
	}

	/**
	 * 第二步、设置要生成excel文件路径
	 * 
	 * @param desXlsPath
	 */
	public void setDesPath(String desXlsPath) {
		this.desXlsPath = desXlsPath;
	}

	/**
	 * 第三步、设置模板中哪个Sheet列
	 * 
	 * @param sheetName
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * 第四步、获取所读取excel模板的对象
	 */
	public void getSheet() {
		try {
			File fi = new File(srcXlsPath);
			if (!fi.exists()) {
				System.out.println("模板文件:" + srcXlsPath + "不存在!");
				return;
			}
			FileInputStream fileInputStream = new FileInputStream(fi);
			fs = new POIFSFileSystem(fileInputStream);
			fileInputStream.close();
			wb = new HSSFWorkbook(fs);
			sheet = wb.getSheet(sheetName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 第五步、设置字符串类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --字符串类型的数据
	 */
	public void setCellStrValue(int rowIndex, int cellnum, String value) {
		HSSFRow toRow = sheet.getRow(rowIndex);
		HSSFCell cell = toRow.getCell(cellnum);
		cell.setCellValue(value);

		//
		// if (rowIndex > 28 && rowIndex%2 == 0) {
		//
		// HSSFRow fromRow1 = sheet.getRow(7);
		// HSSFRow fromRow2 = sheet.getRow(8);
		// HSSFRow toRow1 = sheet.getRow(rowIndex + 1);
		// for (Iterator cellIt = fromRow1.cellIterator(); cellIt.hasNext();) {
		// HSSFCell tmpCell = (HSSFCell) cellIt.next();
		// HSSFCell newCell = toRow1.createCell(tmpCell.getCellNum());
		// copyCell(wb, tmpCell, newCell, false);
		// }
		//
		// HSSFRow toRow2= sheet.getRow(rowIndex + 2);
		// for (Iterator cellIt = fromRow2.cellIterator(); cellIt.hasNext();) {
		// HSSFCell tmpCell = (HSSFCell) cellIt.next();
		// HSSFCell newCell = toRow1.createCell(tmpCell.getCellNum());
		// copyCell(wb, tmpCell, newCell, false);
		// }
		//
		// }

	}

	public static void copyCellStyle(HSSFCellStyle fromStyle, HSSFCellStyle toStyle) {
		toStyle.setAlignment(fromStyle.getAlignment());
		// 边框和边框颜色
		toStyle.setBorderBottom(fromStyle.getBorderBottom());
		toStyle.setBorderLeft(fromStyle.getBorderLeft());
		toStyle.setBorderRight(fromStyle.getBorderRight());
		toStyle.setBorderTop(fromStyle.getBorderTop());
		toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
		toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
		toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
		toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

		// 背景和前景
		toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
		toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

		toStyle.setDataFormat(fromStyle.getDataFormat());
		toStyle.setFillPattern(fromStyle.getFillPattern());
		// toStyle.setFont(fromStyle.getFont(null));
		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());// 首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());// 旋转
		toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
		toStyle.setWrapText(fromStyle.getWrapText());

	}

	public static void copyCell(HSSFWorkbook wb, HSSFCell srcCell, HSSFCell distCell, boolean copyValueFlag) {
		HSSFCellStyle newstyle = wb.createCellStyle();
		copyCellStyle(srcCell.getCellStyle(), newstyle);
		// distCell.setEncoding(srcCell.getc.getEncoding());
		// 样式
		distCell.setCellStyle(newstyle);
		// 评论
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		// 不同数据类型处理
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (copyValueFlag) {
			if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
					distCell.setCellValue(srcCell.getDateCellValue());
				} else {
					distCell.setCellValue(srcCell.getNumericCellValue());
				}
			} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
				distCell.setCellValue(srcCell.getRichStringCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
				// nothing21
			} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
				distCell.setCellValue(srcCell.getBooleanCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
				distCell.setCellErrorValue(srcCell.getErrorCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
				distCell.setCellFormula(srcCell.getCellFormula());
			} else { // nothing29
			}
		}
	}

	/**
	 * 第五步、设置日期/时间类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --日期/时间类型的数据
	 */
	public void setCellDateValue(int rowIndex, int cellnum, Date value) {
		HSSFCell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置浮点类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --浮点类型的数据
	 */
	public void setCellDoubleValue(int rowIndex, int cellnum, double value) {
		HSSFCell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置Bool类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --Bool类型的数据
	 */
	public void setCellBoolValue(int rowIndex, int cellnum, boolean value) {
		HSSFCell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置日历类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --日历类型的数据
	 */
	public void setCellCalendarValue(int rowIndex, int cellnum, Calendar value) {
		HSSFCell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置富文本字符串类型的数据。可以为同一个单元格内的字符串的不同部分设置不同的字体、颜色、下划线
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --富文本字符串类型的数据
	 */
	public void setCellRichTextStrValue(int rowIndex, int cellnum, RichTextString value) {
		HSSFCell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第六步、完成导出
	 */
	public void exportToNewFile() {
		FileOutputStream out;
		try {
			out = new FileOutputStream(desXlsPath);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deletRow(int start, int i) {

		int delstart = start;
		while (start < i) {
			HSSFRow row = sheet.getRow(start);
			sheet.removeRow(row);
			start = start + 1;
		}

		i = sheet.getLastRowNum();
		HSSFRow tempRow;
		while (i > 30) {
			i--;
			tempRow = sheet.getRow(i);
			if (tempRow == null) {
				sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
			}
		}

	}

}