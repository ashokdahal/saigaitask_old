/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package jp.ecom_plat.saigaitask.service.excellist;

import static org.apache.poi.ss.usermodel.CellStyle.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jp.ecom_plat.saigaitask.service.BaseService;
import jp.ecom_plat.saigaitask.service.tagfunction.TagFunctionService;
import jp.ecom_plat.saigaitask.util.Constants;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.framework.util.StringUtil;

@org.springframework.stereotype.Service
public class ExceltohtmlService extends BaseService{
	Logger logger = Logger.getLogger(ExcellistService.class);

	@Resource
	private TagFunctionService tagFunctionService;

	private Workbook wb;
    private boolean gotBounds;
    private int firstColumn;
    private int endColumn;
    private HtmlHelper helper;
    private int pageCount;
    private List<Integer> columnWidthList;

    private List<MergedRegionsInfo> mergedRegionsInfoList;

    private static final String DEFAULTS_CLASS = "excelDefaults";
//    private static final String COL_HEAD_CLASS = "colHeader";
//    private static final String ROW_HEAD_CLASS = "rowHeader";


    private static final Map<Short, String> ALIGN = mapFor(
    		ALIGN_LEFT,              "left",
            ALIGN_CENTER,            "center",
            ALIGN_RIGHT,             "right",
            ALIGN_FILL,              "left",
            ALIGN_JUSTIFY,           "left",
            ALIGN_CENTER_SELECTION, "center");

    private static final Map<Short, String> VERTICAL_ALIGN = mapFor(
            VERTICAL_BOTTOM, "bottom",
            VERTICAL_CENTER, "middle",
            VERTICAL_TOP,     "top");

    private static final Map<Short, String> BORDER = mapFor(
    		BORDER_DASH_DOT,             "dashed 1pt",
    		BORDER_DASH_DOT_DOT,        "dashed 1pt",
    		BORDER_DASHED,               "dashed 1pt",
            BORDER_DOTTED,               "dotted 1pt",
            BORDER_DOUBLE,               "double 3pt",
            BORDER_HAIR,                 "solid 1px",
            BORDER_MEDIUM,               "solid 2pt",
            BORDER_MEDIUM_DASH_DOT,     "dashed 2pt",
            BORDER_MEDIUM_DASH_DOT_DOT, "dashed 2pt",
            BORDER_MEDIUM_DASHED,       "dashed 2pt",
            BORDER_NONE,                 "none",
            BORDER_SLANTED_DASH_DOT,   "dashed 2pt",
            BORDER_THICK,                "solid 3pt",
            BORDER_THIN,                 "solid 1pt");

	private final int MERGED_NOT   = 0;
	private final int MERGED_FIRST = 1;
	private final int MERGED_IN    = 2;
	private final int MERGED_LAST  = 3;

	private final int USERINPUT_MAXLANGTH = 500;


