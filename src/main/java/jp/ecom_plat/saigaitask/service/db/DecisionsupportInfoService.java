/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.decisionsupportInfo;
import static jp.ecom_plat.saigaitask.entity.Names.unitInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.beans.util.BeanMap;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import jp.ecom_plat.saigaitask.entity.db.DecisionsupportInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 意思決定支援レイヤ設定用サービス
 */
@org.springframework.stereotype.Repository
public class DecisionsupportInfoService extends AbstractService<DecisionsupportInfo> {

	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected TableService tableService;
	@Resource protected MenuInfoService menuInfoService;

	// 総避難者数推定レイヤ種別
	final int DECISIONSUPPORTTYPE_TOTAL_EVACUEE = 9;
	// 避難所レイヤ種別
	final int DECISIONSUPPORTTYPE_SHELTER = 10;
	// 黄色にする基準(推定避難者数が収容者数の何倍になるか)
	final double ALERT_YELLOW = 1.0;
	// 赤色にする基準(推定避難者数が収容者数の何倍になるか)
	final double ALERT_RED = 3.0;

	/**
	 * IDによる検索
	 * @param id : decisionsupportid
	 * @return DecisionsupportInfo
	 */
	public DecisionsupportInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 自治体IDによる検索
	 * @param localgovinfoid
	 * @return List<DecisionsupportInfo>
	 */
	public List<DecisionsupportInfo> findByLocalgovinfoId(Long localgovinfoid) {
		return select()
				.leftOuterJoin(decisionsupportInfo().localgovInfo())
				.leftOuterJoin(decisionsupportInfo().tablemasterInfo())
				.where( eq(decisionsupportInfo().localgovinfoid(), localgovinfoid) )
				.orderBy( asc(decisionsupportInfo().decisionsupporttypeid()) )
				.getResultList();
	}

	/**
	 * 自治体IDおよび意思決定支援レイヤ種別IDによる検索
	 * 複数件ある場合でも1件のみ取得する
	 * @param localgovinfoid
	 * @param decisionsupporttypeid
	 * @return DecisionsupportInfo
	 */
	public DecisionsupportInfo findByLocalgovinfoIdAndTypeId(Long localgovinfoid, int decisionsupporttypeid) {
		return select()
				.leftOuterJoin(decisionsupportInfo().localgovInfo())
				.leftOuterJoin(decisionsupportInfo().tablemasterInfo())
				.where(
					and(
						eq(decisionsupportInfo().localgovinfoid(), localgovinfoid),
						eq(decisionsupportInfo().decisionsupporttypeid(), decisionsupporttypeid),
						ne(decisionsupportInfo().valid(), false)
					) )
				.limit(1)
				.getSingleResult();
	}

	/**
	 * 自治体IDおよび意思決定支援レイヤ種別IDによる検索
	 * 複数件ある場合でも1件のみ取得する
	 * @param localgovinfoid
	 * @param decisionsupporttypeid
	 * @return DecisionsupportInfo
	 */
	public List<DecisionsupportInfo> findByLocalgovinfoIdAndTypeIdList(Long localgovinfoid, int decisionsupporttypeid) {
		return select()
				.leftOuterJoin(decisionsupportInfo().localgovInfo())
				.leftOuterJoin(decisionsupportInfo().tablemasterInfo())
				.where(
					and(
						eq(decisionsupportInfo().localgovinfoid(), localgovinfoid),
						eq(decisionsupportInfo().decisionsupporttypeid(), decisionsupporttypeid),
						ne(decisionsupportInfo().valid(), false)
					) )
				.getResultList();
	}

