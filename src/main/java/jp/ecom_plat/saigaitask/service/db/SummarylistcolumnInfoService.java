/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.summarylistcolumnInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.names.SummarylistcolumnInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 集計リスト項目情報サービス
 */
@org.springframework.stereotype.Repository
public class SummarylistcolumnInfoService extends AbstractService<SummarylistcolumnInfo> {

    public SummarylistcolumnInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * メニューID指定で集計リスト項目情報リストを取得します.
	 * valid=true のもののみ取得します.
	 * disporder順でソートします.
	 * @param menuid メニューID
	 * @return 外部リスト集計項目情報リスト
	 */
	public List<SummarylistcolumnInfo> findByMenuid(long menuid) {

		// 本来は summarylistinfoid で検索すべきだが、menuidでの検索に対応する
		List<SummarylistInfo> summarylistInfos = new ArrayList<>();
		{
			MenutableInfoService menutableInfoService = SpringContext.getApplicationContext().getBean(MenutableInfoService.class);
			List<MenutableInfo> menutableInfos = menutableInfoService.findByMenuInfoId(menuid);

			SummarylistInfoService summarylistInfoService = SpringContext.getApplicationContext().getBean(SummarylistInfoService.class);
			for(MenutableInfo menutableInfo : menutableInfos) {
				if(menutableInfo.deleted==true) continue;

				SummarylistInfo summarylistInfo = summarylistInfoService.findByTablemasterinfoId(menutableInfo.tablemasterinfoid);
				if(summarylistInfo!=null) {
					summarylistInfos.add(summarylistInfo);
				}
			}
		}
		if(summarylistInfos.size()==0) {
			return new ArrayList<>(); //throw new ServiceException(lang.__("External list summary info has not been set."));
		}
		if(1<summarylistInfos.size()) {
			return new ArrayList<>(); //throw new ServiceException(lang.__("multiple aggregation list are set on menu."));
		}

		SummarylistInfo summarylistInfo = summarylistInfos.get(0);
		return findBySummarylistinfoid(summarylistInfo.id);
	}

	/**
	 * 集計リスト情報ID指定で集計リスト項目情報リストを取得します.
	 * valid=true のもののみ取得します.
	 * disporder順でソートします.
	 * @param summarylistinfoid 集計リスト情報ID
	 * @return 外部リスト集計項目情報リスト
	 */
	public List<SummarylistcolumnInfo> findBySummarylistinfoid(long summarylistinfoid) {

		// Select
		AutoSelect<SummarylistcolumnInfo> select = select();

		// Where
		SimpleWhere where = new SimpleWhere();
		where.eq(SummarylistcolumnInfoNames.valid(), true);
		where.eq(SummarylistcolumnInfoNames.summarylistinfoid(), summarylistinfoid);
		select.where(where);

		// OrderBy
		//select.orderBy(Operations.asc(SummarylistcolumnInfoNames.disporder()));

		return select.getResultList();
	}

    /**
     * 件数を返します。
     *
	 * @param summarylistinfoid 集計リスト情報ID
     * @return 件数
     */
    public long getCountBySummarylistinfoid(long summarylistinfoid) {
		// Select
		AutoSelect<SummarylistcolumnInfo> select = select();

		// Where
		SimpleWhere where = new SimpleWhere();
		where.eq(SummarylistcolumnInfoNames.valid(), true);
		where.eq(SummarylistcolumnInfoNames.summarylistinfoid(), summarylistinfoid);
		select.where(where);

		return select.getCount();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(summarylistcolumnInfo().summarylistInfo())
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
	public List<SummarylistcolumnInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = summarylistcolumnInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!summarylistcolumnInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(summarylistcolumnInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(summarylistcolumnInfo().summarylistInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}

	/**
	 * 表示順最大値を取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
/*	public int getLargestDisporder(Map<String, Object> conditions) {
		conditions.put(summarylistcolumnInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<SummarylistcolumnInfo> list = findByCondition(conditions, summarylistcolumnInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}*/

	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(SummarylistcolumnInfo entity, PropertyName<?>[] excludes) {
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
	 * V1.4形式の設定からV2.0形式の設定に更新する
	 * @return 更新数
	 */
	public int upgradeToVer2() {
		int count = 0;
		try {
			String sql = "select distinct menuinfoid from summarylistcolumn_info where menuinfoid is not null;";
			List<Long> menuinfoIds = jdbcManager.selectBySql(Long.class, sql).getResultList();
			MenuInfoService menuInfoService =  SpringContext.getApplicationContext().getBean(MenuInfoService.class);
			MenutasktypeInfoService menutasktypeInfoService =  SpringContext.getApplicationContext().getBean(MenutasktypeInfoService.class);
			MenutableInfoService menutableInfoService =  SpringContext.getApplicationContext().getBean(MenutableInfoService.class);
			SummarylistInfoService summarylistInfoService =  SpringContext.getApplicationContext().getBean(SummarylistInfoService.class);
			SummarylistcolumnInfoService summarylistcolumnInfoService = this; //SpringContext.getApplicationContext().getBean(SummarylistcolumnInfoService.class);
			for(Long menuinfoId : menuinfoIds) {
				MenuInfo menuInfo = menuInfoService.findById(menuinfoId);
				if(menuInfo!=null) {
					SummarylistInfo summarylistInfo = new SummarylistInfo();

					// 自治体IDを取得
					MenutasktypeInfo menutasktypeInfo = menutasktypeInfoService.findById(menuInfo.menutasktypeinfoid);
					if(menutasktypeInfo!=null) {
						//summarylistInfo.localgovinfoid = menutasktypeInfo.localgovinfoid;
					}

					// 集計対象テーブルの設定
					List<MenutableInfo> menutableInfos = menutableInfoService.findByMenuInfoId(menuinfoId);
					for(MenutableInfo menutableInfo : menutableInfos) {
						if(menutableInfo.tablemasterinfoid!=null) {
							summarylistInfo.targettablemasterinfoid = menutableInfo.tablemasterinfoid;
							break;
						}
					}

					// 集計リスト情報を新規登録
					summarylistInfoService.insert(summarylistInfo);

					// V1.4設定になっている集計リスト項目情報を更新
					List<SummarylistcolumnInfo> summarylistcolumnInfos = summarylistcolumnInfoService.findByMenuid(menuinfoId);
					for(SummarylistcolumnInfo summarylistcolumnInfo : summarylistcolumnInfos) {
						summarylistcolumnInfo.menuinfoid = null;
						summarylistcolumnInfo.summarylistinfoid = summarylistInfo.id;
						summarylistcolumnInfoService.update(summarylistcolumnInfo);
						count++;
					}
				}
			}
		} catch(Exception e) {
			logger.warn("Failed to update summarylistcolumn_info: "+e.getMessage(), e);
		}
		return count;
	}
}
