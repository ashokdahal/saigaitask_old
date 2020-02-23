/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.excellist;

import static jp.ecom_plat.saigaitask.util.Constants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionException;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionService;
import jp.ecom_plat.saigaitask.util.Constants;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.framework.util.StringUtil;


@org.springframework.stereotype.Service
public class ExcellistService extends BaseService{

	Logger logger = Logger.getLogger(ExcellistService.class);

	@Resource private LoginDataDto loginDataDto;
    @Resource private TrackDataService trackDataService;
    @Resource private TagFunctionService tagFunctionService;

    private int firstColumn;
    private int endColumn;

    /**
     * Excel帳票テンプレートファイルの内容チェックを行う
     * @param excelFile
     * @return エラーメッセージを格納したJSONObject
     */
	public JSONObject check(File excelFile){
		JSONObject json = new JSONObject();
		StringBuffer messageBuf = new StringBuffer();

		try{
		    Workbook book = new XSSFWorkbook(excelFile);
		    // 第一シートのみチェックし第二シート以降は無視
	    	Sheet sheet= book.getSheetAt(0);

	    	// 値が設定された範囲を処理
	    	int startRowIndex = sheet.getFirstRowNum();
	    	if(startRowIndex < 0){
	    		startRowIndex = 0;
	    	}
	    	int endRowIndex = sheet.getLastRowNum();
	    	if(endRowIndex < 0){
	    		endRowIndex = startRowIndex + 1;
	    	}
	    	for(int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++  ){
	    		Row currentRow = sheet.getRow(rowIndex);
	    		if(currentRow == null){
	    			continue;
	    		}

	    		int startCellIndex = currentRow.getFirstCellNum();
	    		int endCellIndex = currentRow.getLastCellNum();

		    	if(startCellIndex < 0 && endCellIndex < 0){
		    		continue;
		    	}
		    	if(startCellIndex < 0){
		    		startCellIndex = 0;
		    	}
		    	if(endCellIndex < 0){
		    		endCellIndex = startCellIndex + 1;
		    	}

		    	for(int cellIndex = startCellIndex; cellIndex <= endCellIndex; cellIndex++  ){
		    		String value = getCellValue(currentRow.getCell(cellIndex));
		    		String validateResult = validate(value);
		    		if(! StringUtil.isEmpty(validateResult)){
		    			messageBuf.append(lang.__("row"));
		    			messageBuf.append(":");
		    			messageBuf.append(rowIndex+1);
		    			messageBuf.append(",");
		    			messageBuf.append(lang.__("column"));
		    			messageBuf.append(":");
		    			messageBuf.append(cellIndex+1);
		    			messageBuf.append("[");
		    			messageBuf.append(value);
		    			messageBuf.append("]");
		    			messageBuf.append("\n");
		    			messageBuf.append("  ");
		    			messageBuf.append(validateResult);
		    			messageBuf.append("\n");
		    		}
		    	}
	    	}
	    	book.close();

		}catch(InvalidFormatException fe){
			// XLSX形式ではなかった
			json.put(JQG_MESSAGE, lang.__("The excel template file is not XSLX format."));
		}catch(IOException ie){
			// ファイルを読み込めなかった
			json.put(JQG_MESSAGE, lang.__("Failed to read the uploaded excel template file.<!--2-->"));

		}catch(Exception e){
			// その他
			json.put(JQG_MESSAGE, lang.__("Failed to read the uploaded excel template file.<!--2-->") + "\n" + e.getMessage());
		}

		if(messageBuf.length() > 0){
			json.put(JQG_MESSAGE, messageBuf.toString());
		}

		return json;
	}

