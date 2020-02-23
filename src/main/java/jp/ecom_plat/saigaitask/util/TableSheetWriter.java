/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * エクセルファイルにテーブルシートを追加するクラスです.
 */
public class TableSheetWriter {

	/**
	 * {@link TableSheetWriter}でエクスポートするデータを行単位で取得するためのインタフェース
	 */
	public static interface TableSheetRow extends Iterable<TableSheetRow>, Iterator<TableSheetRow> {
	
		/**
		 * 現在の行の指定カラムの値を取得する.
		 * @param column 値を取得したいカラム名
		 * @return 指定したカラムの値
		 */
		public Object getColumnValueOnCurrentRow(String column);
	
		/**
		 * @return {@link TableSheetRow}のためのイテレータ
		 */
		public Iterator<TableSheetRow> iterator();
	
		/**
		 * 次の行{@link TableSheetRow}があるかどうか
		 * @return 次の行があるなら true
		 */
		public boolean hasNext();
	
		/**
		 * @return 次の行{@link TableSheetRow}
		 */
		public TableSheetRow next();
	
		/**
		 * 特に処理しない
		 */
		public void remove();
	}

	// Logger
	Logger logger = Logger.getLogger(TableSheetWriter.class);

	HSSFWorkbook wb = null;
	HSSFSheet sheet = null;
	String sheetName = null;
	List<String> columns = null;

	/**
	 * データをエクスポートしないカラム
	 * null の場合は columns でデータエクスポートする
	 */
	public Map<String, Boolean> skipDataExport = null;
	int rowIdx = 0;
	int cellNum = 0;

	/**
	 * @param wb エクセルWorkbook
	 * @param sheetName シート名称
	 * @param columns エクスポートするカラムのリスト
	 */
	public TableSheetWriter(HSSFWorkbook wb, String sheetName, List<String> columns) {
		this.wb = wb;
		this.sheetName = sheetName;
		this.columns = columns;

		// CSVシートにデータを書き込み
		sheet = wb.getSheet(sheetName);
		if(sheet==null) sheet = wb.createSheet(sheetName);

		// ヘッダの出力
		if(true) {
			HSSFRow row = sheet.createRow(rowIdx++);
			int cellIdx = 0;
			for(String column : columns) {
				row.createCell(cellIdx++).setCellValue(new HSSFRichTextString(column));
			}
			cellNum = cellIdx;
		}
	}

	/**
	 * JSONObjectを１行でエクスポートする.
	 * @param json エクスポート対象のJSONObject
	 */
	public void writeLine(JSONObject json) {
		HSSFRow row = sheet.createRow(rowIdx++);
		// ヘッダとずれないように、ヘッダ定義から値を取得する
		for(String column : columns) {
			// データエクスポート対象外のチェック
			if(skipDataExport!=null && skipDataExport.get(column)==true) continue;

			// 値を取得
			Object val = null;
			if(json.has(column)) {
				try {
					val = json.get(column);
				} catch (JSONException e) {
					logger.error(e.getMessage(), e);
				}
			}

			// セルへ書き込み
			writeCell(row, columns.indexOf(column), val);
		}
	}

	/**
	 * {@link TableSheetWriter.TableSheetRow} を１行でエクスポートする.
	 * @param tableSheetRow エクスポート対象
	 */
	public void writeLine(TableSheetWriter.TableSheetRow tableSheetRow) {
		HSSFRow row = sheet.createRow(rowIdx++);
		// ヘッダとずれないように、ヘッダ定義から値を取得する
		for(String column : columns) {
			// データエクスポート対象外のチェック
			if(skipDataExport!=null && skipDataExport.get(column)==true) continue;

			// 値を取得
			Object val = tableSheetRow.getColumnValueOnCurrentRow(column);

			// セルへ書き込み
			writeCell(row, columns.indexOf(column), val);
		}
	}

	/**
	 * 指定セル位置に値を書き込む.
	 * @param row エクセル行
	 * @param cellIdx セル位置
	 * @param val 書き込む値
	 */
	public void writeCell(HSSFRow row, int cellIdx, Object val) {
		HSSFCell cell = row.createCell(cellIdx);
		String value = val!=null ? val.toString() : null;

		// 値を出力する
		if(value==null) {
			cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
		}
		// 文字列型にいれているコードだと、先頭の0がなくなったりするので、値をみて判断は不可。
		//			else if(StringUtil.isNumber(value)) {
		//				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		//				cell.setCellValue(Double.parseDouble(value));
		//			}
		// TODO: boolean output
		//cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
		else {
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(new HSSFRichTextString((String) value));
		}
	}

	/**
	 * エクスポートが終わった後の後処理を実行する.
	 */
	public void end() {
		// フィルター(not working)
		//sheet.setAutoFilter(new CellRangeAddress(sheet.getFirstRowNum(), sheet.getLastRowNum(), 0, cellNum));

		// 幅を自動調整
		for(int idx=0; idx<cellNum; idx++) {
			sheet.autoSizeColumn(idx);
		}
	}



}