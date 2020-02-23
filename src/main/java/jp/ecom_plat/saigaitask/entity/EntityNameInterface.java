/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity;

/**
 * エンティティ名称を取得するためのインタフェース.
 * 各エンティティのNamesクラスでこのインタフェースを実装し、
 * ユーザがわかる名称を返すようにすること。
 * 
 * 自治体設定関連の場合は、管理画面のグリッドに表示する名称とあわせる。
 */
public interface EntityNameInterface {

	/**
	 * エンティティの名称を取得します.
	 * @return エンティティ名称
	 */
	public String entityName();
}
