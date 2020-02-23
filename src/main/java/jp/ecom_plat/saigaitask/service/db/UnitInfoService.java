/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.groupInfo;
import static jp.ecom_plat.saigaitask.entity.Names.unitInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.in;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;

import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class UnitInfoService extends AbstractService<UnitInfo> {

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
	public UnitInfo findById(Long id) {
		return select().leftOuterJoin(unitInfo().localgovInfo()).id(id).getSingleResult();
	}
	/**
	 * deleted = false の条件付きでid検索
	 * @return MenuInfo
	 */
	public UnitInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().leftOuterJoin(unitInfo().localgovInfo()).id(id).getSingleResult();
	}

	/**
	 * NAMEで検索
	 * @param NAME
	 * @return NAMEに対応唯一のレコード
	 */
	public UnitInfo findByName(String name, String password,Long localgovinfoid) {
		try{
			return select().leftOuterJoin(unitInfo().localgovInfo()).where(
					and(
							eq(unitInfo().name(), name),
							in(unitInfo().localgovinfoid(), localgovinfoid ,0L),
							eq(unitInfo().valid(), true),
							ne(unitInfo().deleted(), true),
							eq(unitInfo().password(), UserAuthorization.getEncryptedPass(password))
									)).getSingleResult();
		}catch (NoSuchAlgorithmException e) {
			logger.error("name : " + name);
			logger.error("", e);
			throw new ServiceException(e);
		}
	}
	/**
	 * NAMEで検索
	 * @param NAME
	 * @return NAMEに対応唯一のレコード
	 */
	public UnitInfo findByName(String name,Long localgovinfoid) {
		return select().leftOuterJoin(unitInfo().localgovInfo()).where(
				and(
						eq(unitInfo().name(), name),
						eq(unitInfo().valid(), true),
						in(unitInfo().localgovinfoid(), localgovinfoid ,0L),
						ne(unitInfo().deleted(), true)
						//eq(unitInfo().password(), UserAuthorization.getEncryptedPass(password))
								)).getSingleResult();
	}
	/**
	 * IDに対応名称を取得
	 * @param ID
	 * @return 名称
	 */
	public String getNameByID(long id){
		return findById(id).name;
	}

	/**
	 * 自治体IDを指定、有効なものを表示順昇順で全検索
	 * @param govid 自治体ID
	 * @return 対応データ
	 */
	public List<UnitInfo> findByLocalgovInfoIdAndValid(long govid) {
		return select().where(
				and(
					eq(unitInfo().localgovinfoid(), govid),
					eq(unitInfo().valid(), true)
				)).orderBy(asc(unitInfo().disporder())).getResultList();
	}

	/**
	 * ログインIDとパスワードで有効なグループを検索
	 * @param loginid
	 * @param password
	 * @return　グループ情報
	 */
	public UnitInfo findByLoginIdAndPasswordAndValid(long loginid, String password) {
		try {
			return select().where(
					and(
						eq(unitInfo().id(), loginid),
						eq(unitInfo().password(), UserAuthorization.getEncryptedPass(password)),
						eq(unitInfo().valid(), true)
					)).getSingleResult();
		} catch (NoSuchAlgorithmException e) {
			logger.error("loginid : " + loginid);
			logger.error("", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 自治体IDとAPIキーで検索
	 * @param govid 自治体ID
	 * @return グループ情報
	 */
	public UnitInfo findByLocalgovInfoIdAndApikey(Long localgovinfoId, String apiKey) {
		return select().where(
				and(
					eq(unitInfo().localgovinfoid(), localgovinfoId),
					eq(unitInfo().apikey(), apiKey)
				)).orderBy(asc(unitInfo().disporder())).limit(1).getSingleResult();
	}

	/**
	 * 自治体IDを指定、表示順昇順で全検索
	 * @param 自治体ID
	 * @return グループ情報
	 */
	public List<UnitInfo> findByLocalgovInfoIdAll(long localGovInfoID) {
		return select().where(eq(unitInfo().localgovinfoid(), localGovInfoID)).orderBy(asc(unitInfo().disporder())).getResultList();
	}

	/**
	 * 自治体IDを指定、表示順最大値取得
	 * @param 自治体ID
	 * @return 表示順最大値
	 */
	public int getLargestDisporderByLocalGovInfoID(long localGovInfoID) {
		List<UnitInfo> list = this.findByLocalgovInfoIdAll(localGovInfoID);

		if (list == null || list.size() == 0)
			return 0;
		else
			return list.get(list.size() - 1).disporder;
	}

	/*public Map<Long, String> getNameMap(List<GroupInfo> glist) {
		Map<Long, String> map = new HashMap<Long, String>();
		List<UnitInfo> ulist = findByGroupInfoAndValid(glist);
		for (UnitInfo unit : ulist) {
			map.put(unit.id, unit.name);
		}
		return map;
	}*/

	/*public String getUnitOptionString(Long gid) {
		StringBuffer opt = new StringBuffer();
		List<UnitInfo> list = findByGroupIdAndValid(gid);
		for (UnitInfo unit : list) {
			opt.append("<option value=").append(unit.id).append(">").append(unit.name).append("</option>");
		}
		return opt.toString();
	}*/

	/**
	 * このユニットIDと同じの班IDを持っているレコードを表示順昇順で全検索
	 * @return ユニットIDに対応データ
	 */
	/*public List<UnitInfo> findByUnitIdAll(long unitID) {
		long groupID = select().where(
				eq(unitInfo().id(), unitID)
			).getSingleResult().groupid;
		return select().where(
				eq(unitInfo().groupid(), groupID)
			).orderBy(asc(unitInfo().disporder())).getResultList();
	}*/

	/**
	 * 班IDを指定、表示順で検索
	 * @param 班ID
	 * @return 班IDに対応データ
	 */
	/*public List<UnitInfo> findByGroupIDOrderByDisporder(long groupID) {
		return select().where(eq(unitInfo().groupid(), groupID)).orderBy(asc(unitInfo().disporder())).getResultList();
	}*/

	/**
	 * 班IDを指定して、表示順最大値を取得
	 * @param 班ID
	 * @return 表示順最大値
	 */
	/*public int getLargestDisporderByGroupID(long groupID) {
		List<UnitInfo> list = findByGroupIDOrderByDisporder(groupID);

		if (list.size() == 0)
			return 0;
		else
			return list.get(list.size() - 1).disporder;
	}*/

	/**
	 * 表示順上に変更
	 * @param オブジェクト
	 */
	/*public void up(UnitInfo object) {
		List<UnitInfo> list = findByGroupIDOrderByDisporder(object.groupid);

		// ずらす処理
		UnitInfo pre = null;
		for (UnitInfo cur : list) {
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
	}*/

	/**
	 * 表示順下に変更
	 * @param オブジェクト
	 */
	/*public void down(UnitInfo object) {
		List<UnitInfo> list = findByGroupIDOrderByDisporder(object.groupid);

		// ずらす処理
		UnitInfo pre = null;
		for (UnitInfo cur : list) {
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
	}*/

	/**
	 * 班IDを指定、表示順を更新、画面レコード削除時用
	 * @param 班ID
	 */
	/*public void sortDisporderByGroupID(long groupID) {
		List<UnitInfo> list = findByGroupIDOrderByDisporder(groupID);
		int idx = 1;

		// 順番のチェック
		for (UnitInfo object : list) {
			if (object.disporder == idx) {
				idx++;
				continue;
			}

			// ずれている？
			object.disporder = idx;
			update(object);
			idx++;
		}
	}*/

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			//.innerJoin(unitInfo().groupInfo())
			//.innerJoin(unitInfo().groupInfo().localgovInfo())
			.innerJoin(unitInfo().localgovInfo())
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
	public List<UnitInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = unitInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!unitInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(unitInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);
		
		return select()
			//.innerJoin(unitInfo().groupInfo())
			//.innerJoin(unitInfo().groupInfo().localgovInfo())
			.innerJoin(unitInfo().localgovInfo())
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
		conditions.put(unitInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<UnitInfo> list = findByCondition(conditions, unitInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}

	/**
	 * APIKEYから検索
	 * @param apiKey APIキー
	 * @return entity
	 */
	public UnitInfo findByAPIKey(String apiKey) {
		return select().where(Operations.eq(unitInfo().apikey(), apiKey)).limit(1).getSingleResult();
	}

	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(UnitInfo entity, PropertyName<?>[] excludes) {
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
	
	/*public List<UnitInfo> check() {
		List<UnitInfo> reslist = select().leftOuterJoin(unitInfo().groupInfo()).getResultList();
		List<UnitInfo> nolist = new ArrayList<UnitInfo>();
		for (UnitInfo unit : reslist) {
			if (unit.groupInfo == null)
				nolist.add(unit);
		}
		return nolist;
	}*/

	@Override
	public DeleteCascadeResult deleteCascade(UnitInfo entity, DeleteCascadeResult result) {

		result.cascade(UserInfo.class, Names.userInfo().unitid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
