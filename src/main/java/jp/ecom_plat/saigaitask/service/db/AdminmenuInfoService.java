/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.adminmenuInfo;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;
import static org.seasar.extension.jdbc.operation.Operations.or;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.framework.exception.ResourceNotFoundRuntimeException;
import org.seasar.framework.util.ResourceUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.AdminmenuInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class AdminmenuInfoService extends AbstractService<AdminmenuInfo> {

	public AdminmenuInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * @return
	 */
	public List<AdminmenuInfo> findByIdOrderByClassify() {
		return select().orderBy(asc(adminmenuInfo().classify()))
				.getResultList();
	}

	/**
	 * 管理画面のサイドメニュー項目を取得する。
	 * 　有効・ログイン班による権限制御及び、区分の昇順で取得する
	 * @param gid : ログイン時の班ID
	 * @return
	 */
	public List<AdminmenuInfo> findMenuInfoByGroupid(Long gid) {
		// サイドメニューに表示しないリストを取得する
		// 共通:
		//   有効・無効がfalse
		//     OR 公開が課のみ、かつ非公開（6）
		//     OR 公開が課のみ、かつ全公開（7）
		//     OR 公開が課のみ、かつ限定公開（8）
		// 管理権限あり:
		//   公開が非公開（0）または班のみ、かつ非公開（3）で且つ指定グループID（班ID）と異なる
		// 管理権限なし:
		//   公開が限定公開（2）と班のみ、かつ限定公開（5）以外で且つ指定グループID（班ID）と異なる
		List<AdminmenuInfo> aiList = select()
				.where(
					or(
						or(
							eq(adminmenuInfo().valid(), false),
							eq(adminmenuInfo().publicmode(), 6),
							// 以下の対応を行うため、コメントアウトとした。
							// 班　publicmode:5
							//   管理者権限あり　　班、課含めて全て表示
							//   管理者権限なし　　班のみ表示
							// 課　publicmode:8
							//   管理者権限あり　　課含めて全て表示（班は非表示）
							//   管理者権限なし　　課のみ表示
//							eq(adminmenuInfo().publicmode(), 7),
//							eq(adminmenuInfo().publicmode(), 8)
							eq(adminmenuInfo().publicmode(), 7)
						),
						(
							loginDataDto.isAdmin() ?
								and(
									or(
										eq(adminmenuInfo().publicmode(), 0),
										eq(adminmenuInfo().publicmode(), 3)
									),
									ne(adminmenuInfo().groupid(), gid)
								)
								:
								and(
									and(
										ne(adminmenuInfo().publicmode(), 2),
										ne(adminmenuInfo().publicmode(), 5)
									),
									ne(adminmenuInfo().groupid(), gid)
								)
						)
					)
				)
				.orderBy(asc(adminmenuInfo().classify())).getResultList();


		// 有効なメニュー項目を取得する
		boolean first_item = true;
		StringBuffer sb = new StringBuffer();
		sb.append("select * ");
		sb.append("from adminmenu_info ");

		// 上記のリストで対象となった階層以下についてライク条件を付加する
		// 配下の階層も非表示となる
		Iterator<AdminmenuInfo> itr = aiList.iterator();
		while (itr.hasNext()) {
			AdminmenuInfo ai = itr.next();
			if (first_item) {
				sb.append("where  (");
			} else {
				sb.append("and ");
			}
			sb.append("classify not like '");
			sb.append((ai.classify.substring(0, 3 * ai.level) + "%"));
			sb.append("' ");
			first_item = false;
		}
		if (!first_item) {
			sb.append(") ");
		}

		sb.append("order by classify");

		return jdbcManager.selectBySql(AdminmenuInfo.class, sb.toString())
				.getResultList();
	}

	/**
	 * 管理画面のサイドメニュー項目を取得する。
	 * 　有効・ログイン班による権限制御及び、区分の昇順で取得する
	 * @param uid : ログイン時の課ID
	 * @return
	 */
	public List<AdminmenuInfo> findMenuInfoByUnitid(Long uid) {
		// サイドメニューに表示しないリストを取得する
		// 共通:
		//   有効・無効がfalse
		//     OR 公開が班のみ、かつ非公開（3）
		//     OR 公開が班のみ、かつ全公開（4）
		//     OR 公開が班のみ、かつ限定公開（5）
		// 管理権限あり:
		//   公開が非公開（0）または課のみ、かつ非公開（6）で且つ指定グループID（課ID）と異なる
		// 管理権限なし:
		//   公開が限定公開（2）と課のみ、かつ限定公開（8）以外で且つ指定グループID（課ID）と異なる
		List<AdminmenuInfo> aiList = select()
				.where(
					or(
						or(
							eq(adminmenuInfo().valid(), false),
							eq(adminmenuInfo().publicmode(), 3),
							eq(adminmenuInfo().publicmode(), 4),
							eq(adminmenuInfo().publicmode(), 5)
						),
						(
							loginDataDto.isAdmin() ?
								and(
									or(
										eq(adminmenuInfo().publicmode(), 0),
										eq(adminmenuInfo().publicmode(), 6)
									),
									ne(adminmenuInfo().groupid(), uid)
								)
								:
								and(
									and(
										ne(adminmenuInfo().publicmode(), 2),
										ne(adminmenuInfo().publicmode(), 8)
									),
									ne(adminmenuInfo().groupid(), uid)
								)
						)
					)
				)
				.orderBy(asc(adminmenuInfo().classify())).getResultList();

		// 有効なメニュー項目を取得する
		boolean first_item = true;
		StringBuffer sb = new StringBuffer();
		sb.append("select * ");
		sb.append("from adminmenu_info ");

		// 上記のリストで対象となった階層以下についてライク条件を付加する
		// 配下の階層も非表示となる
		Iterator<AdminmenuInfo> itr = aiList.iterator();
		while (itr.hasNext()) {
			AdminmenuInfo ai = itr.next();
			if (first_item) {
				sb.append("where  (");
			} else {
				sb.append("and ");
			}
			sb.append("classify not like '");
			sb.append((ai.classify.substring(0, 3 * ai.level) + "%"));
			sb.append("' ");
			first_item = false;
		}
		if (!first_item) {
			sb.append(") ");
		}

		sb.append("order by classify");

		return jdbcManager.selectBySql(AdminmenuInfo.class, sb.toString())
				.getResultList();
	}

	static final String ADMINMENU_INFO_SQL="data/adminMenu_info.sql";

	/**
	 * 管理画面のメニューを更新します.
	 */
	public void restoreAdminmenuInfo() {
		try {
			// ファイルが存在しない状態で、削除しないように
			// ファイルが存在するかチェック
			ResourceUtil.getResourceAsFile(ADMINMENU_INFO_SQL);
			// メニューを削除
			jdbcManager.updateBySql("DELETE FROM adminmenu_info").execute();
			// メニューを登録
			jdbcManager.updateBySqlFile(ADMINMENU_INFO_SQL).execute();
			logger.info(/*lang._*/("Updated the side menu of the admin window."));
		} catch(ResourceNotFoundRuntimeException e) {
			String arg0 = ADMINMENU_INFO_SQL;
			throw new ServiceException(/*lang._*/(arg0+" not found."));
		}
	}

	public List<AdminmenuInfo> check() {
		List<AdminmenuInfo> reslist = select().leftOuterJoin(adminmenuInfo().groupInfo()).getResultList();
		List<AdminmenuInfo> nolist = new ArrayList<AdminmenuInfo>();
		for (AdminmenuInfo info : reslist) {
			if (info.groupInfo == null)
				nolist.add(info);
		}
		return nolist;
	}
}
