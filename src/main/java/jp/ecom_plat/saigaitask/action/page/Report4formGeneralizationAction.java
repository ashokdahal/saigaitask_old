/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.SummaryListDto;
import jp.ecom_plat.saigaitask.entity.db.GeneralizationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.Reportcontent2Data;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.names.ReportDataNames;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.form.page.Report4formForm;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.db.GeneralizationhistoryDataService;
import jp.ecom_plat.saigaitask.service.db.ReportDataService;
import jp.ecom_plat.saigaitask.service.db.Reportcontent2DataService;
import jp.ecom_plat.saigaitask.service.db.ReportcontentDataService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.util.Config;

/**
 * ４号様式の総括表メニューのアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class Report4formGeneralizationAction extends AbstractPageAction {

	/**
	 * xsl -> pdf 変換用の OpenOffice のホームディレクトリ
	 */
	final static String OFFICE_HOME =  Config.getString("OFFICE_HOME");

	/** ページ種別 */
	public static final String PAGE_TYPE = "report4formGeneralization";

	/** アクションフォーム */
	protected Report4formForm report4formForm;

	/** 記録グループデータサービス */
	@Resource protected TrackgroupDataService trackgroupDataService;
	@Resource protected SummarylistcolumnInfoService summarylistcolumnInfoService;
	@Resource protected ReportDataService reportDataService;
	@Resource protected ReportcontentDataService reportcontentDataService;
	@Resource protected Reportcontent2DataService reportcontent2DataService;
	@Resource protected FileService fileService;
	@Resource protected GeneralizationhistoryDataService generalizationhistoryDataService;

	/** JDBCマネージャ */
	@Resource protected JdbcManager jdbcManager;

	// 集計・総括メニュー
	/** 記録グループ */
	public List<TrackgroupData> trackgroupDatas;
	/** 記録データから、４号様式データリストを取得するマップ */
	public Map<Long, List<ReportData>> trackdataid2reportDatas;

	/** 総括表タブ */
	public List<Report4formForm> report4formForms;

	// 集計表タブ
	/** 集計表タブの表示フラグ */
	public boolean showSummaryTab = false;
	/** 集計表タブで表示するリスト */
	public ListDto listDto;
	/** 集計表タブで表示するリスト */
	public List<ListDto> listDtos;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);;
		model.put("trackgroupDatas", trackgroupDatas);
		model.put("trackdataid2reportDatas",trackdataid2reportDatas);
		model.put("report4formForms",report4formForms);
		model.put("showSummaryTab", showSummaryTab);
		model.put("listDto", listDto);
		model.put("listDtos", listDtos);
		model.put("report4formForm", report4formForm);
	}

	/**
	 * 総括・集計メニュー
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/report4formGeneralization","/page/report4formGeneralization/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute Report4formForm report4formForm) {
		this.report4formForm = report4formForm;
		initPage(PAGE_TYPE, report4formForm);
		long preftrackdataid = loginDataDto.getTrackdataid();

		// 記録グループを取得
		trackgroupDatas = trackgroupDataService.findByPreftrackdataid(preftrackdataid);

		// 災害グループ各市町村の４号様式を取得
		trackdataid2reportDatas = new HashMap<Long, List<ReportData>>();
		for(TrackgroupData trackgroupData : trackgroupDatas) {
			// 災害グループ各市町村の記録データ
			TrackData cityTrackData = trackgroupData.cityTrackData;
			List<ReportData> reportDatas = reportDataService.findByTrackdataid(cityTrackData.id);
			trackdataid2reportDatas.put(cityTrackData.id, reportDatas);
		}

		// 集計表タブの表示フラグを設定
		List<SummarylistcolumnInfo> summarylistcolumnInfos = summarylistcolumnInfoService.findByMenuid(report4formForm.menuid);
		showSummaryTab = 0<summarylistcolumnInfos.size();

		setupModel(model);
		return "/page/report4formGeneralization/index";
	}

	/**
	 * タブの初期化
	 * @return null(フォワード先しない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/tabInitialize")
	public String tabInitialize(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
		try {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(lang.__("Now loading..<!--2-->"));
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(),e);
		}
		return null;
	}

	/**
	 * 総括表タブ
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/generalizationTab")
	public String generalizationTab(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
		report4formForms = new ArrayList<Report4formForm>();

		// ４号様式のデータをIDから取得
		if(report4formForm.reportdataids!=null) {
			for(String reportdataid : report4formForm.reportdataids) {
				ReportData reportData = null;
				// reportdataid で検索
				if(StringUtil.isNotEmpty(reportdataid)) {
					reportData = reportDataService.findById(Long.parseLong(reportdataid));
				}

				if(reportData!=null) {
					report4formForm = new Report4formForm();
					Beans.copy(reportData.reportcontent2Datas.get(0), report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					Beans.copy(reportData.reportcontentDatas.get(0), report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					Beans.copy(reportData, report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					report4formForms.add(report4formForm);
				}
			}

		}
		setupModel(model);
		return "/page/report4formGeneralization/generalization-tab";
	}

	/**
	 * 総括表タブ CSV出力
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/generalizationTabCsv")
	public String generalizationTabCsv(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	HSSFWorkbook wb = createGeneralizationXsl();

		//出力用一時ファイル名を時間で作成
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(time);
		try {
			//HTTPレスポンスヘッダの定義
			HttpServletResponse httpServletResponse = response;
			httpServletResponse.setContentType("application/octet-stream");
			httpServletResponse.setHeader("Content-disposition","attachment; filename=\""+now+".xls\"");
			wb.write(response.getOutputStream());

		}catch(IOException e){
			logger.error(loginDataDto.logInfo(),e);
			throw new ServiceException(e);
		}
		return null;
	}

	/**
	 * 総括表タブ PDF出力
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/generalizationTabPdf")
	public String generalizationTabPdf(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	//出力用一時ファイル名を時間で作成
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(time);
		try {
			// PDF ファイルの作成
			File pdfFile = createGeneralizationPDF();

			//HTTPレスポンスヘッダの定義
			HttpServletResponse httpServletResponse = response;
			httpServletResponse.setContentType("application/octet-stream");
			httpServletResponse.setHeader("Content-disposition","attachment; filename=\""+now+".pdf\"");

			// response
			OutputStream out2 = httpServletResponse.getOutputStream();
			InputStream input = new FileInputStream(pdfFile);
			byte[] buf = new byte[512*1024];
			try {
				while(input.read(buf)!=-1) {
					out2.write(buf);
				}
			} finally {
				input.close();
			}
		}catch(IOException e){
			logger.error(loginDataDto.logInfo(),e);
			throw new ServiceException(e);
		}
		setupModel(model);
		return null;
	}

	/**
	 *
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/generalizationTabSave")
	public String generalizationTabSave(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

		report4formForm.pagetype = PAGE_TYPE;
		String listid = "generalizationlist";

		long localgovinfoid = loginDataDto.getLocalgovinfoid();

		// 登録日時
		Timestamp registtime = new Timestamp(System.currentTimeMillis());

		// 履歴保存
		GeneralizationhistoryData generalizationhistoryData = new GeneralizationhistoryData();
		generalizationhistoryData.trackdataid = loginDataDto.getTrackdataid();
		//generalizationhistoryData.menutypeid = menutypeid;
		generalizationhistoryData.pagetype = report4formForm.pagetype;
		generalizationhistoryData.listid = listid;
		generalizationhistoryData.registtime = registtime;
		generalizationhistoryDataService.insert(generalizationhistoryData);

		// ファイル名
		String filename = generalizationhistoryData.id
				+ "-" + report4formForm.pagetype + "-" + listid
				+ "-" + new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.JAPANESE).format(registtime);

		// xsl を作成
		HSSFWorkbook wb = createGeneralizationXsl();

		// xsl 生成
		File xslFile = saveGeneralizationXsl(localgovinfoid, filename, wb);
		String xslRelativePath = fileService.getFileRealRelativePath(xslFile);
		if(xslRelativePath!=null) {
			generalizationhistoryData.csvpath = xslRelativePath;
		}

		// PDF 保存
		File pdfFile = createGeneralizationPDF(wb);
		if(pdfFile.exists()) {
			// 保存場所へ移動
			File savePdfFile = fileService.createHistoryFile(localgovinfoid, filename, "pdf");
			if(savePdfFile.delete()) {
				pdfFile.renameTo(savePdfFile);
				generalizationhistoryData.pdfpath = savePdfFile.getAbsolutePath();
				String pdfRelativePath = fileService.getFileRealRelativePath(savePdfFile);
				if(pdfRelativePath!=null) {
					generalizationhistoryData.pdfpath = pdfRelativePath;
				}
			}
		}

		// パスを更新
		generalizationhistoryDataService.update(generalizationhistoryData);

//		// 登録分の情報を取得
//		JSONObject json = createHistoryItem(generalizationhistoryData);
//		try {
//			response.getWriter().println(json.toString());
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}

		setupModel(model);
		return null;
	}

	protected File createGeneralizationPDF() {
		HSSFWorkbook wb = createGeneralizationXsl();
		return createGeneralizationPDF(wb);
	}

	protected File createGeneralizationPDF(HSSFWorkbook wb) {
		try {
			// 一時ファイルに保存
			File xlsFile = File.createTempFile("report4generalization", ".xls");
			FileOutputStream xlsOut = new FileOutputStream(xlsFile);
			try {
				wb.write(xlsOut);
			} finally {
				xlsOut.close();
			}

			// PDF
			File pdfFile = new File(xlsFile.getAbsolutePath()+".pdf");

			// xsl to pdf
			DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
			if(StringUtil.isEmpty(OFFICE_HOME)) throw new ServiceException(lang.__("OpenOffice home directory (OFFICE_HOME) has not been set."));
			config.setOfficeHome(OFFICE_HOME);
			OfficeManager officeManager = config.buildOfficeManager();
			officeManager.start();
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
			DocumentFormat outputFormat = new DefaultDocumentFormatRegistry().getFormatByExtension("pdf");
			converter.convert(xlsFile, pdfFile, outputFormat);
			officeManager.stop();

			return pdfFile;
		}catch(IOException e){
			logger.error(loginDataDto.logInfo(),e);
			throw new ServiceException(e);
		}

	}

	/**
	 * ４号様式の総括表を作成
	 * @return
	 */
	protected HSSFWorkbook createGeneralizationXsl() {
		// ４号様式データ
		List<ReportData> reportDatas = new ArrayList<ReportData>();

		// ４号様式のデータをIDから取得
		if(report4formForm.reportdataids!=null) {
			for(String reportdataid : report4formForm.reportdataids) {
				ReportData reportData = null;
				// reportdataid で検索
				if(StringUtil.isNotEmpty(reportdataid)) {
					reportData = reportDataService.findById(Long.parseLong(reportdataid));
					reportDatas.add(reportData);
				}
			}
		}

		//テンプレートファイルの取得
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String templatePath = fileService.getTemplateRoot()+rb.getString("REPORT_TEMPLATE_PATH_GENERALIZATION");
		File file = new File(templatePath);
		if(!file.exists()){
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("No template file ["+rb.getString("REPORT_TEMPLATE_PATH_GENERALIZATION")+"]");
			throw new ServiceException("No template file ["+rb.getString("REPORT_TEMPLATE_PATH_GENERALIZATION")+"]");
		}

		//ファイルを開く
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			// CSVシートにデータを書き込み
			final String SHEET_NAME="csv";
			{
				HSSFSheet sheet = wb.getSheet(SHEET_NAME);
				if(sheet==null) sheet = wb.createSheet(SHEET_NAME);
				int rowIdx = 0;

				// ヘッダの定義
				// Reportcontent2DataNames.pref() だと、pref となりクラス名が入らないので、
				// ReportDataNames.reportcontent2Data().pref() のように、 reportcontent2Data.pref となるように
				// もう１階層上から定義させる
				List<PropertyName<?>> propertyNames = new ArrayList<PropertyName<?>>();
				propertyNames.add(ReportDataNames.reportcontent2Datas().pref());
				propertyNames.add(ReportDataNames.reportcontent2Datas().city());
				propertyNames.add(ReportDataNames.reportcontent2Datas().disastername());
				propertyNames.add(ReportDataNames.reportcontent2Datas().reportno());
				propertyNames.add(ReportDataNames.reportcontent2Datas().casualties21());
				propertyNames.add(ReportDataNames.reportcontent2Datas().casualties22());
				propertyNames.add(ReportDataNames.reportcontent2Datas().casualties23());
				propertyNames.add(ReportDataNames.reportcontent2Datas().casualties24());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houseall1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houseall2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houseall3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().househalf1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().househalf2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().househalf3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().housepart1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().housepart2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().housepart3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houseupper1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houseupper2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houseupper3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houselower1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houselower2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().houselower3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().build1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().build2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().field1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().field2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().farm1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().farm2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().school());
				propertyNames.add(ReportDataNames.reportcontent2Datas().hospital());
				propertyNames.add(ReportDataNames.reportcontent2Datas().road());
				propertyNames.add(ReportDataNames.reportcontent2Datas().bridge());
				propertyNames.add(ReportDataNames.reportcontent2Datas().river());
				propertyNames.add(ReportDataNames.reportcontent2Datas().harbor());
				propertyNames.add(ReportDataNames.reportcontent2Datas().landslide());
				propertyNames.add(ReportDataNames.reportcontent2Datas().gabage());
				propertyNames.add(ReportDataNames.reportcontent2Datas().cliff());
				propertyNames.add(ReportDataNames.reportcontent2Datas().railway());
				propertyNames.add(ReportDataNames.reportcontent2Datas().ship());
				propertyNames.add(ReportDataNames.reportcontent2Datas().water());
				propertyNames.add(ReportDataNames.reportcontent2Datas().telephone());
				propertyNames.add(ReportDataNames.reportcontent2Datas().suffer1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().electricity());
				propertyNames.add(ReportDataNames.reportcontent2Datas().gas());
				propertyNames.add(ReportDataNames.reportcontent2Datas().block());
				propertyNames.add(ReportDataNames.reportcontent2Datas().suffer2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().fire1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().fire2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().fire3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount2());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount3());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount4());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount5());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount6());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount7());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount7());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount8());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount9());
				propertyNames.add(ReportDataNames.reportcontent2Datas().amount10());
				propertyNames.add(ReportDataNames.reportcontent2Datas().fireman1());
				propertyNames.add(ReportDataNames.reportcontent2Datas().fireman2());

				// ヘッダの出力
				if(true) {
					HSSFRow row = sheet.createRow(rowIdx++);
					int cellIdx = 0;
					for(PropertyName<?> propertyName : propertyNames) {
						row.createCell(cellIdx++).setCellValue(new HSSFRichTextString(propertyName.toString()));
					}
				}

				// データの出力
				for(ReportData reportData : reportDatas) {
					HSSFRow row = sheet.createRow(rowIdx++);
					int cellIdx = 0;
					// ヘッダとずれないように、ヘッダ定義から値を取得する
					for(PropertyName<?> propertyName : propertyNames) {
						String className = propertyName.toString().split("\\.")[0];
						String fieldName = propertyName.toString().split("\\.")[1];
						String value = null;
						// TODO: ４号様式(その１) 必要なら。
						// ４号様式(その２)
						if(ReportDataNames.reportcontent2Datas().toString().equalsIgnoreCase(className)) {
							Reportcontent2Data reportContent2Data = reportData.reportcontent2Datas.get(0);
							Field field = Reportcontent2Data.class.getField(fieldName);
							Object val = field.get(reportContent2Data);
							value = val!=null ? val.toString() : null;
						}
						// セルへ書き込み
						HSSFCell cell = row.createCell(cellIdx++);

						if(value==null) {
							cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
						}
						else if(StringUtil.isNumber(value)) {
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Double.parseDouble(value));
						}
						// TODO: boolean output
						//cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
						else {
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(new HSSFRichTextString(value));
						}
					}
				}
			}

			// 他のシートを再計算
			try {
				HSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
				// 途中でエラーになると、中断されるので、一括ではやらない
				//evaluator.evaluateAll();
				for(int sheetIdx=0; sheetIdx<wb.getNumberOfSheets(); sheetIdx++) {
					if(SHEET_NAME.equals(wb.getSheetName(sheetIdx))==false) {
						HSSFSheet sheet = wb.getSheetAt(sheetIdx);
						int st = sheet.getFirstRowNum();
						int end = sheet.getLastRowNum();

						//属性出力位置を検索
						for (int i = st; i <= end; i++) {
							HSSFRow row = sheet.getRow(i);
							if (row == null) continue;
							//evaluator.setCurrentRow(row);
							int cs = row.getFirstCellNum();
							int ce = row.getLastCellNum();
							for (int j = cs; j < ce; j++) {
								HSSFCell cell = row.getCell(j);
								if (cell == null) continue;
								if(cell.getCellType()==HSSFCell.CELL_TYPE_FORMULA) {
									try {
										evaluator.evaluateFormulaCell(cell);
									} catch (NotImplementedException e) {
										logger.warn(lang.__("Contains unusable function. ({0})", cell.getCellFormula()), e);
									} catch (Exception e) {
										logger.error(lang.__("Failed to run method. ({0})", cell.getCellFormula()), e);
									} catch (StackOverflowError e) {
										logger.error(lang.__("Nest of method is too deep. ({0})", cell.getCellFormula()), e);
									}
								}
							}
						}
					}
				}
			} catch(Exception e) {
				String msg = lang.__("Unexpected error occurred in calculation.");
				logger.error(msg, e);
				throw new ServiceException(msg, e);
			}

			return wb;

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(),e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存する.
	 * @param wb
	 */
	protected File saveGeneralizationXsl(long localgovinfoid, String filename, HSSFWorkbook wb) {
		// 出力ファイル
		File outFile = fileService.createHistoryFile(localgovinfoid, filename, "xls");
		FileOutputStream out = null;
		try{
			//Excelファイルとして出力
			out = new FileOutputStream(outFile);
			wb.write(out);
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(),e);
			throw new ServiceException(e);
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e2) {
				logger.error(loginDataDto.logInfo(),e2);
				throw new ServiceException(e2);
			}
		}
		return outFile;
	}

	/**
	 * 集計表タブ
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/summaryTab")
	public String summaryTab(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
		// 集計リストDto
		SummaryListDto summaryListDto = new SummaryListDto();
		summaryListDto.columnNames = new ArrayList<String>();
		summaryListDto.columnValues = new ArrayList<List<String>>();

		// 集計項目の設定
		summaryListDto.columnNames.add(lang.__("Local gov. name")); //最初の列はリスト名称で固定
		List<SummarylistcolumnInfo> summarylistcolumnInfos = summarylistcolumnInfoService.findByMenuid(report4formForm.menuid);
		if(summarylistcolumnInfos.size()==0) throw new ServiceException(lang.__("Aggregation item info not configured"));
		for(SummarylistcolumnInfo summarylistcolumnInfo : summarylistcolumnInfos) {
			//summaryListDto.columnNames.add(summarylistcolumnInfo.name);
			SummaryListDto.Calc calc = SummaryListDto.Calc.parse(summarylistcolumnInfo.function, null);
			if(calc!=null) {
				// 条件があれば設定する
				String condition = summarylistcolumnInfo.condition;
				if(StringUtil.isNotEmpty(condition)) calc.condition(condition);
				summaryListDto.calcs.add(calc);
			}
		}

		// ４号様式のデータをIDから取得
		if(report4formForm.reportdataids!=null) {
			for(String reportdataid : report4formForm.reportdataids) {
				ReportData reportData = null;
				// reportdataid で検索
				if(StringUtil.isNotEmpty(reportdataid)) {
					reportData = reportDataService.findById(Long.parseLong(reportdataid));
				}

				if(reportData!=null) {
					Beans.copy(reportData.reportcontent2Datas.get(0), report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					Beans.copy(reportData.reportcontentDatas.get(0), report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
					Beans.copy(reportData, report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();

					// リストを作成
					ListDto listDto = new ListDto();
					listDto.columnNames = new ArrayList<String>();
					listDto.columnValues = new ArrayList<List<String>>();
					List<String> columnValue = new ArrayList<String>();
					Class<Report4formForm> clazz = Report4formForm.class;
					for(Field field : clazz.getFields()) {
						try {
							String name = field.getName();
							String value = (String) field.get(report4formForm);
							listDto.columnNames.add(name);
							columnValue.add(value);
						} catch (IllegalArgumentException e) {
							logger.error(loginDataDto.logInfo(),e);
						} catch (IllegalAccessException e) {
							logger.error(loginDataDto.logInfo(),e);
						} catch (ClassCastException e) {
							logger.error(loginDataDto.logInfo(),e);
						}
					}
					listDto.columnValues.add(columnValue);

					// 集計リストに追加
					LocalgovInfo localgovInfo = reportData.trackData.localgovInfo;
					summaryListDto.addList("", localgovInfo.city, listDto);
				}
			}

		}

		// 集計を実行
		summaryListDto.summary();
		summaryListDto.totalable = true;
		summaryListDto.total();
		this.listDto = summaryListDto;
		listDtos = new ArrayList<ListDto>();
		listDtos.add(summaryListDto);

		// 総括表履歴用
		report4formForm.pagetype = PAGE_TYPE;
		summaryListDto.title = lang.__("Aggregation table");
		summaryListDto.styleId = "summarylist";

		setupModel(model);
		return "/page/externallist/content.jsp";
	}

	/**
	 * 詳細タブ
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/page/report4formGeneralization/detailTab")
	public String detailTab(Map<String,Object>model,
			@Valid @ModelAttribute Report4formForm report4formForm, BindingResult bindingResult) {
		this.report4formForm = report4formForm;
		ReportData reportData = null;
		// reportdataid で検索
		if(StringUtil.isNotEmpty(report4formForm.reportdataid)) {
			reportData = reportDataService.findById(Long.parseLong(report4formForm.reportdataid));
		}

		if(reportData!=null) {
			Beans.copy(reportData.reportcontent2Datas.get(0), report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
			Beans.copy(reportData.reportcontentDatas.get(0), report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
			Beans.copy(reportData, report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
			setupModel(model);
			return "detail-tab.jsp";
		}

		try {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(lang.__("Data is not found.<!--2-->"));
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(),e);
		}
		setupModel(model);
		return null;
	}

}
