/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.ListEditDto;
import jp.ecom_plat.saigaitask.entity.db.DatatransferInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculateInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculatecolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.SlimerForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.DataTransferService;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.EvalService;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.TableFeatureService;
import jp.ecom_plat.saigaitask.service.db.AutocompleteDataService;
import jp.ecom_plat.saigaitask.service.db.AutocompleteInfoService;
import jp.ecom_plat.saigaitask.service.db.DatatransferInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculateInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculatecolumnInfoService;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 一括変更を処理するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class SlimerAction extends AbstractPageAction {

	// Service
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected HistoryTableService historyTableService;
	@Resource protected ClearinghouseService clearinghouseService;
	@Resource protected TablecalculateInfoService tablecalculateInfoService;
	@Resource protected TablecalculatecolumnInfoService tablecalculatecolumnInfoService;
	@Resource protected EvalService evalService;
	@Resource protected UserTransaction userTransaction;
	@Resource protected EdituserAttrService edituserAttrService;
	@Resource protected AutocompleteInfoService autocompleteInfoService;
	@Resource protected AutocompleteDataService autocompleteDataService;
	@Resource protected TableFeatureService tableFeatureService;
	@Resource protected DataTransferService dataTransferService;
	@Resource protected DatatransferInfoService datatransferInfoService;

	/** ActionForm */
	public SlimerForm slimerForm;

	/**
	 *  一括変更を実行する.
	 *  eコミマップのレイヤと災対のテーブルの両方をサポート.
	 *  @return null
	 */
	@Transactional(propagation=Propagation.NEVER)
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/slimer","/page/slimer/index"}, method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String index(Map<String,Object>model, @Valid @ModelAttribute SlimerForm slimerForm) {

		Timestamp time = new Timestamp(TimeUtil.newDate().getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String now = sdf.format(new Date()); // ローカルタイム

		//一括変更対象のID格納用（遷移先のページでは特に使っていない）
		String retIds = "aaa";

		//グルーピング対象のカラムの値が変更されたあとでも、
		//当初の変更対象の指定を維持できるように、
		//グルーピング対象のレコードのIDのListを取得する
		//ここでは変数の定義のみ
		List<Long> groupingIds = new ArrayList<Long>();

		// クリアリングハウスに更新をかけるレイヤ
		HashMap<String, TracktableInfo> tracktableInfoMap = new HashMap<String, TracktableInfo>();

		try {
			//コミットされる前にページの読み込みが走る場合があるので。トランザクションを別管理にする。
			userTransaction.begin();

			MapmasterInfo mastermap = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			TracktableInfo ttbl = null;
			TablemasterInfo master = null;
			String tablename = slimerForm.tablename;
			String key = slimerForm.key; // primary key
			String layerid = null;
			LayerInfo layerInfo = null;

			MapDB mapDB = MapDB.getMapDB();
			UserInfo userInfo = mapDB.getAuthIdUserInfo(loginDataDto.getEcomUser());
			if(userInfo==null) {
				throw new ServiceException(lang.__("e-community map user not found."));
			}

			// テーブル名・レイヤの取得
			ttbl = tracktableInfoService.findByTrackDataIdAndTablename(loginDataDto.getTrackdataid(), tablename);
			if (ttbl == null) {
				master = tablemasterInfoService.findByMapmasterInfoIdAndTablename(mastermap.id, tablename);
				if (master == null) return null;
				layerid = master.layerid;
				layerInfo = MapDB.getMapDB().getLayerInfo(layerid);
			}
			else {
				master = tablemasterInfoService.findById(ttbl.tablemasterinfoid);
				layerid = ttbl.layerid;
				layerInfo = MapDB.getMapDB().getLayerInfo(layerid);
			}

			String timeQuery = "";
			if(LayerInfo.TimeSeriesType.HISTORY==layerInfo.timeSeriesType) {
				Date[] timeParam = new Date[]{new Date(time.getTime())};
				timeQuery = ExMapDB.getTimeQuery(layerInfo, timeParam);
			}

			// あとでクリアリングハウスに更新する
			if (ttbl != null && StringUtil.isNotEmpty(layerid))
				tracktableInfoMap.put(ttbl.layerid, ttbl);

			// 更新属性の初期化
			HashMap<String, String>    attribute = new HashMap<String, String>(); // 一括更新
			HashMap<String, Boolean> isAddColumn = new HashMap<String, Boolean>(); // 追記フラグ
			for (ListEditDto data : slimerForm.slimerDatas) {
				String value = data.value;
				//String ids[] = data.id.split(":");
				//tablename = ids[0];
				//String attrid = ids[1];
				//if(key!=null && key.equals(ids[2])==false) throw new ServiceException(lang.__("Batch update of different keys is not supported."));
				//key = ids[2];
				//Long id = Long.parseLong(ids[3]);
				//logger.info("datas : "+tablename+","+attrid+","+key+","+id+","+value);
				Boolean addable = data.addable;

				// 対象属性に追加
				attribute.put(data.column, value);
				isAddColumn.put(data.column, addable);
			}

			// 対象地物IDリストの初期化
			boolean targetAll = slimerForm.targetIds==null || slimerForm.targetIds.size()==0 || (slimerForm.targetIds.size()==1&&"0".equals(slimerForm.targetIds.get(0)));
			{
				//グルーピング対象のレコードのIDのListを取得する
				List<String> colDataList = new ArrayList<String>();
				if(groupingIds.size()==0){
					for(ListEditDto dataDto : slimerForm.groupingDatas){
						String idsArr[] = dataDto.id.split(":");
						//String table = idsArr[0];//テーブル名
						//String attrid = idsArr[1];//カラム名
						//String value = dataDto.value//属性IDのカラムのフィルタリング対象値
						colDataList.add(idsArr[1]+":"+dataDto.value);
					}
					if(colDataList.size()>0)groupingIds = tableService.getGroupingDataIds(tablename, colDataList, key, timeQuery);
				}

				// グルーピング指定がなく、フィルタなしの場合はすべての地物IDを取得する
				if(targetAll) {
					slimerForm.targetIds = new ArrayList<String>();
					List<BeanMap> updateRecords = null;

					//eコミマップのテーブル
					if(StringUtil.isNotEmpty(layerid)){
						updateRecords = tableService.selectByGrouping(tablename, key, groupingIds, timeQuery);
					}
					//その他のテーブル
					else {
						updateRecords = tableService.selectByTrackdataidGrouping(tablename, loginDataDto.getTrackdataid(), key, groupingIds);
					}

					// 対象IDのリストを初期化
					for(BeanMap updateRecord : updateRecords) {
						slimerForm.targetIds.add(String.valueOf(updateRecord.get(key)));
					}
				}
			}

			// 地物のループ
			Map<Long, HashMap<String, String>> features = new HashMap<>();
			for(String idStr : slimerForm.targetIds) {
				Long fid = Long.valueOf(idStr);

				// グルーピング対象のレコードが指定されている場合、指定外のIDなら更新しない
				if(!targetAll && 0<groupingIds.size() && !groupingIds.contains(fid)) continue;

				// 更新対象フィーチャとして登録
				features.put(fid, attribute);

				if(retIds.length()>0){
					retIds = String.valueOf(fid);
				}else{
					retIds += ", "+fid;
				}
			}

			// 一括更新実行
			tableFeatureService.execute(master, ttbl, features, isAddColumn, null);

			//データ転送
			DatatransferInfo transinfo = datatransferInfoService.findByTablemasterInfoIdAndValid(master.id);
			if (transinfo != null)
				dataTransferService.transferData(transinfo, loginDataDto.getTrackdataid());

			//JSON形式で返す（返り値は特に使っていない）
			//try {
				//HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
				//httpServletResponse.setContentType("application/json");
				//PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());

				//Entity→JSON形式に変換して出力します。
				//sendPoint.println("[{\"id\": \""+retIds+"\", \"updatetime\":\""+now+"\"}]");
				//sendPoint.flush();
				//sendPoint.close();
				return "[{\"id\": \""+retIds+"\", \"updatetime\":\""+now+"\"}]";

			//} catch (IOException e) {
			//	logger.error(loginDataDto.logInfo(), e);
			//	throw new ServiceException(e);
			//}

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {
				logger.error(loginDataDto.logInfo(), e1);
			}
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		}

		// クリアリングハウスのメタデータを更新
		long trackmapinfoid = 0;
		if(loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			trackmapinfoid = trackData.trackmapinfoid;
		}
		for(Map.Entry<String, TracktableInfo> entry : tracktableInfoMap.entrySet()) {
			String layerId = entry.getKey();
			clearinghouseService.updatemetadataByLayerId(layerId, trackmapinfoid);
		}

		return null;
	}

	protected void calc(List<TablecalculateInfo> calcList, String tablename, String layerid, List<BeanMap> maplist) {
		for (TablecalculateInfo calc : calcList) {
			for (BeanMap map : maplist) {
				Long fid = (Long)map.get("gid");
				Object val = evalService.eval(calc, layerid, tablename, fid);
				TablecalculatecolumnInfo ccol = tablecalculatecolumnInfoService.findById(calc.tablecalculatecolumninfoid);
				if (val != null)
					tableService.update(tablename, ccol.columnname, "gid", fid, val.toString());
				else
					tableService.update(tablename, ccol.columnname, "gid", fid, null);
			}
		}
	}
}