	public void  convertLayerId(File excelFile, HashMap<String, String> layerIdMap, Map<Long, Long> filterIdTable) throws Exception{

		FileOutputStream out = null;
		try{
			InputStream ins = new FileInputStream(excelFile);
		    Workbook book = WorkbookFactory.create( ins );
		    // 第一シートのみチェックし第二シート以降は無視
	    	Sheet sheet= book.getSheetAt(0);

	    	// 値が設定された範囲を処理
	    	int startRowIndex = sheet.getFirstRowNum();
	    	if(startRowIndex < 0){
	    		startRowIndex = 0;
	    	}
	    	int endRowIndex = sheet.getLastRowNum();
	    	if(endRowIndex < 0){
	    		endRowIndex = startRowIndex + 1;
	    	}
	    	for(int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++  ){
	    		Row currentRow = sheet.getRow(rowIndex);
	    		if(currentRow == null){
	    			continue;
	    		}

	    		int startCellIndex = currentRow.getFirstCellNum();
	    		int endCellIndex = currentRow.getLastCellNum();

		    	if(startCellIndex < 0 && endCellIndex < 0){
		    		continue;
		    	}
		    	if(startCellIndex < 0){
		    		startCellIndex = 0;
		    	}
		    	if(endCellIndex < 0){
		    		endCellIndex = startCellIndex + 1;
		    	}

		    	for(int cellIndex = startCellIndex; cellIndex <= endCellIndex; cellIndex++  ){
		    		String value = getCellValue(currentRow.getCell(cellIndex));
    				String newValue = value;
		    		if(tagCheck(value) == TAGCHECK_SUCCESS){
		    			// レイヤIDの変換対象タグはval()のみ
		    			if(value.toLowerCase().indexOf("val(") >= 0){
		    				// 正規表現でレイヤIDを抜き出して置換
		    				String regex = "\"[a-zA-Z]+[0-9]+\"";
		    				Pattern p = Pattern.compile(regex);
		    				Matcher m = p.matcher(value);
		    				while(m.find()){
		    					String matchstr = m.group();
		    					String layerId = matchstr.replaceAll("\"", "");
		    					String newLayerId = layerIdMap.get(layerId);
		    					String newLayerIdStr = "\"" + newLayerId + "\"";
		    					newValue = newValue.replaceAll(matchstr, newLayerIdStr);
		    				}
		    				currentRow.getCell(cellIndex).setCellValue(newValue);
		    			}

		    			// フィルタID変換
		    			if(value.toLowerCase().indexOf("filter(") >= 0){
		    				// 正規表現でフィルタ関数を抜き出して置換
		    				String regex = "filter\\(\\s*[0-9]+\\s*\\)";
		    				Pattern p = Pattern.compile(regex);
		    				Matcher m = p.matcher(value);
		    				while(m.find()){
		    					String matchstr = m.group();
		    					String filterId = matchstr.replaceAll("filter\\(", "").replaceAll("\\)", "").trim();
		    					String newFilterId = filterIdTable.get(Long.parseLong(filterId)).toString();
		    					String newFilterFuncStr = "filter(" + newFilterId + ")";
		    					newValue = newValue.replace(matchstr, newFilterFuncStr);
		    				}
		    				currentRow.getCell(cellIndex).setCellValue(newValue);
		    			}
		    		}
		    	}
	    	}
	    	out = new FileOutputStream(excelFile);
	    	book.write(out);
	    	book.close();
		    ins.close();

		}catch(Exception e){
			throw e;
		}

	}

	/**
	 * セルの値を取得する
	 * @param cell
	 * @return セルの値の文字列。セルが数式の場合は数式の文字列。
	 */
	private String getCellValue(Cell cell){
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			return Double.toString(cell.getNumericCellValue());
		case Cell.CELL_TYPE_BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		case Cell.CELL_TYPE_BLANK:
			return "";
		default:
			return null;
		}
	}

