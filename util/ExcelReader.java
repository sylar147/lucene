package com.cape.platform.module.search.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-3 下午10:00:32
 * @deprecated 请参考<code>com.cape.platform.module.search.parser.OoxmlParser</code>
 */
@Deprecated
public class ExcelReader {
	
	private final static Log logger = LogFactory.getLog(ExcelReader.class);

	public static String getXlsContent(InputStream is) {
		StringBuffer content = new StringBuffer();
		// 工作簿
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(is);
			// 循环每一个sheet
			for (int numSheets = 0; numSheets < workbook.getNumberOfSheets(); numSheets++) {
				// 获得一个sheet
				HSSFSheet aSheet = workbook.getSheetAt(numSheets);
				content.append("\n");
				if (null == aSheet) {
					continue;
				}
				// 循环每一行
				for (int rowNum = 0; rowNum <= aSheet.getLastRowNum(); rowNum++) {
					content.append("\n");
					// 得到某一行
					HSSFRow aRow = aSheet.getRow(rowNum);
					if (null == aRow) {
						continue;
					}
					// 循环每一列
					for (int cellNum = 0; cellNum <= aRow.getLastCellNum(); cellNum++) {
						// 得到每一列
						HSSFCell aCell = aRow.getCell(cellNum);
						if (null == aCell) {
							continue;
						}
						// 如果是字符串类型
						if (aCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
							content.append(aCell.getRichStringCellValue()
									.getString());
							// 否则，如果是数值类型
						} else if (aCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
							// 如果Cell的Type为CELL_TYPE_NUMERIC时，还需要进一步判断该Cell的数据格式，
							// 因为它有可能是Date类型，在Excel中的Date类型也是以Double类型的数字存储的。
							boolean b = HSSFDateUtil.isCellDateFormatted(aCell);
							if (b) {
								Date date = aCell.getDateCellValue();
								SimpleDateFormat df = new SimpleDateFormat(
										"yyyy-MM-dd");
								content.append(df.format(date));
							} else {
								content.append(aCell.getNumericCellValue());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("解析Excel文件出错", e);
		}

		return content.toString();
	}
	
	public static String getXlsxContent(InputStream is) {
		StringBuffer content = new StringBuffer();
		// 工作簿
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(is);
			// 循环每一个sheet
			for (int numSheets = 0; numSheets < workbook.getNumberOfSheets(); numSheets++) {
				// 获得一个sheet
				XSSFSheet aSheet = workbook.getSheetAt(numSheets);
				content.append("\n");
				if (null == aSheet) {
					continue;
				}
				// 循环每一行
				for (int rowNum = 0; rowNum <= aSheet.getLastRowNum(); rowNum++) {
					content.append("\n");
					// 得到某一行
					XSSFRow aRow = aSheet.getRow(rowNum);
					if (null == aRow) {
						continue;
					}
					// 循环每一列
					for (int cellNum = 0; cellNum <= aRow.getLastCellNum(); cellNum++) {
						// 得到每一列
						XSSFCell aCell = aRow.getCell(cellNum);
						if (null == aCell) {
							continue;
						}
						// 如果是字符串类型
						if (aCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
							content.append(aCell.getRichStringCellValue()
									.getString());
							// 否则，如果是数值类型
						} else if (aCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
							// 如果Cell的Type为CELL_TYPE_NUMERIC时，还需要进一步判断该Cell的数据格式，
							// 因为它有可能是Date类型，在Excel中的Date类型也是以Double类型的数字存储的。
							boolean b = HSSFDateUtil.isCellDateFormatted(aCell);
							if (b) {
								Date date = aCell.getDateCellValue();
								SimpleDateFormat df = new SimpleDateFormat(
										"yyyy-MM-dd");
								content.append(df.format(date));
							} else {
								content.append(aCell.getNumericCellValue());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content.toString();
	}
	
	public static void main(String[] args) throws Exception{
		/*FileInputStream fis = new FileInputStream("d:\\工作备份\\PlatformDorado\\other\\问题集.xlsx");
		//String content = getXLSContent(fis);
		String content = getXlsxContent(fis);
		System.out.println(content);*/
	}
}