    public void init(String path, Date datetime){

        try {
        	FileInputStream in = new FileInputStream(path);
            Workbook newWb = WorkbookFactory.create(in);
            if (newWb == null)
                throw new NullPointerException("wb");
            this.wb = newWb;
            setupColorMap();

           	tagFunctionService.initLayerMap();
           	tagFunctionService.setDatetime(datetime);
        } catch(FileNotFoundException fe){
            throw new IllegalArgumentException(lang.__("No excel template file exits."), fe);
        } catch (InvalidFormatException ife){
            throw new IllegalArgumentException(lang.__("The excel template file is not XSLX format."), ife);
        }catch(IOException ie){
            throw new IllegalArgumentException(lang.__("Failed to read the uploaded excel template file.<!--2-->"), ie);
        }
    }


    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> mapFor(Object... mapping) {
        Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < mapping.length; i += 2) {
            map.put((K) mapping[i], (V) mapping[i + 1]);
        }
        return map;
    }


    private void setupColorMap() {
        if (wb instanceof HSSFWorkbook)
            helper = new HSSFHtmlHelper((HSSFWorkbook) wb);
        else if (wb instanceof XSSFWorkbook)
            helper = new XSSFHtmlHelper((XSSFWorkbook) wb);
        else
            throw new IllegalArgumentException(
                    "unknown workbook type: " + wb.getClass().getSimpleName());
    }



    public Appendable printPage() throws IOException {

		Appendable output = new StringBuffer();
    	Formatter out = new Formatter(output);

    	try {
            print(out);
            out.close();
//            if (output instanceof Closeable) {
//                Closeable closeable = (Closeable) output;
//                closeable.close();
//            }

        } finally {
//            if (out != null)
//                out.close();
//            if (output instanceof Closeable) {
//                Closeable closeable = (Closeable) output;
//                closeable.close();
//            }
        }

    	return output;
    }

    public int getPageCount(){
    	return this.pageCount;
    }
    private void setPageCount(int pageCount){
    	this.pageCount = pageCount;
    }

    public void print(Formatter out) {
    	loadMergedRegions();
    	ensureColumnBounds(wb.getSheetAt(0));
        printInlineStyle(out);
        printSheets(out);
    }

    private void printInlineStyle(Formatter out) {
        out.format("<style type=\"text/css\">%n");
        printStyles(out);
        out.format("</style>%n");
    }

    public void printStyles(Formatter out) {
        // First, copy the base css
        BufferedReader in = null;
        try {
    		String cssPath = application.getRealPath(Constants.EXCELLIST_BASEDIR + "excelStyle.css");
    		File cssFile = new File(cssPath);
            in = new BufferedReader(new FileReader(cssFile));
            String line;
            while ((line = in.readLine()) != null) {
                out.format("%s%n", line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Reading standard css", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //noinspection ThrowFromFinallyBlock
                    throw new IllegalStateException("Reading standard css", e);
                }
            }
        }


        // now add css for each used style
        Set<CellStyle> seen = new HashSet<CellStyle>();
//        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
        for (int i = 0; i < 1; i++) {
            Sheet sheet = wb.getSheetAt(i);
            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                int cIndex = 0;
                for (Cell cell : row) {
                    CellStyle style = cell.getCellStyle();
                    MergedRegionsInfo mergedRegionsInfo = isMergedCell(cell);
                    if(mergedRegionsInfo != null){
//                    	CellRangeAddress range = mergedRegionsInfo.range;

                		int currentRowIndex = cell.getRowIndex();
                		int currentColumnIndex = cell.getColumnIndex();
                		if(currentRowIndex == 16 && currentColumnIndex == 8){
                			System.out.println("");
                		}

//                		style.setBorderTop(BORDER_NONE);
//                		style.setBorderBottom(BORDER_NONE);
//                		style.setBorderLeft(BORDER_NONE);
//                		style.setBorderRight(BORDER_NONE);
//                		style.setFillPattern(NO_FILL);

                		style.setBorderTop(mergedRegionsInfo.borderTop);
                		style.setBorderBottom(mergedRegionsInfo.borderBottom);
                		style.setBorderLeft(mergedRegionsInfo.borderLeft);
                		style.setBorderRight(mergedRegionsInfo.borderRight);
                		style.setFillPattern(mergedRegionsInfo.fillPattern);

//                		if(range.getFirstRow() == cell.getRowIndex() && range.getFirstColumn() == cell.getColumnIndex()){
//                    		style.setBorderTop(mergedRegionsInfo.borderTop);
//                    		style.setBorderBottom(mergedRegionsInfo.borderBottom);
//                    		style.setBorderLeft(mergedRegionsInfo.borderLeft);
//                    		style.setBorderRight(mergedRegionsInfo.borderRight);
//                    		style.setFillPattern(mergedRegionsInfo.fillPattern);
//                    	}
//
//                		if(range.getFirstRow() == cell.getRowIndex()){
//                    		style.setBorderTop(mergedRegionsInfo.borderTop);
//                		}
//                    	if(range.getLastRow() == cell.getRowIndex()){
//                    		style.setBorderBottom(mergedRegionsInfo.borderBottom);
//                    	}
//
//                		if(range.getFirstColumn() == cell.getColumnIndex()){
//                    		style.setBorderLeft(mergedRegionsInfo.borderLeft);
//                		}
//                    	if(range.getLastColumn() == cell.getColumnIndex()){
//                    		style.setBorderRight(mergedRegionsInfo.borderRight);
//                    	}
                    }

                    if (!seen.contains(style)) {
                        printStyle(out, style, cIndex);
                        seen.add(style);
                    }
                    cIndex++;
                }
            }
        }
    }

    private void printStyle(Formatter out, CellStyle style, int columnIndex) {
        out.format(".%s .%s {%n", DEFAULTS_CLASS, styleName(style));
        styleContents(out, style, columnIndex);
        out.format("}%n");
    }

    private void styleContents(Formatter out, CellStyle style, int columnIndex) {
        styleOut(out, "text-align", style.getAlignment(), ALIGN);
        styleOut(out, "vertical-align", style.getAlignment(), VERTICAL_ALIGN);


        if(style.getWrapText()){
//            styleOutRaw(out, "word-break", "break-all");
            styleOutRaw(out, "word-wrap", "break-word");
            styleOutRaw(out, "overflow-wrap", "break-word");
        }
        if(columnIndex < columnWidthList.size() -1){
            styleOutRaw(out, "width", columnWidthList.get(columnIndex) + "px");
        }else{
            styleOutRaw(out, "width", "0px");
        }
        styleOutRaw(out, "position", "relative");

        fontStyle(out, style);
        borderStyles(out, style);
        helper.colorStyles(style, out);
    }

    private void borderStyles(Formatter out, CellStyle style) {
        styleOut(out, "border-left", style.getBorderLeft(), BORDER);
        styleOut(out, "border-right", style.getBorderRight(), BORDER);
        styleOut(out, "border-top", style.getBorderTop(), BORDER);
        styleOut(out, "border-bottom", style.getBorderBottom(), BORDER);
    }

    private void fontStyle(Formatter out, CellStyle style) {
        Font font = wb.getFontAt(style.getFontIndex());

        if (font.getBoldweight() > HSSFFont.BOLDWEIGHT_NORMAL)
            out.format("  font-weight: bold;%n");
        if (font.getItalic())
            out.format("  font-style: italic;%n");

        int fontheight = font.getFontHeightInPoints();
        if (fontheight == 9) {
            //fix for stupid ol Windows
            fontheight = 10;
        }
        out.format("  font-size: %dpt;%n", fontheight);

        // Font color is handled with the other colors
    }

    private String styleName(CellStyle style) {
        if (style == null)
            style = wb.getCellStyleAt((short) 0);
        StringBuilder sb = new StringBuilder();
        Formatter fmt = new Formatter(sb);
        fmt.format("style_%02x", style.getIndex());
        String fmtStr = fmt.toString();
        fmt.close();
        return fmtStr;
    }

    private <K> void styleOut(Formatter out, String attr, K key, Map<K, String> mapping) {
        String value = mapping.get(key);
        if (value != null) {
            out.format("  %s: %s;%n", attr, value);
        }
    }

    private <K> void styleOutRaw(Formatter out, String attr, String value) {
        if (value != null) {
            out.format("  %s: %s;%n", attr, value);
        }
    }

    private static int ultimateCellType(Cell c) {
        int type = c.getCellType();
        if (type == Cell.CELL_TYPE_FORMULA)
            type = c.getCachedFormulaResultType();
        return type;
    }

    private void printSheets(Formatter out) {
        Sheet sheet = wb.getSheetAt(0);
        printSheet(out, sheet);
    }

    public void printSheet(Formatter out, Sheet sheet) {
        ensureColumnBounds(sheet);
        String page = printSheetContent(sheet);
    	out.format(page);
    }

    private void ensureColumnBounds(Sheet sheet) {
        if (gotBounds)
            return;

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
        gotBounds = true;
        setColumnWidth(sheet);
    }

    private void setColumnWidth(Sheet sheet){
        if (!gotBounds)
            return;

        // カラム幅リスト作成
        float tableWidth = 1000;
        List<Float> columnWidthTempList = new ArrayList<Float>();
        float columnWidthTotal = 0;
        for (int i = 0; i <= endColumn; i++) {
            float columnWidth = sheet.getColumnWidthInPixels(i);
            columnWidthTempList.add(columnWidth);
            columnWidthTotal += columnWidth;
        }
        columnWidthList = new ArrayList<Integer>();
        for(float columnWidth : columnWidthTempList){
        	int rate = Math.round(columnWidth * 100 / columnWidthTotal);
        	columnWidthList.add(Math.round(tableWidth *  rate / 100 ));
        }
    }


    private void loadMergedRegions(){
        Sheet sheet = wb.getSheetAt(0);
        mergedRegionsInfoList = new ArrayList<MergedRegionsInfo>();
    	int size = sheet.getNumMergedRegions();
		for (int i = 0; i < size; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
    		Short useBorderTop = -1;
    		Short useBorderBottom = -1;
    		Short useBorderLeft = -1;
    		Short useBorderRight = -1;
    		Short useFillPattern = -1;
			for(int rowIndex = range.getFirstRow(); rowIndex <= range.getLastRow(); rowIndex++){
				for(int colIndex = range.getFirstColumn(); colIndex <= range.getLastColumn(); colIndex++){
					Cell cell = getCell(sheet, rowIndex, colIndex);
					CellStyle cellStyle = cell.getCellStyle();
					if(cellStyle.getBorderTop() > useBorderTop){
						useBorderTop = cellStyle.getBorderTop();
					}
					if(cellStyle.getBorderBottom() > useBorderBottom){
						useBorderBottom = cellStyle.getBorderBottom();
					}
					if(cellStyle.getBorderLeft() > useBorderLeft){
						useBorderLeft = cellStyle.getBorderLeft();
					}
					if(cellStyle.getBorderRight() > useBorderRight){
						useBorderRight = cellStyle.getBorderRight();
					}
					if(cellStyle.getFillPattern() > useFillPattern){
						useFillPattern = cellStyle.getFillPattern();
					}
				}
			}

			MergedRegionsInfo mergedRegionsInfo = new MergedRegionsInfo(range, useBorderTop, useBorderBottom, useBorderLeft, useBorderRight, useFillPattern);
			mergedRegionsInfoList.add(mergedRegionsInfo);
		}
    }

    private MergedRegionsInfo isMergedCell(Cell cell){
    	int idx = 0;
    	boolean found = false;
    	for(MergedRegionsInfo mergedRegionsInfo : mergedRegionsInfoList){
			if (mergedRegionsInfo.range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				found = true;
				break;
			}
			idx++;
    	}

    	if(found){
    		return mergedRegionsInfoList.get(idx);
    	}else{
    		return null;
    	}
    }

    private MergedRegionsInfo isMergedCell(int rowIndex,  int colIndex){
    	int idx = 0;
    	boolean found = false;
    	for(MergedRegionsInfo mergedRegionsInfo : mergedRegionsInfoList){
			if (mergedRegionsInfo.range.isInRange(rowIndex, colIndex)) {
				found = true;
				break;
			}
			idx++;
    	}

    	if(found){
    		return mergedRegionsInfoList.get(idx);
    	}else{
    		return null;
    	}
    }

    private int getPotisionInMergedCell(MergedRegionsInfo mergedCell,  int rowIndex,  int colIndex){
    	int result = MERGED_NOT;
    	if(mergedCell == null){
    		return result;
    	}

		if (mergedCell.range.isInRange(rowIndex, colIndex)) {
			if(rowIndex == mergedCell.range.getFirstRow() &&
					colIndex == mergedCell.range.getFirstColumn()){
				result = MERGED_FIRST;
			}else if(rowIndex == mergedCell.range.getLastRow() &&
					colIndex == mergedCell.range.getLastColumn()){
				result = MERGED_LAST;
			}else{
				result = MERGED_IN;
			}
		}
    	return result;
    }

    private String printSheetContent(Sheet sheet) {

        StringBuffer headerSbuf = new StringBuffer();
        headerSbuf.append(String.format("<table class=%s>%n", DEFAULTS_CLASS));
        headerSbuf.append(String.format("<tbody>%n"));

        // 印刷範囲を設定する

        // とりあえず、データが存在する行と列を取得
//        int printStartRowIndex = 0;
//        int printStartColumnIndex = 0;
//        int printEndRowIndex = sheet.getLastRowNum();
//        int printEndColumnIndex = endColumn;

        // 印刷範囲が指定されていた場合はその設定値を使用する。
//        String printArea = wb.getPrintArea(wb.getSheetIndex(sheet));
//        if (printArea != null) {
//            int sheetPosition = printArea.indexOf("!");
//            if (sheetPosition != -1) {
//                printArea = printArea.substring(sheetPosition + 1);
//            } else {
//                printArea = null;
//            }
//        }
//        if(printArea != null){
            // $A$1:$B$2 形式を数値に変換する。
//            String [] printAreaTempArray = printArea.split(":");
//            String [] printAreaTempArray2 = printAreaTempArray[0].split("\\$");
//        	printStartRowIndex = Integer.parseInt(printAreaTempArray2[2].trim()) -1;
//        	printStartColumnIndex = convertAtoN(printAreaTempArray2[1].trim()) -1;
//            String [] printAreaTempArray3 = printAreaTempArray[1].split("\\$");
//        	printEndRowIndex = Integer.parseInt(printAreaTempArray3[2].trim()) -1;
//        	printEndColumnIndex = convertAtoN(printAreaTempArray3[1].trim()) -1;
//        }

        // 改ページ位置を取得
//        int [] breakRows = sheet.getRowBreaks();

        // 印刷タイトルを取得
//        CellRangeAddress titleRange =  sheet.getRepeatingRows();
//        int titleStartColumn =  -1;
//        int titleStartRow =  -1;
//        int titleEndColumn =  -1;
//        int titleEndRow =  -1;
//        List<String> headerRows = new ArrayList<String>();
//        if(titleRange != null){
//        	titleStartColumn =  titleRange.getFirstColumn();
//        	titleStartRow =  titleRange.getFirstRow();
//        	titleEndColumn =  titleRange.getLastColumn();
//        	titleEndRow =  titleRange.getLastRow();

//        	headerRows = printRows(sheet, titleStartRow, titleEndRow).get(0);
//        	for(String headerRow : headerRows){
//                headerSbuf.append(headerRow);
//        	}
//        }
//    	int pageRowMax = printEndRowIndex - printStartRowIndex;
//        List<String> contents = printRows(sheet, titleEndRow+1, -1);
//        int contentSize = (pageRowMax + 1) - headerRows.size();
//
//
//        List<List<String>> pages = devideList(contents, contentSize);
        List<List<String>> pages = printRows(sheet/*, titleEndRow+1, -1*/);
        if(pages != null){
        	setPageCount(pages.size());
        }else{
        	setPageCount(0);
        }

        StringBuffer sbuf = new StringBuffer();
//		sbuf.append(String.format("<div id=\"excelTabs\">\n"));
//		sbuf.append(String.format("<ul>\n"));
//        for(int i = 1; i <= pages.size(); i++ ){
//    		String tabId = "exceltab-" + Integer.toString(i);
//    		sbuf.append(String.format("<li><a href=\"#"));
//    		sbuf.append(tabId);
//    		sbuf.append(String.format("\">"));
//    		sbuf.append(i);
//    		sbuf.append(String.format("</a></li>\n"));
//
//        }
//		sbuf.append(String.format("</ul>\n"));

        int tabIndex = 1;
    	for(List<String> page : pages){
    		String tabId = "exceltab-" + Integer.toString(tabIndex);
            sbuf.append(String.format("<div id=\""));
            sbuf.append(String.format(tabId));
            sbuf.append(String.format("\">%n"));

            sbuf.append(headerSbuf.toString());
    		for(String row : page){
                sbuf.append(row);
    		}
            sbuf.append(String.format("</tbody>%n"));
            sbuf.append(String.format("</table>%n"));
            sbuf.append(String.format("</div>%n"));
            tabIndex++;
    	}
//        sbuf.append(String.format("</div>%n"));

        return sbuf.toString();

    }


    private List<List<String>> printRows(Sheet sheet/*, int startIndex, int endIndex*/){
    	List<List<String>> printPages = new ArrayList<List<String>>();

        Iterator<Row> rows = sheet.rowIterator();
        int rowIndex = 0;
       	List<String> printCellContents = new ArrayList<String>();
        while (rows.hasNext()) {
        	Row row = rows.next();

//        	if(rowIndex < startIndex){
//        		rowIndex++;
//        		continue;
//        	}
//        	if(endIndex >= 0 && rowIndex > endIndex){
//        		break;
//        	}

        	// 空でないセルのデータを集める
            for (int i = firstColumn; i < endColumn; i++) {
                CellStyle style = null;
                if (i >= row.getFirstCellNum() && i < row.getLastCellNum()) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        style = cell.getCellStyle();
                        //Set the value that is rendered for the cell
                        //also applies the format
                        CellFormat cf = CellFormat.getInstance(style.getDataFormatString());
                        CellFormatResult result = cf.apply(cell);
                        // 値が?かつセルが数式だった場合、数式そのものを表示
                        if(!StringUtil.isEmpty(result.text) && result.text.equals("?")){
                        	if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
        		                printCellContents.add("=" + cell.getCellFormula());
                        	}
                        }else{
    		                printCellContents.add(result.text);
                        }
                    }
                }
            }
        }

        // タグの置換え
        tagFunctionService.prepare(printCellContents);
        int pageCount = tagFunctionService.getPageCount();
        if (pageCount == 0)
        	pageCount = 1;
        List<Boolean> editableList = tagFunctionService.getEditable();

        // ページごとに実行
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
 	    	List<String> printRows = new ArrayList<String>();
        	List<String> pageData = tagFunctionService.getPage(pageIndex);
        	rows = sheet.rowIterator();
	        rowIndex = 0;
	        int fieldCount = 0;

	        // 行ごとに実行
	        while (rows.hasNext()) {
	        	Row row = rows.next();

//	        	if(rowIndex < startIndex){
//	        		rowIndex++;
//	        		continue;
//	        	}
//	        	if(endIndex >= 0 && rowIndex > endIndex){
//	        		break;
//	        	}

	        	List<ExceltohtmlService.PrintCell> printCells = new ArrayList<ExceltohtmlService.PrintCell>();
//        	List<String> printCellContents = new ArrayList<String>();
//	        List<List<String>> printCellContentsList = new ArrayList<List<String>>();
                List<String> contents = new ArrayList<String>();

                // カラムごとに実行
	            for (int i = firstColumn; i < endColumn; i++) {
	                String content = "&nbsp;";
	                String attrs = "";
	                CellStyle style = null;
	                boolean editable = false;
	                if (i >= row.getFirstCellNum() && i < row.getLastCellNum()) {
	                    Cell cell = row.getCell(i);
	                    if (cell != null) {
	                    	content = pageData.get(fieldCount);
	                    	editable = editableList.get(fieldCount++);
	                        style = cell.getCellStyle();
	                        attrs = tagStyle(cell, style);
	                        if (content.equals("")){
	                            content = "&nbsp;";
	                        }
	                    }
	                }
	                printCells.add(new PrintCell(content, attrs, style, rowIndex, i, editable));
	                contents.add(content);
	            }
//	            tagFunctionService.prepare(printCellContents);
//	            List<Boolean> editableList = tagFunctionService.getEditable();
//	            int pageCount = tagFunctionService.getPageCount();
//            if(pageCount == 0){
//            	List<String> contens = tagFunctionService.getPage(0);
//            	printCellContentsList.add(contens);
//            }else{
//                for(int i = 0; i < pageCount; i++){
//                	List<String> contens = tagFunctionService.getPage(i);
//                	printCellContentsList.add(contens);
//
//                }
//            }
//	            printCellContentsList.add(contents);

//	            for(List<String> contentList : printCellContentsList){
            	StringBuffer sbuf = new StringBuffer();
                if(contents.size() == printCells.size()){
                    int i = 0;
                    int i2 = firstColumn;
                    for(PrintCell printCell :  printCells){
                        String formName = Constants.EXCELLIST_USERINPUTPREFIX + "_" + pageIndex + "_" + rowIndex + "_" + i2;
                    	if(printCell.editable) {
                    		printCell.content = "<textarea class=\"" + Constants.EXCELLIST_USERINPUTPREFIX + "\" id=\""+ formName +"\" cols=\"COLSVALUE\" rows=\"ROWSVALUE\" wrap=\"soft\""
                    				+ " maxlength=\"" + USERINPUT_MAXLANGTH + "\">" + contents.get(i) + "</textarea>";
                    	}else{
                        	printCell.content = contents.get(i);
                    	}
                    	i++;
                    	i2++;
                    }
                }

                sbuf.append(String.format("  <tr>%n"));
                for(PrintCell printCell :  printCells){
                	MergedRegionsInfo mergedCell = isMergedCell(printCell.rowIndex, printCell.colIndex);
                	int potisionInmergedCell = getPotisionInMergedCell(mergedCell, printCell.rowIndex, printCell.colIndex);
                	if(potisionInmergedCell <= MERGED_NOT){
        				if(printCell.content.indexOf("</textarea>") >= 0){
        					printCell.content = printCell.content.replace("COLSVALUE", Integer.toString(1));
        					printCell.content = printCell.content.replace("ROWSVALUE", Integer.toString(1));
        				}
                        sbuf.append(String.format("    <td class=%s %s>%s</td>%n", styleName(printCell.style), printCell.attrs, printCell.content));
                	}else{
                		int colSpan = mergedCell.range.getLastColumn() -  mergedCell.range.getFirstColumn();
                		if(colSpan < 1){
                			colSpan = 1;
                		}else{
                			colSpan += 1;
                		}
                		int rowSpan = mergedCell.range.getLastRow() -  mergedCell.range.getFirstRow();
                		if(rowSpan < 1){
                			rowSpan = 1;
                		}else{
                			rowSpan += 1;
                		}
        				if(printCell.content.indexOf("</textarea>") >= 0){
        					printCell.content = printCell.content.replace("COLSVALUE", Integer.toString(colSpan));
        					printCell.content = printCell.content.replace("ROWSVALUE", Integer.toString(rowSpan));
        				}

                		if(rowSpan > 1){
                			if(potisionInmergedCell == MERGED_FIRST){
                                sbuf.append(String.format("    <td rowspan=%s colspan=%s class=%s %s>%s</td>%n", rowSpan, colSpan, styleName(printCell.style), printCell.attrs, printCell.content));
                			}else{
                				continue;
                			}
                		}else{
                			if(potisionInmergedCell == MERGED_FIRST){
                                sbuf.append(String.format("    <td colspan=%s class=%s %s>%s</td>%n", colSpan, styleName(printCell.style), printCell.attrs, printCell.content));
                			}else{
                				continue;
                			}
                		}
                	}
                }
                sbuf.append(String.format("  </tr>%n"));
                printRows.add(sbuf.toString());

                rowIndex++;
//	            }
	        }

	        printPages.add(printRows);
        }

    	return printPages;
    }

    private String tagStyle(Cell cell, CellStyle style) {


        if (style.getAlignment() == ALIGN_GENERAL) {
            switch (ultimateCellType(cell)) {
            case HSSFCell.CELL_TYPE_STRING:
                return "style=\"text-align: left;\"";
            case HSSFCell.CELL_TYPE_BOOLEAN:
            case HSSFCell.CELL_TYPE_ERROR:
                return "style=\"text-align: center;\"";
            case HSSFCell.CELL_TYPE_NUMERIC:
            default:
                // "right" is the default
                break;
            }
        }
        return "";
    }

    public void close(){
    	try{
        	if(wb != null){
        		wb.close();
        	}
    	}catch(IOException e){
    		logger.error("ExceltohtmlService.close",e);
    	}
    }

    private Cell getCell(Sheet sheet, int rowIndex, int columnIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row != null) {
			Cell cell = row.getCell(columnIndex);
			return cell;
		}
		return null;
    }

    class PrintCell{
        String content;
        String attrs;
        CellStyle style;
        int rowIndex;
        int colIndex;
        boolean editable;

        PrintCell(String content, String attrs, CellStyle style, int rowIndex, int colIndex, boolean editable){
        	this.content = content;
        	this.attrs = attrs;
        	this.style = style;
        	this.rowIndex = rowIndex;
        	this.colIndex = colIndex;
        	this.editable = editable;
        }
    }

    class MergedRegionsInfo{
    	CellRangeAddress range;
    	short borderTop;
    	short borderBottom;
    	short borderLeft;
    	short borderRight;
    	short fillPattern;

    	MergedRegionsInfo(CellRangeAddress range, Short borderTop, Short borderBottom, Short borderLeft, Short borderRight, Short fillPattern){
    		this.range = range;
    		this.borderTop = borderTop;
    		this.borderBottom = borderBottom;
    		this.borderLeft = borderLeft;
    		this.borderRight = borderRight;
    		this.fillPattern = fillPattern;
    	}

    	MergedRegionsInfo(CellRangeAddress range, Cell startCell){
    		this.range = range;
    		CellStyle cs = startCell.getCellStyle();
    		this.borderTop = cs.getBorderTop();
    		this.borderBottom = cs.getBorderBottom();
    		this.borderLeft = cs.getBorderLeft();
    		this.borderRight = cs.getBorderRight();
    		this.fillPattern = cs.getFillPattern();
    	}
    }

    public static int convertAtoN(String aIndex)
    {
        int result = 0;
        if (aIndex == null || aIndex.length() <= 0){
        	return result;
        }

        char[] chars = aIndex.toCharArray();

        int len = aIndex.length() - 1;
        for(char c :  chars){
            int asc = (int)c - 64;
            if (asc < 1 || asc > 26){
            	return 0;
            }
            result += asc * (int)Math.pow((double)26,(double)len--);
        }
        return result;
    }

//    public static  <T> List<List<T>> devideList(List<T> origin, int size) {
//		if (origin == null || origin.isEmpty() || size <= 0) {
//			return Collections.emptyList();
//		}
//
//		int block = origin.size() / size + (origin.size() % size > 0 ? 1 : 0 );
//
//		List<List<T>> devidedList = new ArrayList<List<T>>(block);
//		for (int i = 0; i < block; i ++) {
//			int start = i * size;
//			int end = Math.min(start + size, origin.size());
//			devidedList.add(new ArrayList<T>(origin.subList(start, end)));
//		}
//		return devidedList;
//    }


}