//	/**
//	 * セルの関数実行結果を返す
//	 * @param cell
//	 * @return 実行結果の文字列。
//	 */
//	private String getStringFormulaValue(Cell cell){
//		Workbook book = cell.getSheet().getWorkbook();
//		CreationHelper helper = book.getCreationHelper();
//		FormulaEvaluator evaluator = helper.createFormulaEvaluator();
//		CellValue value = evaluator.evaluate(cell);
//		switch (value.getCellType()) {
//		case Cell.CELL_TYPE_STRING:
//			return value.getStringValue();
//		case Cell.CELL_TYPE_NUMERIC:
//			return Double.toString(value.getNumberValue());
//		case Cell.CELL_TYPE_BOOLEAN:
//			return Boolean.toString(value.getBooleanValue());
//		default:
//			System.out.println(value.getCellType());
//			return null;
//		}
//	}

	/**
	 * セル文字列値の内容確認を行う
	 * @param value
	 * @return エラーメッセージ
	 */
	private String validate(String value){

		String errorMessages = "";

		if(StringUtil.isEmpty(value)){
			return errorMessages;
		}

		int tagCheckResult = tagCheck(value);
		switch (tagCheckResult) {
		case Constants.TAGCHECK_NOTAG:
			break;
		case Constants.TAGCHECK_SUCCESS:
			// タグを切り出す
			List<String> tags = getTagList(value);
			for(String tagText: tags){
				try{
					String tagCheckEcommapResult = tagCheckEcommap(tagText);
					if(!StringUtil.isEmpty(tagCheckEcommapResult)){
						errorMessages = tagCheckEcommapResult;
					}
				}catch(ServiceException e){
					errorMessages = e.getMessage();
				}
			}

			break;
		case Constants.TAGCHECK_NOSTART:
			errorMessages = lang.__("No start tag.");
			break;
		case Constants.TAGCHECK_NOEND:
			errorMessages = lang.__("No end tag.");
			break;
		default:
			errorMessages = lang.__("Invalid tag.");
			break;
		}

		return errorMessages;
	}


	/**
	 * 文字列がExcel帳票テンプレートのタグかのチェックを行う
	 * @param value
	 * @return 0:タグなし。1:正常タグあり。2:開始タグがない。3:終了タグがない。4:不正なタグ
	 */
	private int tagCheck(String text){

		if(StringUtil.isEmpty(text)){
			return Constants.TAGCHECK_NOTAG;
		}

		if(text.indexOf("<%>") >= 0) {
			return Constants.TAGCHECK_INVALID;
		}

		int startTagFirstIndex = text.indexOf(Constants.EXCELLIST_TAGSTARTSTR);
		int endTagFirstIndex = text.indexOf(Constants.EXCELLIST_TAGENDSTR);

		if(startTagFirstIndex >= 0 && endTagFirstIndex >= 0){
			List<List<Integer>> indexes = getTagIndexList(text);
			List<Integer> startTagIndexes = indexes.get(0);
			List<Integer> endTagIndexes = indexes.get(1);

			// 開始タグと終了タグの位置関係が正しいこと。入れ子は認めない。
			for(int i = 0; i < startTagIndexes.size(); i++){
				if(startTagIndexes.get(i) > endTagIndexes.get(i)){
					return Constants.TAGCHECK_NOSTART;
				}
			}
			return Constants.TAGCHECK_SUCCESS;
		}else if (startTagFirstIndex >= 0 && endTagFirstIndex < 0){
			return Constants.TAGCHECK_NOEND;
		}else if (startTagFirstIndex < 0 && endTagFirstIndex >= 0){
			return Constants.TAGCHECK_NOSTART;
		}else{
			return Constants.TAGCHECK_NOTAG;
		}
	}

	/**
	 * エクセル帳票テンプレートタグの内容チェックを行う
	 * @param text
	 * @return
	 */
	private String tagCheckEcommap(String tagStr){
		String message = "";

		List<String> tagContentsPre = new ArrayList<String>();
		tagContentsPre.add(tagStr);
		tagFunctionService.prepare(tagContentsPre);
		List<String> tagContentsResult = tagFunctionService.getPage(0);
		if(tagContentsResult == null){

		}else{
			// TODO:平時にアップロードされた場合、レイヤIDや属性
			String result = tagContentsResult.get(0);
			if(result.indexOf(lang.__(TagFunctionException.PARSE_ERROR)) >= 0){
				message = lang.__("Failed to analyze tags.");
			}else if(result.indexOf(lang.__(TagFunctionException.INTERNAL_ERROR)) >= 0){
				message = lang.__("Internal error occur in case to analyze tags.");
			}else if(result.indexOf(lang.__(TagFunctionException.INCOMPATIBLE_DATATYPE)) >= 0){
				message = lang.__("Invalid data format.");
			}else if(result.indexOf(lang.__(TagFunctionException.INVALID_LAYER)) >= 0){
				message = lang.__("Invalid layer ID.");
			}else if(result.indexOf(lang.__(TagFunctionException.INVALID_ATTR)) >= 0){
				message = lang.__("Invalid attribute ID.");
			}else if(result.indexOf(lang.__(TagFunctionException.INVALID_FILTERID)) >= 0){
				message = lang.__("Invalid filter ID.");
			}else if(result.indexOf(lang.__(TagFunctionException.DATA_ERROR)) >= 0){
				message = lang.__("Invalid data format<!--2-->");
			}
		}

		return message;
	}

	public File createExcellist(File templateFile, Long menuinfoid, JSONObject userinputsJson, Date datetime) throws IOException, FileNotFoundException{
		// 帳票ファイル名
		String dateStr = new SimpleDateFormat("yyyyMMddHHmm").format(datetime);
		String excellistFileName = Constants.MENUINFOID_EXCELLIST_FILENAMEPREFIX
				+ loginDataDto.getLocalgovinfoid()
				+ "_"
				+ loginDataDto.getGroupid()
				+ "_"
				+ menuinfoid
				+ "_"
				+ dateStr
				+ "_"
				+ UUID.randomUUID().toString();

		// テンプレートファイルを帳票ファイルにコピー
		File workDir = templateFile.getParentFile();
		File excellistFile = new File(workDir, excellistFileName);
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try{
			inStream = new FileInputStream(templateFile);
			outStream = new FileOutputStream(excellistFile);
			inChannel = inStream.getChannel();
			outChannel = outStream.getChannel();
			try {
				inChannel.transferTo(0, inChannel.size(),outChannel);
			}catch (IOException e) {
				throw e;
			}finally {
				if (inChannel != null){
					inChannel.close();
				}
				if (outChannel != null){
					outChannel.close();
				}
				if (inStream != null){
					inStream.close();
				}
				if (outStream != null){
					outStream.close();
				}
			}
		}catch(FileNotFoundException e){
			throw e;
		}

		// 帳票ファイルのタグを展開
		try{
			if( ! printExcellistfile(excellistFile, userinputsJson, datetime)){
				excellistFile = null;
			}
		}catch(Exception e){
			if(excellistFile.exists()){
				excellistFile.delete();
			}
			excellistFile = null;
		}


		return excellistFile;
	}

    private void ensureColumnBounds(Sheet sheet) {

        Iterator<Row> iter = sheet.rowIterator();
        firstColumn = (iter.hasNext() ? Integer.MAX_VALUE : 0);
        endColumn = 0;
        while (iter.hasNext()) {
            Row row = iter.next();
            short firstCell = row.getFirstCellNum();
            if (firstCell >= 0) {
                firstColumn = Math.min(firstColumn, firstCell);
                endColumn = Math.max(endColumn, row.getLastCellNum());
            }
        }
    }


    private boolean printExcellistfile(File excellistFile, JSONObject userinputsJson, Date datetime) throws IOException{
    	tagFunctionService.initLayerMap();
    	tagFunctionService.setDatetime(datetime);

		FileOutputStream out = null;
		try{
			InputStream ins = new FileInputStream(excellistFile);
		    Workbook book = WorkbookFactory.create( ins );
		    int sheetCount = book.getNumberOfSheets();

		    // 第一シートのみチェックし第二シート以降は無視
	    	Sheet sheet= book.getSheetAt(0);
	    	ensureColumnBounds(sheet);

	        // とりあえず、データが存在する行と列を取得
//	        int printStartRowIndex = 0;
//	        int printStartColumnIndex = 0;
//	        int printEndRowIndex = sheet.getLastRowNum();
//	        int printEndColumnIndex = endColumn;

	        // 印刷範囲が指定されていた場合はその設定値を使用する。
//	        String printArea = book.getPrintArea(book.getSheetIndex(sheet));
//	        if (printArea != null) {
//	            int sheetPosition = printArea.indexOf("!");
//	            if (sheetPosition != -1) {
//	                printArea = printArea.substring(sheetPosition + 1);
//	            } else {
//	                printArea = null;
//	            }
//	        }
//	        if(printArea != null){
//	            // $A$1:$B$2 形式を数値に変換する。
//	            String [] printAreaTempArray = printArea.split(":");
//	            if(printAreaTempArray != null){
//	                String [] printAreaTempArray2 = printAreaTempArray[0].split("\\$");
//	                if(printAreaTempArray2 != null){
//	                	printStartRowIndex = Integer.parseInt(printAreaTempArray2[2].trim()) -1;
//	                	printStartColumnIndex = ExceltohtmlService.convertAtoN(printAreaTempArray2[1].trim()) -1;
//	                }
//	                String [] printAreaTempArray3 = printAreaTempArray[1].split("\\$");
//	                if(printAreaTempArray3 != null){
//	                	printEndRowIndex = Integer.parseInt(printAreaTempArray3[2].trim()) -1;
//	                	printEndColumnIndex = ExceltohtmlService.convertAtoN(printAreaTempArray3[1].trim()) -1;
//	                }
//	            }
//	        }

	        // 改ページ位置を取得
//	        int [] breakRows = sheet.getRowBreaks();

	        // 印刷タイトルを取得
//	        CellRangeAddress titleRange =  sheet.getRepeatingRows();
//	        int titleStartColumn =  -1;
//	        int titleStartRow =  -1;
//	        int titleEndColumn =  -1;
//	        int titleEndRow =  -1;
//	        if(titleRange != null){
//	        	titleStartColumn =  titleRange.getFirstColumn();
//	        	titleStartRow =  titleRange.getFirstRow();
//	        	titleEndColumn =  titleRange.getLastColumn();
//	        	titleEndRow =  titleRange.getLastRow();
//	        }
//	    	int pageRowMax = printEndRowIndex - printStartRowIndex;
//	        int contentSize = (pageRowMax + 1) - (titleEndRow - titleStartRow);


	    	// 値が設定された範囲を処理
//	    	int startRowIndex = sheet.getFirstRowNum();
//	    	if(startRowIndex < 0){
//	    		startRowIndex = 0;
//	    	}
//	    	int endRowIndex = sheet.getLastRowNum();
//	    	if(endRowIndex < 0){
//	    		endRowIndex = startRowIndex + 1;
//	    	}
	        Iterator<Row> rows = sheet.rowIterator();


	    	// 空でないセルのデータを集める
	    	List<String> cellValues = new ArrayList<String>();
	        while (rows.hasNext()) {
//	    	for(int rowIndex = 0; rowIndex <= endRowIndex; rowIndex++  ){
//	    		Row currentRow = sheet.getRow(rowIndex);
	        	Row currentRow = rows.next();
	    		int endCellIndex;
	    		if (currentRow != null && (endCellIndex = currentRow.getLastCellNum()) > 0) {
			    	for(int cellIndex = 0; cellIndex <= endCellIndex; cellIndex++){
			    		Cell currentCell = currentRow.getCell(cellIndex);
			    		String value;
			    		if (currentCell != null &&
			    			(value = getCellValue(currentCell)) != null && !value.isEmpty()) {
			    			cellValues.add(value);
			    		}
			    	}
	    		}
	    	}

	    	// タグの置換え
			tagFunctionService.prepare(cellValues);
			int pageCount = tagFunctionService.getPageCount();
			if (pageCount == 0)
				pageCount = 1;
			List<Boolean> editableList = tagFunctionService.getEditable();

			// ページごとにシートを追加する
			for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
				Sheet newSheet = book.cloneSheet(0);
				book.setSheetName(sheetCount + pageIndex, "Page" + (pageIndex + 1));
				List<String> pageData = tagFunctionService.getPage(pageIndex);
				int fieldCount = 0;
				// 行ごとに実行
				int rowIndex = 0;
		        rows = newSheet.rowIterator();
		        while (rows.hasNext()) {
//		    	for(int rowIndex = 0; rowIndex < endRowIndex; rowIndex++  ){
//		    		Row currentRow = newSheet.getRow(rowIndex);
		    		Row currentRow = rows.next();
//		    		int endCellIndex;
//		    		if (currentRow != null && (endCellIndex = currentRow.getLastCellNum()) > 0) {
		    		if (currentRow != null ) {
		    			// カラムごとに実行
//				    	for(int cellIndex = 0; cellIndex <= endCellIndex; cellIndex++){
				    	for(int cellIndex = firstColumn; cellIndex < endColumn; cellIndex++){
				    		Cell currentCell = currentRow.getCell(cellIndex);
				    		String value;
				    		if (currentCell != null &&
				    			(value = getCellValue(currentCell)) != null && !value.isEmpty()) {
				    			if(fieldCount >= pageData.size()){
				    				break;
				    			}
				    			// editable 指定がある場合
		    		            if(editableList.get(fieldCount) && userinputsJson != null){
		    		            	String userinput = userinputsJson.getString(Constants.EXCELLIST_USERINPUTPREFIX +  "_" + pageIndex +  "_" + rowIndex + "_" + cellIndex);
		    		            	currentCell.setCellValue(userinput);
		    		            	// セル内改行を有効にしておく
		    		            	CellStyle cs = currentCell.getCellStyle();
		    		            	cs.setWrapText(true);
		    		            	fieldCount++;
		    		            }
		    		            else {
		    		            	// 数式の場合は数式そのものをコピーしておく
		    		            	if(currentCell.getCellType() == Cell.CELL_TYPE_FORMULA){
			    		            	currentCell.setCellFormula(pageData.get(fieldCount++));
		    		            	}else{
			    		            	currentCell.setCellValue(pageData.get(fieldCount++));
		    		            	}
		    		            }
				    		}
				    	}
		    		}
		    		rowIndex++;
		    	}
			}

			// 先頭にあるテンプレートシートを削除する
			for (int i = 0; i < sheetCount; i++)
				book.removeSheetAt(0);

	    	out = new FileOutputStream(excellistFile);
	    	book.write(out);
	    	book.close();
		    ins.close();
	    	return true;

		}
		catch(InvalidFormatException fe){
			// XLSX形式ではなかった
    		logger.error("ExcellistService.printExcellistfile not XLSX file.",fe);
			return false;
		}catch(IOException ie){
			// ファイルを読み込めなかった
    		logger.error("ExcellistService.printExcellistfile file read error.",ie);
			return false;
		}catch(Exception e){
			// 処理中に何らかのエラー。
    		logger.error("ExcellistService.printExcellistfile exceptiom.",e);
			return false;
		}finally{
			if(out != null ){
				out.close();
			}
		}
	}

	/**
	 * 文字列中のExcel帳票テンプレートタグのインデックスリストを作成する
	 * @param text
	 * @return
	 */
	private List<List<Integer>>  getTagIndexList(String text){

		List<List<Integer>> retList = new ArrayList<List<Integer>>();
		List<Integer> startTagIndexes = new ArrayList<Integer>();
		List<Integer> endTagIndexes = new ArrayList<Integer>();

		int textLength = text.length();
		int startTagFirstIndex = text.indexOf(Constants.EXCELLIST_TAGSTARTSTR);
		int endTagFirstIndex = text.indexOf(Constants.EXCELLIST_TAGENDSTR);

		startTagIndexes.add(startTagFirstIndex);
		endTagIndexes.add(endTagFirstIndex);

		// 開始タグを検索
		for(int i = startTagFirstIndex + Constants.EXCELLIST_TAGSTARTSTR.length(); i < textLength ; i++){
			int partStartIndex = i;
			int partEndIndex = i + Constants.EXCELLIST_TAGSTARTSTR.length();
			if(partEndIndex >= (textLength -1)){
				partEndIndex = textLength -1;
			}
			String textPart = text.substring(partStartIndex, partEndIndex);
			if(textPart.indexOf(Constants.EXCELLIST_TAGSTARTSTR) >= 0){
				startTagIndexes.add(partStartIndex);
			}
		}

		// 終了タグを検索
		for(int i = endTagFirstIndex + Constants.EXCELLIST_TAGENDSTR.length(); i < textLength ; i++){
			int partStartIndex = i;
			int partEndIndex = i + Constants.EXCELLIST_TAGSTARTSTR.length();
			if(partEndIndex >= (textLength)){
				partEndIndex = textLength;
			}
			String textPart = text.substring(partStartIndex, partEndIndex);
			if(textPart.indexOf(Constants.EXCELLIST_TAGENDSTR) >= 0){
				endTagIndexes.add(partStartIndex);
			}
		}

		retList.add(startTagIndexes);
		retList.add(endTagIndexes);

		return retList;
	}

	/**
	 * 文字列中のExcel帳票テンプレートタグのリストを作成する
	 * @param text
	 * @return
	 */
	private List<String> getTagList(String text){
		List<String> retValue = new ArrayList<String>();
		if(tagCheck(text) == Constants.TAGCHECK_SUCCESS){

			List<List<Integer>> tagIndexes = getTagIndexList(text);
			List<Integer> startTagIndexes = tagIndexes.get(0);
			List<Integer> endTagIndexes = tagIndexes.get(1);

			for(int i = 0; i < startTagIndexes.size(); i++){
				retValue.add(text.substring(startTagIndexes.get(i), endTagIndexes.get(i) + 2));
			}
		}

		return retValue;

	}
}