	/**
	 * 意思決定支援の属性値設定以外が１つでもnullならfalse
	 * 属性値も、必要な設定が入っていなければfalse
	 * @param decisionsupportInfo
	 * @return 全て設定済ならtrue
	 */
	public boolean isNotEmptyParam(DecisionsupportInfo decisionsupportInfo){
		if(decisionsupportInfo == null) return false;
		if(decisionsupportInfo.localgovinfoid == null) return false;
		if(decisionsupportInfo.localgovInfo == null) return false;
		if(decisionsupportInfo.decisionsupporttypeid == null) return false;
		if(decisionsupportInfo.tablemasterinfoid == null) return false;
		if(decisionsupportInfo.tablemasterInfo == null) return false;
		switch(decisionsupportInfo.decisionsupporttypeid){
		case  1: // 建物被害推定レイヤ attrid1 = 全壊率, attrid2 = 半壊率
		case  2: // 人口レイヤ attrid1 = 人口数, attrid2 = 世帯数
		case 10: // 避難所レイヤ attrid1 = 推定避難者数, attrid2 = 収容定員数
			if(isEmptyValue(decisionsupportInfo.attrid1) || isEmptyValue(decisionsupportInfo.attrid2))
				return false;
			break;
		case 6: // 建物被害による避難者数推定レイヤ   attrid1 = 避難者数
		case 7: // 停電、断水による避難者数推定レイヤ attrid1 = 避難者数
		case 8: // 帰宅困難者推定レイヤ attrid1 = 避難者数
		case 9: // 総避難者数推定レイヤ attrid1 = 避難者数
			if(isEmptyValue(decisionsupportInfo.attrid1))
				return false;
			break;
		case 11:// ライフライン等の被害エリアレイヤ
			if(isEmptyValue(decisionsupportInfo.calculation_method))
				return false;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 文字列チェック
	 * @param value
	 * @return : emptyならtrue
	 */
	public boolean isEmptyValue(String value){
		 if ( value == null || value.length() == 0 )
			 return true;
		 else
			 return false;
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(decisionsupportInfo().tablemasterInfo())
			.innerJoin(decisionsupportInfo().decisionsupporttypeMaster())
			.innerJoin(decisionsupportInfo().localgovInfo())
			.where(conditions)
			.getCount();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @return 検索結果
	 */
	public List<DecisionsupportInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = decisionsupportInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!decisionsupportInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(decisionsupportInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(decisionsupportInfo().tablemasterInfo())
			.innerJoin(decisionsupportInfo().decisionsupporttypeMaster())
			.innerJoin(decisionsupportInfo().localgovInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}


	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(DecisionsupportInfo entity, PropertyName<?>[] excludes) {
		if(excludes != null){
			return jdbcManager
				.update(entity)
				.excludes(excludes)
				.execute();
		}else{
			return jdbcManager
				.update(entity)
				.execute();
		}
	}

	/**
	 * 推定避難者数と収容人数を比較し、設定値を超える避難所のIDとスタイル名を返却する関数
	 * @param isGid : 返却するHashmapのkeyにgidを利用するならtrue, falseはWKTで返却する
	 * @return : HashMap<避難所gid, Class名>
	 */
	public HashMap<String, String> filterShelter(boolean isGid){
		// フィルタするgidを取得する
		HashMap<String, String> filterMap = new HashMap<String, String>();
		// 避難所レイヤの情報を取得
		DecisionsupportInfo decisionsupportInfo = findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_SHELTER);
		if(decisionsupportInfo != null && isNotEmptyParam(decisionsupportInfo)){
			// 避難所レイヤ名を取得する
			String shelterLayerID = findByTrackLayerID(decisionsupportInfo);
			if(shelterLayerID == null){
				logger.warn(lang.__("No refugee layer info."));
				return filterMap;
			}
			List<BeanMap> result = tableService.selectAll(shelterLayerID);
			// 推定避難者数
			String estimationEvaceeAttr = decisionsupportInfo.attrid1;
			// 収容可能者数
			String seatingEvaceeAttr = decisionsupportInfo.attrid2;
			for(BeanMap map : result){
				if(map.containsKey(estimationEvaceeAttr) && map.containsKey(seatingEvaceeAttr)){
					String estimationEvaceeStr = (String)map.get(estimationEvaceeAttr);
					String seatingEvaceeStr = (String)map.get(seatingEvaceeAttr);
					try{
						// 収容者数と比較
						int estimationEvacee = Integer.parseInt(estimationEvaceeStr);
						int seatingEvacee = Integer.parseInt(seatingEvaceeStr);
						Long gid = (Long)map.get("_orgid");
						Object geomObj = map.get("theGeom");
						Geometry geom = null;
						if(geomObj != null){
							// String に変換
							String wkt = geomObj.toString();
							// SRID=4326; のような文字列がくっついてくるようになったので削除する
							if(wkt.indexOf(";") != -1){
								wkt = wkt.substring(wkt.indexOf(";")+1, wkt.length());
							}
							WKTReader reader = new WKTReader();
							geom = reader.read(wkt);
							//WKBReader reader = new WKBReader();
							//geom = reader.read(WKBReader.hexToBytes(geomObj.toString()));
						}
						if(estimationEvacee > seatingEvacee * ALERT_RED){
							if(isGid){
								filterMap.put(String.valueOf(gid), "red50");
							}else{
								filterMap.put(geom.toText(), "red");
							}
						}else if(estimationEvacee > seatingEvacee * ALERT_YELLOW){
							if(isGid){
								filterMap.put(String.valueOf(gid), "yellow50");
							}else{
								filterMap.put(geom.toText(), "yellow");
							}
						}
					} catch (NumberFormatException e){
					} catch (ParseException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}
		}
		return filterMap;
	}

	/**
	 * 平時はTablemasterInfoから、災害モードならTracktableInfoからレイヤ名を取得して返却する
	 * @param decisionType : 意思決定支援種別タイプ
	 * @return : layerid
	 */
	public String findByTrackLayerID(DecisionsupportInfo decisionsupportInfo){
		String layerName = null;
		if(decisionsupportInfo != null && isNotEmptyParam(decisionsupportInfo)){
			// コピーフラグ対応
			if(loginDataDto.getTrackdataid() == 0){
				layerName = decisionsupportInfo.tablemasterInfo.layerid;
			}else{
				TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(decisionsupportInfo.tablemasterInfo.id, loginDataDto.getTrackdataid());
				if(ttbl != null) layerName = ttbl.layerid;
			}
		}
		return layerName;
	}

	/**
	 * 総避難者数レイヤの最終更新時間を取得する
	 * @return updatetime(String)
	 */
	public Timestamp decisionsupportUpdateTime(){
		Timestamp lasttime = null;
		// 避難所レイヤの情報を取得
		DecisionsupportInfo decisionsupportInfo_Total = findByLocalgovinfoIdAndTypeId(loginDataDto.getLocalgovinfoid(), DECISIONSUPPORTTYPE_TOTAL_EVACUEE);
		if(decisionsupportInfo_Total != null && isNotEmptyParam(decisionsupportInfo_Total)){
			TablemasterInfo totalTable = tablemasterInfoService.findByNotDeletedId(decisionsupportInfo_Total.tablemasterinfoid);
			// 避難所レイヤIDの取得
			String totalLayerID = findByTrackLayerID(decisionsupportInfo_Total);
			//eコミマップのレイヤ
			lasttime = tableService.getEcomDataLastUpdateTime(totalLayerID, totalTable.updatecolumn, null);
			// UTC対応
			lasttime = new Timestamp(TimeUtil.newTimestampWithOffset(lasttime.getTime()).getTime());
		}
		return lasttime;
	}

	/**
	 * MapAction内で、凡例リスト表示の際に利用するMaplayerInfoデータを作成する
	 * 必要な所以外は設定がないので、避難者推計以外では使用しないこと
	 * @param menuinfoid : pageDto.menuinfoid
	 * @param tablemasterInfo : 避難者推計のtablemasterinfoid
	 * @param decisionLayerID : 避難者推計のlayerid
	 * @return : 作成したMaplayerInfo
	 */
	public MaplayerInfo dummyMaplayerInfoCreate(long menuinfoid, TablemasterInfo tablemasterInfo, String decisionLayerID){
		if(menuinfoid == 0 || tablemasterInfo == null) return null;

		MaplayerInfo maplayerInfo = new MaplayerInfo();
		maplayerInfo.menuinfoid = menuinfoid;
		maplayerInfo.menuInfo = menuInfoService.findById(menuinfoid);
		maplayerInfo.tablemasterinfoid = tablemasterInfo.id;
		//maplayerInfo.tablemasterInfo = tablemasterInfoService.findById(tablemasterinfoid);
		maplayerInfo.tablemasterInfo = tablemasterInfo;

		TracktableInfo tTableInfo = new TracktableInfo();
		List<TracktableInfo> trackTableList = new ArrayList<TracktableInfo>();
		tTableInfo.layerid = decisionLayerID;
		trackTableList.add(tTableInfo);
		maplayerInfo.tablemasterInfo.tracktableInfos = trackTableList;

		maplayerInfo.visible = true;
		maplayerInfo.closed = true;
		maplayerInfo.editable = false;
		maplayerInfo.addable = false;
		maplayerInfo.searchable = true;
		maplayerInfo.snapable = false;
		maplayerInfo.valid = true;
		maplayerInfo.deleted = false;

		//List<MaplayerattrInfo> maplayerattrInfoList =
		maplayerInfo.maplayerattrInfos = new ArrayList<MaplayerattrInfo>();
		return maplayerInfo;
	}

	/**
	 * 表示順最大値を取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
	public int getLargestDisporder(Map<String, Object> conditions) {
		conditions.put(unitInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<DecisionsupportInfo> list = findByCondition(conditions, decisionsupportInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}
}
