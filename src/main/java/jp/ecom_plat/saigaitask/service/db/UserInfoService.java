/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.userInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupuserInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class UserInfoService extends AbstractService<UserInfo> {
	//protected static String[] alterFieldName = {"ID", "ユニット", "名前", "役割", "電話番号", "携帯電話番号", "メールアドレス", "携帯電話のメールアドレス", "備考", "表示順", "有効"};

//	 private static Map<Long, String> userInfoMap;

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
	public UserInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * 班IDで検索
	 * @param gid 班ID
	 * @return ユーザ情報リスト
	 */
	public List<UserInfo> findByGroupIdAndValid(Long gid) {
		return select().where(
				and(
					eq(userInfo().groupid(), gid),
					eq(userInfo().valid(), true)
				)).orderBy(asc(userInfo().disporder())).getResultList();
	}

	/**
	 * ユニットIDで検索
	 * @param uid ユニットID
	 * @return ユーザ情報リスト
	 */
	public List<UserInfo> findByUnitIdAndValid(Long uid) {
		return select().where(
				and(
					eq(userInfo().unitid(), uid),
					eq(userInfo().valid(), true)
				)).orderBy(asc(userInfo().disporder())).getResultList();
	}

	/**
	 * ユニットから検索
	 * @param ulist ユニットリスト
	 * @return ユーザ情報リスト
	 */
	public List<UserInfo> findByUnitIdAndValid(List<UnitInfo> ulist) {
		List<UserInfo> userlist = new Vector<UserInfo>();
		for (UnitInfo uinfo : ulist) {
			userlist.addAll(findByUnitIdAndValid(uinfo.id));
		}
		return userlist;
	}

	public String getUserOptionString(List<UnitInfo> ulist) {
		StringBuffer opt = new StringBuffer();
		for (UnitInfo unit : ulist) {
			List<UserInfo> list = findByUnitIdAndValid(unit.id);
			for (UserInfo user : list) {
				opt.append("<option value=").append(user.id).append(">").append(user.name).append("</option>");
			}
		}
		return opt.toString();
	}

	public Map<Long, String> getNameMap(List<UnitInfo> unitlist) {
		// if (userInfoMap != null) return userInfoMap;

		Map<Long, String> userInfoMap = new HashMap<Long, String>();
		for (UnitInfo unit : unitlist) {
			List<UserInfo> ulist = findByUnitIdAndValid(unit.id);
			for (UserInfo uinfo : ulist) {
				userInfoMap.put(uinfo.id, uinfo.name);
			}
		}
		return userInfoMap;
	}

	/*
	 * public void clearMap() { userInfoMap = null; }
	 */

	/**
	 * 班IDを指定、表示順で検索
	 * @param 班ID
	 * @return 班IDに対応データ
	 */
	public List<UserInfo> findByUnitIdAllOrderByDisporder(long unitID) {
		return select().where(eq(userInfo().unitid(), unitID)).orderBy(asc(userInfo().disporder())).getResultList();
	}

	/**
	 * 班IDを指定、表示順最大値取得
	 * @param 班ID
	 * @return 表示順最大値
	 */
	public int getLargestDisporderByUnitID(long unitID) {
		List<UserInfo> list = findByUnitIdAllOrderByDisporder(unitID);

		if (list.size() == 0)
			return 0;
		else
			return list.get(list.size() - 1).disporder;
	}

	/**
	 * 表示順上に変更
	 * @param オブジェクト
	 */
	public void up(UserInfo object) {
		List<UserInfo> list = findByUnitIdAllOrderByDisporder(object.unitid);

		// ずらす処理
		UserInfo pre = null;
		for (UserInfo cur : list) {
			if (cur.id.equals(object.id)) {
				if (pre == null)
					return;
				cur.disporder = pre.disporder;
				pre.disporder = object.disporder;
				update(pre);
				update(cur);
				return;
			}
			pre = cur;
		}
	}

	/**
	 * 表示順下に変更
	 * @param オブジェクト
	 */
	public void down(UserInfo object) {
		List<UserInfo> list = findByUnitIdAllOrderByDisporder(object.unitid);

		// ずらす処理
		UserInfo pre = null;
		for (UserInfo cur : list) {
			if (cur.id.equals(object.id)) {
				pre = cur;
				continue;
			}
			if (pre != null) {
				pre.disporder = cur.disporder;
				cur.disporder = object.disporder;
				update(pre);
				update(cur);
				return;
			}
		}
	}

	/**
	 * 班IDを指定、表示順を更新、画面レコード削除時用
	 * @param 班ID
	 */
	public void sortDisporderByUnitID(long unitID) {
		List<UserInfo> list = findByUnitIdAllOrderByDisporder(unitID);
		int idx = 1;

		// 順番のチェック
		for (UserInfo object : list) {
			if (object.disporder == idx) {
				idx++;
				continue;
			}

			// ずれている？
			object.disporder = idx;
			update(object);
			idx++;
		}
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(userInfo().unitInfo())
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
	public List<UserInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = userInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!userInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(userInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(userInfo().unitInfo(), ne(userInfo().unitInfo().deleted(), true))
			.innerJoin(userInfo().groupInfo(),ne(userInfo().groupInfo().deleted(), true))
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
		conditions.put(userInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<UserInfo> list = findByCondition(conditions, userInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public int update(UserInfo entity, PropertyName<?>[] excludes) {
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
	 * 自治体IDに紐付くユーザ情報を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
	public List<UserInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<UserInfo> select = select();
		select.innerJoin(userInfo().unitInfo(),ne(userInfo().unitInfo().deleted(), true));
		select.innerJoin(userInfo().groupInfo(),ne(userInfo().groupInfo().deleted(), true));
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(userInfo().groupInfo().localgovinfoid(), localgovinfoid)
					);
			select.where(
					eq(userInfo().unitInfo().localgovinfoid(), localgovinfoid)
					);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(userInfo().disporder()), asc(userInfo().id()));
		return select.getResultList();
	}

	public List<UserInfo> check() {
		List<UserInfo> reslist = select().leftOuterJoin(userInfo().unitInfo()).getResultList();
		List<UserInfo> nolist = new ArrayList<UserInfo>();
		for (UserInfo user : reslist) {
			if (user.unitInfo == null)
				nolist.add(user);
		}
		return nolist;
	}

	@Override
	public DeleteCascadeResult deleteCascade(UserInfo entity, DeleteCascadeResult result) {

		result.cascade(AssemblestateData.class, Names.assemblestateData().userid(), entity.id);
		//result.cascade(JalertserverInfo.class, Names.jalertserverInfo().userid(), entity.id);
		result.cascade(NoticegroupuserInfo.class, Names.noticegroupuserInfo().userid(), entity.id);
		//result.cascade(TelemeterserverInfo.class, Names.telemeterserverInfo().userid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
