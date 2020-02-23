/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.groupInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;

import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AdminbackupData;
import jp.ecom_plat.saigaitask.entity.db.AlarmdefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.AutocompleteInfo;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LandmarkData;
import jp.ecom_plat.saigaitask.entity.db.LoginData;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.OauthtokenData;
import jp.ecom_plat.saigaitask.entity.db.ThreadData;
import jp.ecom_plat.saigaitask.entity.db.ThreadresponseData;
import jp.ecom_plat.saigaitask.entity.db.ThreadsendtoData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.db.WhiteboardData;
import jp.ecom_plat.saigaitask.entity.names.MenuloginInfoNames;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.StringUtil;

@org.springframework.stereotype.Repository
public class GroupInfoService extends AbstractService<GroupInfo> {

	/** サービスクラス */
	@Resource
	protected MenuloginInfoService menuloginInfoService;

	private static long idZero = 0;
	private /*static */String getIdZeroName() {
		return lang.__("Common");
	}

	/**
	 * IDで検索
	 * @param ID
	 * @return IDに対応唯一のレコード
	 */
	public GroupInfo findById(Long id) {
		return select().leftOuterJoin(groupInfo().localgovInfo()).id(id).getSingleResult();
	}
	/**
	 * deleted = false の条件付きでid検索
	 * @return MenuInfo
	 */
	public GroupInfo findByNotDeletedId(Long id) {
		return selectNotDeleted().leftOuterJoin(groupInfo().localgovInfo()).id(id).getSingleResult();
	}
	/**
	 * NAMEで検索
	 * @param NAME
	 * @return NAMEに対応唯一のレコード
	 */
	public GroupInfo findByName(String name, String password,Long localgovinfoid) {
		try{
			return select().leftOuterJoin(groupInfo().localgovInfo()).where(
					and(
							eq(groupInfo().name(), name),
							in(groupInfo().localgovinfoid(), localgovinfoid ,0L),
							eq(groupInfo().valid(), true),
							ne(groupInfo().deleted(), true),
							eq(groupInfo().password(), UserAuthorization.getEncryptedPass(password))
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
	public GroupInfo findByName(String name, Long localgovinfoid) {
		return select().leftOuterJoin(groupInfo().localgovInfo()).where(
				and(
						eq(groupInfo().name(), name),
						eq(groupInfo().valid(), true),
						in(groupInfo().localgovinfoid(), localgovinfoid ,0L),
						ne(groupInfo().deleted(), true)
								)).getSingleResult();
	}
	/**
	 * IDに対応名称を取得
	 * @param ID
	 * @return 名称
	 */
	public String getNameByID(long id) {
		if (id == idZero)
			return getIdZeroName();
		else
			return this.findById(id).name;
	}

	/**
	 * 自治体IDを指定、有効なものを表示順昇順で全検索
	 * @param 自治体ID
	 * @return 対応データ
	 */
	public List<GroupInfo> findByLocalgovInfoIdAndValid(long govid) {
		return findByLocalgovInfoIdAndValid(govid, false);
	}

	/**
	 * 自治体IDを指定、表示順昇順で全検索
	 * ユニット情報も取得する
	 * @param 自治体ID
	 * @return 対応データ
	 */
	public List<GroupInfo> findByLocalgovInfoIdAndValid(long govid, boolean fetchUnitInfos) {
		//List<GroupInfo> list = groupInfoItems.get(govid);
		//if (list != null)
		//	return list;

		AutoSelect<GroupInfo> select = selectNotDeleted();

		// ユニット情報を結合
		if(fetchUnitInfos) {
			select.innerJoin("unitInfos");
		}

		List<GroupInfo> list =
			select.where(
				and(
					eq(groupInfo().localgovinfoid(), govid),
					eq(groupInfo().valid(), true),
					ne(groupInfo().deleted(), true))
			).orderBy(
				asc(groupInfo().disporder())
			).getResultList();
		//groupInfoItems.put(govid, list);
		return list;
	}

	/**
	 * ログインIDとパスワードで有効なグループを検索
	 * @param loginid
	 * @param password
	 * @return　グループ情報
	 */
	public GroupInfo findByLoginIdAndPasswordAndValid(long loginid, String password) {
		try {
			return select().where(
					and(
						eq(groupInfo().id(), loginid),
						eq(groupInfo().password(), UserAuthorization.getEncryptedPass(password)),
						eq(groupInfo().valid(), true)
					)).getSingleResult();
		} catch (NoSuchAlgorithmException e) {
			logger.error("loginid : " + loginid);
			logger.error("", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 自治体IDを指定、表示順昇順で全検索
	 * @param 自治体ID
	 * @return グループ情報
	 */
	public List<GroupInfo> findByLocalgovInfoIdAll(long localGovInfoID) {
		return select().where(eq(groupInfo().localgovinfoid(), localGovInfoID)).orderBy(asc(groupInfo().disporder())).getResultList();
	}

	/**
	 * 自治体IDと管理者フラグで検索
	 * @param govid 自治体ID
	 * @return グループ情報
	 */
	public GroupInfo findByLocalgovInfoIdAndAdmin(Long govid) {
		return select().where(
				and(
					ne(groupInfo().deleted(), true),
					eq(groupInfo().localgovinfoid(), govid),
					eq(groupInfo().admin(), true)
				)).orderBy(asc(groupInfo().disporder())).limit(1).getSingleResult();
	}

	/**
	 * 自治体IDに紐付くアラームメッセージ設定情報を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
    public List<GroupInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<GroupInfo> select = select();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(groupInfo().localgovinfoid(), localgovinfoid)
					);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(groupInfo().disporder()), asc(groupInfo().id()));
		return select.getResultList();
	}

	/**
	 * 自治体IDとAPIキーで検索
	 * @param govid 自治体ID
	 * @return グループ情報
	 */
	public GroupInfo findByLocalgovInfoIdAndApikey(Long localgovinfoId, String apiKey) {
		return select().where(
				and(
					eq(groupInfo().localgovinfoid(), localgovinfoId),
					eq(groupInfo().apikey(), apiKey)
				)).orderBy(asc(groupInfo().disporder())).limit(1).getSingleResult();
	}

	/**
	 * 自治体IDでIDと名称のマップを返す。
	 * @param govid
	 *            自治体ID
	 * @return IDと名称のマップ
	 */
	public Map<Long, String> getNameMap(long localGovInfoID) {
		Map<Long, String> map = new HashMap<Long, String>();
		map.put(idZero, getIdZeroName());

		List<GroupInfo> list = this.findByLocalgovInfoIdAndValid(localGovInfoID);
		for (GroupInfo o : list) {
			map.put(o.id, o.name);
		}

		return map;
	}

	/**
	 * マップのクリア
	 */
	/*public void clearMap() {
		groupInfoItems = null;
	}*/

	/**
	 * 班の追加
	 * @param grp
	 * @return 数
	 */
	/*@Override
	public int insert(GroupInfo grp) {
		int ret = super.insert(grp);
		groupInfoItems.remove(grp.getLocalgovinfoid());
		return ret;
	}*/

	/**
	 * 自治体IDを指定、表示順最小値を持つIDを取得
	 * @param 自治体ID
	 * @return 表示順最小値を持つID
	 */
	public long getIDWhichSmallestDisporderByLocalGovInfoID(long localGovInfoID) {
		List<GroupInfo> list = this.findByLocalgovInfoIdAll(localGovInfoID);

		if (list == null || list.size() == 0)
			return 0;
		else
			return list.get(0).id;
	}

	/**
	 * 自治体IDを指定、表示順最大値取得
	 * @param 自治体ID
	 * @return 表示順最大値
	 */
	public int getLargestDisporderByLocalGovInfoID(long localGovInfoID) {
		List<GroupInfo> list = this.findByLocalgovInfoIdAll(localGovInfoID);

		if (list == null || list.size() == 0)
			return 0;
		else
			return list.get(list.size() - 1).disporder;
	}

	/**
	 * 表示順上に変更
	 * @param GroupInfoオブジェクト
	 */
	public void up(GroupInfo o) {
		List<GroupInfo> list = this.findByLocalgovInfoIdAll(o.localgovinfoid);

		// ずらす処理
		GroupInfo pre = null;
		for (GroupInfo cur : list) {
			if (cur.id.equals(o.id)) {
				if (pre == null)
					return;
				cur.disporder = pre.disporder;
				pre.disporder = o.disporder;
				update(pre);
				update(cur);
				return;
			}
			pre = cur;
		}
	}

	/**
	 * 表示順下に変更
	 * @param GroupInfoオブジェクト
	 */
	public void down(GroupInfo o) {
		List<GroupInfo> list = this.findByLocalgovInfoIdAll(o.localgovinfoid);

		// ずらす処理
		GroupInfo pre = null;
		for (GroupInfo cur : list) {
			if (cur.id.equals(o.id)) {
				pre = cur;
				continue;
			}
			if (pre != null) {
				pre.disporder = cur.disporder;
				cur.disporder = o.disporder;
				update(pre);
				update(cur);
				return;
			}
		}
	}

	/**
	 * 自治体IDを指定、表示順を更新、画面レコード削除時用
	 * @param 自治体ID
	 */
	public void sortDisporderOnLocalGovInfo(long localGovInfoID) {
		List<GroupInfo> list = this.findByLocalgovInfoIdAll(localGovInfoID);
		int idx = 1;

		// 順番のチェック
		for (GroupInfo o : list) {
			if (o.disporder == idx) {
				idx++;
				continue;
			}

			// ずれている？
			o.disporder = idx;
			update(o);
			idx++;
		}
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)selectNotDeleted()
			.leftOuterJoin(groupInfo().localgovInfo())
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
	public List<GroupInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = groupInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!groupInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(groupInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return selectNotDeleted()
			.leftOuterJoin(groupInfo().localgovInfo())
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
		conditions.put(groupInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<GroupInfo> list = findByCondition(conditions, groupInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

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
	public GroupInfo findByAPIKey(String apiKey) {
		return select().where(Operations.eq(groupInfo().apikey(), apiKey)).limit(1).getSingleResult();
	}
	
	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(GroupInfo entity, PropertyName<?>[] excludes) {
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
	 * 班の追加
	 *
	 * @param grp
	 * @return 数
	 */
	public int insertBySQL(GroupInfo grp) {
		try {
			//TODO:SQL使用禁止！
			int ret = 0;
			String sql = "insert into group_info (";
			sql += "id,localgovinfoid,name,password,ecomuser,ecompass,admin,headoffice,note,disporder,valid";
			sql += ") values (";
			sql += grp.id + ",";
			sql += grp.localgovinfoid + ",";
			sql += "'" + grp.name + "'" + ",";
			sql += "'" + UserAuthorization.getEncryptedPass(grp.password) + "'" + ",";
			sql += "'" + grp.ecomuser + "'" + ",";
			sql += "'" + StringUtil.encrypt(grp.ecompass) + "'" + ",";
			sql += grp.admin + ",";
			sql += grp.headoffice + ",";
			sql += "'" + grp.note + "'" + ",";
			sql += grp.disporder + ",";
			sql += grp.valid;
			sql += ")";
			ret = jdbcManager.updateBySql(sql).execute();
			return ret;
		} catch (NoSuchAlgorithmException e) {
			logger.error("localgovinfoid : "+grp.localgovinfoid+", groupid : "+grp.id);
			logger.error("", e);
			throw new ServiceException(e);
		}
	}

	public int insertBatchSQL() {
		//ResourceUtil.getBuildDir(GroupInfoExService.class).getParent()
		String sqlFile = "/data/adminMenu_info.sql";
		int count = jdbcManager.updateBySqlFile(sqlFile).execute();
		return count;
	}

	public List<GroupInfo> check() {
		List<GroupInfo> reslist = select().leftOuterJoin(groupInfo().localgovInfo()).getResultList();
		List<GroupInfo> nolist = new ArrayList<GroupInfo>();
		for (GroupInfo grp : reslist) {
			if (grp.localgovInfo == null)
				nolist.add(grp);
		}
		return nolist;
	}

	public int deleteLogically(GroupInfo entity) throws Exception {
		menuloginInfoService.deleteLogicallyBySimpleWhere(MenuloginInfoNames.groupid(), entity.id);
        return super.deleteLogically(entity);
    }

	@Override
	public DeleteCascadeResult deleteCascade(GroupInfo entity, DeleteCascadeResult result) {

		result.cascade(AdminbackupData.class, Names.adminbackupData().groupid(), entity.id);
		result.cascade(AlarmmessageData.class, Names.alarmmessageData().groupid(), entity.id);
		result.cascade(LandmarkData.class, Names.landmarkData().groupid(), entity.id);
		result.cascade(LoginData.class, Names.loginData().groupid(), entity.id);
		result.cascade(ThreadData.class, Names.threadData().groupid(), entity.id);
		result.cascade(ThreadresponseData.class, Names.threadresponseData().groupid(), entity.id);
		result.cascade(ThreadsendtoData.class, Names.threadsendtoData().groupid(), entity.id);
		// 管理画面のメニューは削除しない
		//result.cascade(AdminmenuInfo.class, Names.adminmenuInfo().groupid(), entity.id);
		result.cascade(AlarmdefaultgroupInfo.class, Names.alarmdefaultgroupInfo().groupid(), entity.id);
		result.cascade(AlarmmessageInfo.class, Names.alarmmessageInfo().groupid(), entity.id);
		result.cascade(AutocompleteInfo.class, Names.autocompleteInfo().groupid(), entity.id);
		result.cascade(EcomgwpostInfo.class, Names.ecomgwpostInfo().groupid(), entity.id);
		result.cascade(MenuloginInfo.class, Names.menuloginInfo().groupid(), entity.id);
		// 班情報とユニット情報が並列となったが、
		// アップグレードの場合は unitInfo に groupid が残ってしまい自治体削除できなくなるため、
		// groupInfo -> unitInfo で削除できるようにしておく。
		result.cascade(UnitInfo.class, Names.unitInfo().groupid(), entity.id);
		result.cascade(UserInfo.class, Names.userInfo().groupid(), entity.id);
		// V1.4.7
		result.cascade(OauthtokenData.class, Names.oauthtokenData().groupid(), entity.id);
		// V2.0
		result.cascade(WhiteboardData.class, Names.whiteboardData().groupid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}

}
