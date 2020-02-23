package jp.ecom_plat.saigaitask.service.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.map.db.StoredConsumer;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MobileqrcodeInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.SpringContext;
import net.sf.json.JSONObject;

import static jp.ecom_plat.saigaitask.entity.Names.mobileqrcodeInfo;
import static jp.ecom_plat.saigaitask.entity.names.MobileqrcodeInfoNames.*;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.*;

/**
 * {@link MobileqrcodeInfo}のサービスクラスです。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2018/04/04 16:05:03")
@org.springframework.stereotype.Repository
public class MobileqrcodeInfoService extends AbstractService<MobileqrcodeInfo> {

    /**
     * 識別子でエンティティを検索します。
     * 
     * @param id
     *            識別子
     * @return エンティティ
     */
    public MobileqrcodeInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     * 
     * @return エンティティのリスト
     */
    public List<MobileqrcodeInfo> findAllOrderById() {
        return select().orderBy(asc(id())).getResultList();
    }

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
				.innerJoin(mobileqrcodeInfo().localgovinfo())
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
	public List<MobileqrcodeInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = mobileqrcodeInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!mobileqrcodeInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(mobileqrcodeInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(mobileqrcodeInfo().localgovinfo())
			.innerJoin(mobileqrcodeInfo().tablemasterinfo())
			.leftOuterJoin(mobileqrcodeInfo().group())
			.leftOuterJoin(mobileqrcodeInfo().unit())
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
	public int update(MobileqrcodeInfo entity, PropertyName<?>[] excludes) {
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
	 * 表示順最大値を取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
	public int getLargestDisporder(Map<String, Object> conditions) {
		//conditions.put(mobileqrcodeInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<MobileqrcodeInfo> list = findByCondition(conditions, mobileqrcodeInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}

	public JSONObject getQRCodeJSON(Long id, Long groupid, Long unitid, boolean outputSecret) {
		MobileqrcodeInfoService mobileqrcodeInfoService = this;
		GroupInfoService groupInfoService = SpringContext.getApplicationContext().getBean(GroupInfoService.class);
		UnitInfoService unitInfoService = SpringContext.getApplicationContext().getBean(UnitInfoService.class);
		TablemasterInfoService tablemasterInfoService = SpringContext.getApplicationContext().getBean(TablemasterInfoService.class);
		LocalgovInfoService localgovInfoService = SpringContext.getApplicationContext().getBean(LocalgovInfoService.class);

		MobileqrcodeInfo mobileqrcodeInfo = null;
		StoredConsumer consumer = null;
		if(0<id) {
			mobileqrcodeInfo = mobileqrcodeInfoService.findById(id);
			mobileqrcodeInfo.tablemasterinfo = tablemasterInfoService.findById(mobileqrcodeInfo.tablemasterinfoid);
			mobileqrcodeInfo.localgovinfo = localgovInfoService.findById(mobileqrcodeInfo.localgovinfoid);
			consumer = StoredConsumer.fetch(mobileqrcodeInfo.oauthconsumerid);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// get group,unit
		if(groupid==null && unitid==null) {
			groupid = mobileqrcodeInfo.groupid;
			unitid = mobileqrcodeInfo.unitid;
		}
		GroupInfo groupInfo = groupid!=null ? groupInfoService.findById(groupid) : null;
		UnitInfo unitInfo   = unitid!=null ? unitInfoService.findById(unitid) : null;
		if(groupid!=null && groupid.equals(-1L)) groupInfo = getNegativeGroupInfo();
		if(unitid!=null && unitid.equals(-1L)) unitInfo = getNegativeUnitInfo();

		LocalgovInfo localgovInfo = mobileqrcodeInfo!=null ? mobileqrcodeInfo.localgovinfo : null;
		if(localgovInfo==null) localgovInfo = groupInfo!=null ? groupInfo.localgovInfo : unitInfo.localgovInfo;

		// URLはリクエストURLを利用する
		String siteUrl = localgovInfo.domain;
		HttpServletRequest request = SpringContext.getRequest();
		if(request!=null) {
			String requestContextURL = Config.getRequestContextURL(request);
			if(requestContextURL!=null) {
				siteUrl = requestContextURL;
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("group_id", groupInfo!=null ? groupInfo.id : "u"+unitInfo.id);
		json.put("localgovinfoid", localgovInfo.id);
		json.put("name", groupInfo!=null ? groupInfo.name : unitInfo.name);
		if(mobileqrcodeInfo!=null) json.put("layerid", mobileqrcodeInfo.tablemasterinfo.layerid);
		json.put("site_url", siteUrl);
		if(mobileqrcodeInfo!=null) json.put("qrcodeid", mobileqrcodeInfo.id);
		if(consumer!=null) json.put("consumer_key", consumer.getConsumerKey());
		if(mobileqrcodeInfo!=null) json.put("authenticationdate", sdf.format(mobileqrcodeInfo.getAuthenticationdate()));
		if(outputSecret) {
			if(consumer!=null) json.put("consumer_key_secret", consumer.getConsumerKeySecret());
		}
		return json;
	}

	public GroupInfo getNegativeGroupInfo() {
		// 全班共通
		GroupInfo allGroupInfo = new GroupInfo();
		allGroupInfo.id = -1L;
		allGroupInfo.name = lang.__("all group in common");
		return allGroupInfo;
	}
	
	public UnitInfo getNegativeUnitInfo() {
		// 全課共通
		UnitInfo allUnitInfo = new UnitInfo();
		allUnitInfo.id = -1L;
		allUnitInfo.name = lang.__("all unit in common");
		return allUnitInfo;
	}
}