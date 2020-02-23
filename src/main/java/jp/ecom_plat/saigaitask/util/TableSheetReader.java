/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.action.ServiceException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * エクセルファイルのテーブルシートのReaderクラスです.
 */
public class TableSheetReader {

	Logger logger = Logger.getLogger(TableSheetReader.class);
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	HSSFSheet sheet;
	int rowIdx = 0;

	/**
	 * フィールド名リスト.
	 * 1行目のヘッダーから取得する.
	 */
	public List<String> fieldNames;

	/**
	 * エクセルシートの名称は31文字までという制限があるため、
	 * 長い名前の場合はカットする.
	 * @param name 使いたいシート名
	 * @return シート名
	 */
	public static final String formatSheetName(String name) {
		if(31<name.length()) return name.substring(0, 31); // シート名は31文字まで
		return name;
	}

	/**
	 * 読み込むエクセルシートを初期化する.
	 * @param wb エクセルWorkBook
	 * @param sheetName シート名
	 */
	public TableSheetReader(HSSFWorkbook wb, String sheetName) {
		sheet = wb.getSheet(sheetName);
		if(sheet==null) {
			throw new ServiceException("Excel Sheet not found: "+sheetName);
		}

		// 1行目のヘッダーからカラム情報を取得
		fieldNames = new ArrayList<String>();
		// 行から、1レコード分のデータを取得
		HSSFRow row = getNextRow();
		if(row!=null) {
			for(int colIdx=0; colIdx<row.getLastCellNum(); colIdx++) {
				Object val = readCell(row, colIdx);
				String fieldName = (String) val;
				fieldNames.add(fieldName);
			}
		}
	}

	/**
	 * 次の行を取得する.
	 * @return 次の行
	 */
	public HSSFRow getNextRow() {
		if(rowIdx<=sheet.getLastRowNum()) {
			return sheet.getRow(rowIdx++);
		}
		return null;
	}

	/**
	 * １行読み込み、1件分のレコードを取得する.
	 * @return １レコード
	 */
	public Map<String, Object> readLine() {
		HSSFRow row = getNextRow();
		if(row==null) return null;

		// 行から、1レコード分のデータを取得
		Map<String, Object> record = new HashMap<String, Object>();
		for(int colIdx=0; colIdx<row.getLastCellNum(); colIdx++) {
			Object val = readCell(row, colIdx);
			String fieldName = fieldNames.get(colIdx);
			// 値を保存
			record.put(fieldName, val);
		}

		return record;
	}

	/**
	 * 指定セル位置の値を取得する.
	 * @param row 行
	 * @param colIdx セル位置
	 * @return セルの値
	 */
	public Object readCell(HSSFRow row, int colIdx) {
		HSSFCell cell = row.getCell(colIdx);
		if(cell==null) return null;

		try {
			// get value
			Object val = null;
			switch(cell.getCellType()) {
			case HSSFCell.CELL_TYPE_BLANK:
				val = null;
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				val = cell.getNumericCellValue();
				break;
			default:
				val = cell.getStringCellValue();
				break;
			}

			return val;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Excel sheet read error:")+sheet.getSheetName()+".("+cell.getRowIndex()+","+cell.getColumnIndex()+")", e);
		}
	}
}
