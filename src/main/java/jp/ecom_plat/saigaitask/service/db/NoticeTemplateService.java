/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticeTemplate;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.service.StationService;

@org.springframework.stereotype.Repository
public class NoticeTemplateService extends AbstractService<NoticeTemplate> {

	/** 自治体情報サービス */
	@Resource
	protected LocalgovInfoService localgovInfoService;
	/** 記録サービス */
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected StationService stationService;

	/**
	 * IDで検索
	 * @param id ID
	 * @return IDに対応唯一のレコード
	 */
	public NoticeTemplate findById(Long id) {
		return select().id(id).getSingleResult();
	}

	public List<NoticeTemplate> findAllOrderBy() {
		return select().orderBy(asc(noticeTemplate().localgovinfoid()), asc(noticeTemplate().noticetemplatetypeid()), asc(noticeTemplate().disporder())).getResultList();
	}

	/**
	 * 自治体IDで検索
	 * @param govid
	 * @return
	 */
	public List<NoticeTemplate> findByLoaclgovInfoId(Long govid) {
		return select().where(
				eq(noticeTemplate().localgovinfoid(), govid)
				).orderBy(asc(noticeTemplate().noticetemplatetypeid()), asc(noticeTemplate().disporder())).getResultList();
	}

    public String replaceTag(String content) {
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	if (content.indexOf(lang.__("<Local gov. name>")) >= 0) {
    		LocalgovInfo gov = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
    		String govname = localgovInfoService.getLocalgovName(gov.id);
    		content = content.replaceAll(lang.__("<Local gov. name>"), govname);
    	}
    	if (content.indexOf(lang.__("<Disaster name>")) >= 0) {
    		TrackData track = trackDataService.findById(loginDataDto.getTrackdataid());
    		if (track != null)
    			content = content.replaceAll(lang.__("<Disaster name>"), track.name);
    	}
    	if (content.indexOf(lang.__("<YearMonthDayHHMM>")) >= 0) {
			//Locale.setDefault(new Locale("ja","JP","JP"));
			SimpleDateFormat sdf = new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' H:m<!--2-->"), lang.getLocale());
    		String time = sdf.format(now);
			//Locale.setDefault(Locale.JAPAN);
    		content = content.replaceAll(lang.__("<YearMonthDayHHMM>"), time);
    	}
    	if (content.indexOf(lang.__("<Date>")) >= 0) {
			//Locale.setDefault(new Locale("ja","JP","JP"));
			SimpleDateFormat sdf = new SimpleDateFormat(lang.__("MMM.d,yyyy"), lang.getLocale());
    		String time = sdf.format(now);
			//Locale.setDefault(Locale.JAPAN);
    		content = content.replaceAll(lang.__("<Date>"), time);
    	}
    	if (content.indexOf(lang.__("<Hour>")) >= 0) {
    		SimpleDateFormat sdf = new SimpleDateFormat(lang.__("H:m"));
    		String time = sdf.format(now);
    		content = content.replaceAll(lang.__("<Hour>"), time);
    	}
    	if (content.indexOf(lang.__("<System>")) >= 0) {
    		String currentStationName = stationService.getLoginCurrentSationName();
    		if (StringUtil.isNotEmpty(currentStationName)) {
				content = content.replaceAll(lang.__("<System>"), currentStationName);
    		}
    	}
    	return content;
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(noticeTemplate().noticetemplatetypeMaster())
			.innerJoin(noticeTemplate().noticetypeMaster())
			.innerJoin(noticeTemplate().localgovInfo())
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
	public List<NoticeTemplate> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = noticeTemplate().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!noticeTemplate().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(noticeTemplate().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return select()
			.innerJoin(noticeTemplate().noticetemplatetypeMaster())
			.innerJoin(noticeTemplate().noticetypeMaster())
			.innerJoin(noticeTemplate().localgovInfo())
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
	public int getLargestDisporder(Map<String, Object> conditions) {
		conditions.put(noticeTemplate().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<NoticeTemplate> list = findByCondition(conditions, noticeTemplate().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}

	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(NoticeTemplate entity, PropertyName<?>[] excludes) {
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

	public List<NoticeTemplate> check() {
		List<NoticeTemplate> reslist = select().innerJoin(noticeTemplate().localgovInfo())
				.innerJoin(noticeTemplate().noticetypeMaster())
				.innerJoin(noticeTemplate().noticetemplatetypeMaster()).getResultList();
		List<NoticeTemplate> nolist = new ArrayList<NoticeTemplate>();
		for (NoticeTemplate temp : reslist) {
			if (temp.localgovInfo == null || temp.noticetypeMaster == null || temp.noticetemplatetypeMaster == null)
				nolist.add(temp);
		}
		return nolist;
	}

	/**
	 * すべての異なる種別と区分の込み合わせを取得する。
	 * （テンプレート種別の「名称」を note に保存する）
	 * @param govid 自治体ID
	 * @return
	 */
	public List<NoticeTemplate> findTypeAndClass(Long govid) {
		String sql =
				"select t1.noticetemplatetypeid, t1.templateclass, min(t2.name) note"
				+ " from notice_template t1, noticetemplatetype_master t2"
				+ " where t1.noticetemplatetypeid = t2.id"
				+ " and t1.localgovinfoid = " + govid
				+ " group by t1.noticetemplatetypeid, t1.templateclass"
				+ " order by min(t2.disporder), min(t1.disporder), min(t1.id)";
		List<NoticeTemplate> list = jdbcManager.selectBySql(NoticeTemplate.class, sql).getResultList();
		return list;
	}

	/**
	* 自治体IDと通知種別で検索
	* @param govid
	* @param noticetypeid
	* @return
	*/
	public List<NoticeTemplate> findByLoaclgovInfoIdNoticetypeId(Long govid, Integer noticetypeid) {
		return select().where(
			and(
				eq(noticeTemplate().localgovinfoid(), govid),
				eq(noticeTemplate().noticetypeid(), noticetypeid)
			)
		).orderBy(asc(noticeTemplate().noticetemplatetypeid()), asc(noticeTemplate().disporder())).getResultList();
	}

	@Override
	public DeleteCascadeResult deleteCascade(NoticeTemplate entity, DeleteCascadeResult result) {

		result.cascade(AssembleInfo.class, Names.assembleInfo().noticetemplateid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
