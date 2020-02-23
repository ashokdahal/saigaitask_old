/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.tablemasterInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;
import static org.seasar.extension.jdbc.operation.Operations.or;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AutocompleteInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.DecisionsupportInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.IssuelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportRefugeInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportShelterInfo;
import jp.ecom_plat.saigaitask.entity.db.StationalarmInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculatecolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TimelinetableInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * テーブルマスタ情報サービスクラス
 */
@org.springframework.stereotype.Repository
public class TablemasterInfoService extends AbstractService<TablemasterInfo> {

	/** サービスクラス */
	@Resource
	protected MaplayerInfoService maplayerInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;

	/**
	 * IDで検索 テーブルマスタ情報ID
	 * @param id
	 * @return マスターテーブル情報
	 */
    public TablemasterInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
	 * deleted = false の条件付きでid検索
	 * @param id テーブルマスタ情報ID
	 * @return MenuInfo
	 */
	public TablemasterInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().id(id).getSingleResult();
	}

    /**
     * 地図IDで検索
     * @param mapid 地図ID
     * @return マスターテーブル情報
     */
    public List<TablemasterInfo> findByMapmasterInfoId(Long mapid) {
    	return select()
    			.innerJoin(tablemasterInfo().mapmasterInfo())
    			.where(
    			and(
    				eq(tablemasterInfo().mapmasterinfoid(), mapid),
    				ne(tablemasterInfo().mapmasterInfo().deleted(), true),
    				ne(tablemasterInfo().deleted(), true)
    			)
    			).getResultList();
    }

    /**
     * 地図IDとテーブル名で検索
     * @param mid 地図ID
     * @param tbl テーブル名
     * @return 設定として有効なテーブルマスタ情報（複数ある場合は先頭）
     */
	public TablemasterInfo findByMapmasterInfoIdAndTablename(Long mid, String tbl) {
		return select()
				.innerJoin(tablemasterInfo().mapmasterInfo())
				.where(
				and(
					eq(tablemasterInfo().mapmasterinfoid(), mid),
					ne(tablemasterInfo().mapmasterInfo().deleted(), true),
					eq(tablemasterInfo().tablename(), tbl),
					ne(tablemasterInfo().deleted(), true)
				)).limit(1).getSingleResult();
	}

	/**
	 * 地図マスタ情報IDとレイヤIDで検索
	 * @param mid 地図マスタ情報ID
	 * @param layerid レイヤID
	 * @return テーブルマスタ情報（複数ある場合は先頭）
	 */
	public TablemasterInfo findByMapmasterInfoIdAndLayerId(Long mid, String layerid) {
		return select()
				.innerJoin(tablemasterInfo().mapmasterInfo())
				.where(
				and(
					eq(tablemasterInfo().mapmasterinfoid(), mid),
					ne(tablemasterInfo().mapmasterInfo().deleted(), true),
					eq(tablemasterInfo().layerid(), layerid)
				)).limit(1).getSingleResult();
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.innerJoin(tablemasterInfo().mapmasterInfo())
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
	public List<TablemasterInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = tablemasterInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!tablemasterInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(tablemasterInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return selectNotDeleted()
			.innerJoin(tablemasterInfo().mapmasterInfo())
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
	 * @return 更新数
	*/
	public int update(TablemasterInfo entity, PropertyName<?>[] excludes) {
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
	 * 自治体IDに紐付くテーブルマスタ情報を取得する。<br>
	 * また地図マスター情報IDが「0」のデータは必ず返却する。
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
	public List<TablemasterInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<TablemasterInfo> select = selectNotDeleted();
		select.innerJoin(tablemasterInfo().mapmasterInfo());
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					and(
						or(eq(tablemasterInfo().mapmasterInfo().localgovinfoid(), localgovinfoid),
							eq(tablemasterInfo().mapmasterinfoid(), 0L)
							),
						ne(tablemasterInfo().deleted(), true)
					)
			);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(tablemasterInfo().id()));
		return select.getResultList();
	}

	/**
	 * レイヤIDから設定として有効なテーブルマスタ情報を取得します.
	 * @param layerId レイヤID
	 * @return 設定として有効なテーブルマスタ情報（複数ある場合は先頭）
	 */
	public TablemasterInfo findByLayerId(String layerId) {
		List<TablemasterInfo> tablemasterInfos = findByLayerId(layerId, /*deleted*/false);
		if(0<tablemasterInfos.size()) return tablemasterInfos.get(0);
		return null;
	}

	/**
	 * レイヤIDからテーブルマスタ情報を取得します.
	 * @param layerId レイヤID
	 * @param deleted 削除フラグ null: すべて取得, false:未削除を取得, true: 削除のみ取得
	 * @return テーブルマスタ情報リスト
	 */
	public List<TablemasterInfo> findByLayerId(String layerId, Boolean deleted) {
		AutoSelect<TablemasterInfo> select = select();
		SimpleWhere where = new SimpleWhere();
		where.eq(tablemasterInfo().layerid(), layerId);
		if(deleted!=null) {
			if(!deleted) where.ne(tablemasterInfo().deleted(), true);
			else where.eq(tablemasterInfo().deleted(), true);
		}
		select.where(where).orderBy(asc(tablemasterInfo().id()));
        return select.getResultList();
	}

	/**
	 * DBチェック関数
	 * @return 設定として不正なリスト
	 */
	public List<TablemasterInfo> check() {
		List<TablemasterInfo> reslist = select().innerJoin(tablemasterInfo().mapmasterInfo()).where(ne(tablemasterInfo().mapmasterinfoid(), 0L)).getResultList();
		List<TablemasterInfo> nolist = new ArrayList<TablemasterInfo>();
		for (TablemasterInfo tbl : reslist) {
			if (tbl.mapmasterInfo == null)
				nolist.add(tbl);
		}
		return nolist;
	}

	public int deleteLogically(TablemasterInfo entity) throws Exception {
		maplayerInfoService.deleteLogicallyBySimpleWhere(MaplayerInfoNames.tablemasterinfoid(), entity.id);
		menutableInfoService.deleteLogicallyBySimpleWhere(MenutableInfoNames.tablemasterinfoid(), entity.id);
		return super.deleteLogically(entity);
	}

	@Override
	public DeleteCascadeResult deleteCascade(TablemasterInfo entity, DeleteCascadeResult result) {

		result.cascade(HistorytableInfo.class, Names.historytableInfo().tablemasterinfoid(), entity.id);
		result.cascade(TracktableInfo.class, Names.tracktableInfo().tablemasterinfoid(), entity.id);
		result.cascade(AutocompleteInfo.class, Names.autocompleteInfo().tablemasterinfoid(), entity.id);
		result.cascade(ClearinghousemetadataInfo.class, Names.clearinghousemetadataInfo().tablemasterinfoid(), entity.id);
		result.cascade(IssuelayerInfo.class, Names.issuelayerInfo().tablemasterinfoid(), entity.id);
		result.cascade(MaplayerInfo.class, Names.maplayerInfo().tablemasterinfoid(), entity.id);
		result.cascade(MenutableInfo.class, Names.menutableInfo().tablemasterinfoid(), entity.id);
		result.cascade(ObservatorydamInfo.class, Names.observatorydamInfo().tablemasterinfoid(), entity.id);
		result.cascade(ObservatorydamlayerInfo.class, Names.observatorydamlayerInfo().tablemasterinfoid(), entity.id);
		result.cascade(ObservatoryrainInfo.class, Names.observatoryrainInfo().tablemasterinfoid(), entity.id);
		result.cascade(ObservatoryrainlayerInfo.class, Names.observatoryrainlayerInfo().tablemasterinfoid(), entity.id);
		result.cascade(ObservatoryriverInfo.class, Names.observatoryriverInfo().tablemasterinfoid(), entity.id);
		result.cascade(ObservatoryriverlayerInfo.class, Names.observatoryriverlayerInfo().tablemasterinfoid(), entity.id);
		result.cascade(PubliccommonsReportRefugeInfo.class, Names.publiccommonsReportRefugeInfo().tablemasterinfoid(), entity.id);
		result.cascade(PubliccommonsReportShelterInfo.class, Names.publiccommonsReportShelterInfo().tablemasterinfoid(), entity.id);
		result.cascade(StationalarmInfo.class, Names.stationalarmInfo().tablemasterinfoid(), entity.id);
		result.cascade(StationlayerInfo.class, Names.stationlayerInfo().tablemasterinfoid(), entity.id);
		result.cascade(TablecalculatecolumnInfo.class, Names.tablecalculatecolumnInfo().tablemasterinfoid(), entity.id);
		result.cascade(TimelinetableInfo.class, Names.timelinetableInfo().tablemasterinfoid(), entity.id);
		result.cascade(MeteotriggerInfo.class, Names.meteotriggerInfo().issuetablemasterinfoid(), entity.id);
		// V1.4.14
		result.cascade(DecisionsupportInfo.class, Names.decisionsupportInfo().tablemasterinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
