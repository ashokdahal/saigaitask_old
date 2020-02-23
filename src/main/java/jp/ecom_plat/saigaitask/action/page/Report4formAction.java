/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.DisasterbuildData;
import jp.ecom_plat.saigaitask.entity.db.DisastercasualtiesData;
import jp.ecom_plat.saigaitask.entity.db.DisasterfarmData;
import jp.ecom_plat.saigaitask.entity.db.DisasterfireData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhospitalData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseholdData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseregidentData;
import jp.ecom_plat.saigaitask.entity.db.DisasterlifelineData;
import jp.ecom_plat.saigaitask.entity.db.DisasterroadData;
import jp.ecom_plat.saigaitask.entity.db.DisasterschoolData;
import jp.ecom_plat.saigaitask.entity.db.DisasterwelfareData;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.Reportcontent2Data;
import jp.ecom_plat.saigaitask.entity.db.ReportcontentData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.db.Reportcontent2DataForm;
import jp.ecom_plat.saigaitask.form.db.ReportcontentDataForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.form.page.Report4formForm;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.db.DisasterbuildDataService;
import jp.ecom_plat.saigaitask.service.db.DisastercasualtiesDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterfarmDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterfireDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhospitalDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhouseDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhouseholdDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterhouseregidentDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterlifelineDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterroadDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterschoolDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterwelfareDataService;
import jp.ecom_plat.saigaitask.service.db.ReportDataService;
import jp.ecom_plat.saigaitask.service.db.Reportcontent2DataService;
import jp.ecom_plat.saigaitask.service.db.ReportcontentDataService;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 4号様式ページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class Report4formAction extends AbstractPageAction {

	/** アクションフォーム */
	protected Report4formForm report4formForm;

	/** 人的被害サービス */
	@Resource
	protected DisastercasualtiesDataService disastercasualtiesDataService;
	@Resource
	protected DisasterhouseDataService disasterhouseDataService;
	@Resource
	protected DisasterhouseholdDataService disasterhouseholdDataService;
	@Resource
	protected DisasterhouseregidentDataService disasterhouseregidentDataService;
	@Resource
	protected DisasterroadDataService disasterroadDataService;
	@Resource
	protected DisasterlifelineDataService disasterlifelineDataService;
	@Resource
	protected DisasterhospitalDataService disasterhospitalDataService;
	@Resource
	protected DisasterfarmDataService disasterfarmDataService;
	@Resource
	protected DisasterwelfareDataService disasterwelfareDataService;
	@Resource
	protected DisasterschoolDataService disasterschoolDataService;
	@Resource
	protected DisasterbuildDataService disasterbuildDataService;
	@Resource
	protected DisasterfireDataService disasterfireDataService;

	/** 報告関連サービス */
	@Resource
	protected ReportDataService reportDataService;
	@Resource
	protected ReportcontentDataService reportcontentDataService;
	@Resource
	protected Reportcontent2DataService reportcontent2DataService;

	/** ファイルサービス */
	@Resource
	protected FileService fileService;

	/** 出力フラグ */
	public boolean outputDialogFlag = false;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("outputDialogFlag", outputDialogFlag);
		model.put("report4formForm", report4formForm);
	}

	/**
	 * 報告ページを表示する.
	 * @return 報告ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/report4form","/page/report4form/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute Report4formForm report4formForm) {
		this.report4formForm = report4formForm;
		initPage("report4", report4formForm);

		try {
			report4formForm.trackdataid = ""+loginDataDto.getTrackdataid();

			// 表示時刻の年の候補は前年、当年、翌年の３つにして
			// デフォルトで当年を選択状態にします。
			recalcShowYears(1);

			java.util.Date statedDate = new Date();

			//表示時刻
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d-k-m");
			String currentTimeStr = sdf.format(new Timestamp(statedDate.getTime()));
			String[] currentTime = currentTimeStr.split("-");
			report4formForm.showtime_mm = currentTime[1];
			report4formForm.showtime_dd = currentTime[2];
			report4formForm.showtime_hh = currentTime[3];
			report4formForm.showtime_mm2 = currentTime[4];

			getData(statedDate);

		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("No. 4 format not displayed."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);

		return "/page/report4form/index";
	}

	/**
	 * 数値がnullの場合は0、その他の場合は数値を文字列に変換して返す。
	 * @param i 数値
	 * @return 数値文字列
	 */
	public static String toString(Integer i) {
		if (i == null) return "0";
		return i.toString();
	}

	/**
	 * 報告データ（4号様式）をファイルに保存する.
	 * @return 保存結果
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/report4form/insert")
	public String insert(Map<String,Object>model, @Valid @ModelAttribute Report4formForm report4formForm) {
		this.report4formForm = report4formForm;

		/** 報告データ */
		Timestamp now = new Timestamp(System.currentTimeMillis());
		ReportData entity = Beans.createAndCopy(ReportData.class, report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
		entity.trackdataid = loginDataDto.getTrackdataid();
		entity.registtime = now;
		reportDataService.insert(entity);

		/** 4号様式（その１） */
		ReportcontentData data1 = Beans.createAndCopy(ReportcontentData.class, report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
		data1.reportdataid = entity.id;
		reportcontentDataService.insert(data1);
		ReportcontentDataForm data1Form = new ReportcontentDataForm();
		Beans.copy(data1, data1Form).dateConverter("yyyy-MM-dd HH:mm:ss").execute();

		/** 4号様式（その２） */
		Reportcontent2Data data2 = Beans.createAndCopy(Reportcontent2Data.class, report4formForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
		data2.reportdataid = entity.id;
		//小計の集計
		data2.subtotal = data2.amount1 + data2.amount2 + data2.amount3 + data2.amount4;
		//被害総額の集計
		data2.atotal = data2.subtotal + data2.amount5 + data2.amount6 + data2.amount7 + data2.amount8 + data2.amount9 + data2.amount10;
		reportcontent2DataService.insert(data2);
		Reportcontent2DataForm data2Form = new Reportcontent2DataForm();
		Beans.copy(data2, data2Form).dateConverter("yyyy-MM-dd HH:mm:ss").execute();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM"), lang.getLocale());
			Date date = sdf.parse(data1.reporttime);
			entity.registtime = new Timestamp(date.getTime());
			reportDataService.update(entity);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}

		/** ファイル出力 */
		//テンプレートファイルの取得
		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String templatePath = fileService.getTemplateRoot()+rb.getString("REPORT_TEMPLATE_PATH_NO4");
		File file = new File(templatePath);
		if(!file.exists()){
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("No template file ["+rb.getString("REPORT_TEMPLATE_PATH_NO4")+"]");
			throw new ServiceException("No template file ["+rb.getString("REPORT_TEMPLATE_PATH_NO4")+"]");
		}
		//出力ファイル定義
		String outFileName = "no4_"+entity.id+".xls";
		String path = FileUtil.getUploadPath(application, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid());
		File outFile = new File(path+outFileName);
		//File outFile = fileService.getFileOnLocalgovDataDir(loginDataDto.getLocalgovinfoid(), FileService.REPORT_DIR +"/"+outFileName);
		if(!fileService.createFile(outFile)){
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("Create file ["+outFileName+"] FAILED");
			throw new ServiceException("Create file ["+outFileName+"] FAILED");
		}

		//ファイルを開く
		HSSFWorkbook wb = null;
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
			wb = new HSSFWorkbook(fs);

			//1つ目のシートに（その1）を、2つ目のシートに（その2）を記入
			for(int a=0; a<2; a++){
				HSSFSheet sheet = wb.getSheetAt(a);
				int st = sheet.getFirstRowNum();
				int end = sheet.getLastRowNum();

				//属性出力位置を検索
				for (int i = st; i <= end; i++) {
					HSSFRow row = sheet.getRow(i);
					if (row == null) continue;
					int cs = row.getFirstCellNum();
					int ce = row.getLastCellNum();
					for (int j = cs; j < ce; j++) {
						HSSFCell cell = row.getCell(j);
						if (cell == null) continue;
						HSSFRichTextString rtext = cell.getRichStringCellValue();
						String str = rtext.getString();

						//1つのセル内に2つ以上のform項目がある場合に対応
						while((str.indexOf("<%") >= 0) ){
							int s = str.indexOf("<%");
							int e = str.indexOf("%>", s);
							String tag = str.substring(s+2, e);

							Field field;
							Object val;
							if(a==0){//その1
								field = ReportcontentDataForm.class.getField(tag);
								val = field.get(data1Form);
							}else if(a==1){//その2
								field = Reportcontent2DataForm.class.getField(tag);
								val = field.get(data2Form);
							}else{
								break;
							}

							if (val != null) {
								String sval = val.toString();
								str = str.replaceFirst("<%"+tag+"%>",sval);
								rtext = new HSSFRichTextString(str);
								//logger.info("str after1<"+tag+"> : "+str);
							} else {
								str = str.replaceFirst("<%"+tag+"%>","");
								rtext = new HSSFRichTextString(str);
								//logger.info("str after2<"+tag+"> : "+str);
							}
						}
						cell.setCellValue(rtext);
					}
				}
			}

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
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(),e);
			throw new ServiceException(e);
		} finally {
			if(wb!=null)
				try {
					wb.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		}

		//ファイルが出力されたら、ファイルパスをDBに格納する
		String outPath = path+outFileName;//outFile.getAbsolutePath();
		String retPath = "/"+outPath.substring(outPath.indexOf("upload/"));
		entity.filepath = retPath;
		reportDataService.update(entity);

		setupModel(model);

		//return "/page/?menuid=" + report4formForm.menuid;
		return "forward:/page/report4form/inserted";
	}

	/**
	 * 報告データ（4号様式）をファイルに保存したあとの表示画面
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/report4form/inserted")
	public String inserted(Map<String,Object>model, @Valid @ModelAttribute Report4formForm report4formForm) {
		this.report4formForm = report4formForm;

		//ページ初期化
		initPage("report4", report4formForm);

		//年の選択状態を保持
		recalcShowYears(-1);

		//出力フラグtrue
		outputDialogFlag = true;

		setupModel(model);

		return "/page/report4form/index";
	}

	/**
	 * 指定時間時点までのデータを報告データ（4号様式）に表示する.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/report4form/showdata")
	public String showdata(Map<String,Object>model, @Valid @ModelAttribute Report4formForm report4formForm) {
		this.report4formForm = report4formForm;

		//ページ初期化
		initPage("report4", report4formForm);

		try {
			if(loginDataDto.getTrackdataid()==0) {
				throw new ServiceException(lang.__("Disaster is not run."));
			}

			//年の選択状態を保持
			recalcShowYears(-1);

			//指定時刻
			java.util.Date statedDate = TimeUtil.formatWareki(
				report4formForm.showtime_yy,
				report4formForm.showtime_mm,
				report4formForm.showtime_dd,
				report4formForm.showtime_hh,
				report4formForm.showtime_mm2,
				null);

			getData(statedDate);

		} catch (ServiceException e) {
			String msg = lang.__("Unable to display No.4 format by specified time.");
			logger.error(loginDataDto.logInfo()+ "\n", e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg, false));
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);

		return "/page/report4form/index";
	}

	protected void getData(java.util.Date statedDate) {

		// 災害名
		if(loginDataDto.getTrackdataid()==0L) throw new ServiceException(lang.__("Disaster is not run."));
		TrackData track = trackDataService.findById(loginDataDto.getTrackdataid());
		if(track==null) throw new ServiceException(lang.__("Record ID is not found."));
		if(StringUtil.isEmpty(report4formForm.disastername)) {
			report4formForm.disastername = track.name;
		}

		//報告日時
		SimpleDateFormat sdf = new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM"), lang.getLocale());
		report4formForm.reporttime = sdf.format(statedDate);
		//発生日時
		//report4formForm.occurtime = sdf.format(statedDate);
		report4formForm.occurtime = sdf.format(track.starttime.getTime());


		//都道府県、市区町村
		LocalgovInfo gov = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		Beans.copy(gov, report4formForm).dateConverter("yyyy-MM-dd").execute();
		report4formForm.headoffice1 = gov.pref;
		report4formForm.headoffice2 = gov.city;


		/** 以下、timestamp型のカラムを使って絞込み検索するため、java.util.Dateの指定時刻をjava.sql.Timestampに変換する */
		Calendar cal = Calendar.getInstance();
		cal.setTime(statedDate);
		java.sql.Timestamp time = new java.sql.Timestamp(cal.getTimeInMillis());

		int amount4 = 0;
		//被災状況人的被害データ
		DisastercasualtiesData cas = disastercasualtiesDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (cas != null) {
			// null値を初期化
			if (cas.casualties1==null) cas.casualties1 = 0;
			if (cas.casualties2==null) cas.casualties2 = 0;
			if (cas.casualties3==null) cas.casualties3 = 0;
			if (cas.casualties4==null) cas.casualties4 = 0;
			// データコピー
			Beans.copy(cas, report4formForm).dateConverter("yyyy-MM-dd").execute();
			// 計算、別のフィールドにデータをコピー
			report4formForm.casualties3 = toString(cas.casualties3+cas.casualties4);
			report4formForm.casualties21 = toString(cas.casualties1);
			report4formForm.casualties22 = toString(cas.casualties2);
			report4formForm.casualties23 = toString(cas.casualties3);
			report4formForm.casualties24 = toString(cas.casualties4);
			report4formForm.total = toString(cas.casualties1+cas.casualties2+cas.casualties3+cas.casualties4);
		}

		//被災状況住家被害データ
		DisasterhouseData house = disasterhouseDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (house != null) {
			report4formForm.house1 = toString(house.houseall);
			report4formForm.house2 = toString(house.househalf);
			report4formForm.house3 = toString(house.housepart);
			report4formForm.house4 = toString(house.houseupper);
			report4formForm.houseall1 = toString(house.houseall);
			report4formForm.househalf1 = toString(house.househalf);
			report4formForm.housepart1 = toString(house.housepart);
			report4formForm.houseupper1 = toString(house.houseupper);
			report4formForm.houselower1 = toString(house.houselower);
		}
		//被災状況住家被害（世帯）データ
		DisasterhouseholdData house2 = disasterhouseholdDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (house2 != null) {
			report4formForm.houseall2 = toString(house2.houseall);
			report4formForm.househalf2 = toString(house2.househalf);
			report4formForm.housepart2 = toString(house2.housepart);
			report4formForm.houseupper2 = toString(house2.houseupper);
			report4formForm.houselower2 = toString(house2.houselower);
		}
		//被災状況住家被害（人）データ
		DisasterhouseregidentData house3 = disasterhouseregidentDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (house3 != null) {
			report4formForm.houseall3 = toString(house3.houseall);
			report4formForm.househalf3 = toString(house3.househalf);
			report4formForm.housepart3 = toString(house3.housepart);
			report4formForm.houseupper3 = toString(house3.houseupper);
			report4formForm.houselower3 = toString(house3.houselower);
		}

		//被災状況土木被害
		DisasterroadData road = disasterroadDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (road != null) {
			Beans.copy(road, report4formForm).dateConverter("yyyy-MM-dd").execute();
			report4formForm.amount3 = toString(road.roadmount);
		}

		//被災状況ライフライン被害
		DisasterlifelineData life = disasterlifelineDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (life != null) {
			Beans.copy(life, report4formForm).dateConverter("yyyy-MM-dd").execute();
			if (life.lifelinemount != null)
				amount4 += life.lifelinemount;
		}

		//被災状況病院被害
		DisasterhospitalData hosp = disasterhospitalDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (hosp != null) {
			Beans.copy(hosp, report4formForm).dateConverter("yyyy-MM-dd").execute();
			if (hosp.hospitalmount != null)
				amount4 += hosp.hospitalmount;
		}

		//被災状況農林被害
		DisasterfarmData farm = disasterfarmDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (farm != null) {
			Beans.copy(farm, report4formForm).dateConverter("yyyy-MM-dd").execute();
			report4formForm.amount2 = toString(farm.farmmount);
		}

		//被災状況民政被害
		DisasterwelfareData welfare = disasterwelfareDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (welfare != null) {
			Beans.copy(welfare, report4formForm).dateConverter("yyyy-MM-dd").execute();
			if (welfare.welfaremount != null)
				amount4 += welfare.welfaremount;
		}

		//被災状況文教被害
		DisasterschoolData school = disasterschoolDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (school != null) {
			Beans.copy(school, report4formForm).dateConverter("yyyy-MM-dd").execute();
			report4formForm.amount1 = toString(school.schoolmount);
		}

		//被災状況非住家被害
		DisasterbuildData build = disasterbuildDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (build != null) {
			Beans.copy(build, report4formForm).dateConverter("yyyy-MM-dd").execute();
			if (build.buildmount != null)
				amount4 += build.buildmount;
		}

		//被災状況火災発生
		DisasterfireData fire = disasterfireDataService.getLatestByTrackDataIdLETime(loginDataDto.getTrackdataid(), time);
		if (fire != null) {
			Beans.copy(fire, report4formForm).dateConverter("yyyy-MM-dd").execute();
		}
		report4formForm.amount4 = Integer.toString(amount4);
	}

	protected void recalcShowYears(int defaultIndex) {
		// 発表日時の候補は前年、当年、翌年の３つにします。
		int listsize = 3;
		report4formForm.showtime_yy_list = new String[listsize];

		Calendar cal0 = Calendar.getInstance();
		int year0 = cal0.get(Calendar.YEAR) - 1;	// 前年
		for(int i=0; i<listsize; i++){
			Calendar cal = (Calendar)cal0.clone();
			cal.set(Calendar.YEAR, year0 + i);
			String[] nen = TimeUtil.getWareki(cal.getTime()); 	// 和暦で選択
			report4formForm.showtime_yy_list[i] = nen[0]+nen[1];
		}

		if(0 <= defaultIndex && defaultIndex < listsize){
			// デフォルトで選択状態にします。
			report4formForm.showtime_yy = report4formForm.showtime_yy_list[defaultIndex];
		}
	}
}
