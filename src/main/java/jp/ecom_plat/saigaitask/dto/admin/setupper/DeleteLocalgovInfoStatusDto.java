/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto.admin.setupper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ecom_plat.saigaitask.action.admin.setupper.DeleteLocalgovInfoAction.FutureResult;

/**
 * 自治体削除処理状況Dto
 * アプリケーションスコープ
 */

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component

public class DeleteLocalgovInfoStatusDto {

	/**
	 * 非同期スレッドプール
	 */
	public ExecutorService exec = Executors.newFixedThreadPool(10);

	ConcurrentHashMap<UUID, Future<FutureResult>> map = new ConcurrentHashMap<UUID, Future<FutureResult>>();

	/**
	 * 
	 * @param future 自治体削除タスク
	 * @return 発行UUID
	 */
	public UUID addFuture(Future<FutureResult> future) {
		UUID uuid = UUID.randomUUID();
		map.put(uuid, future);
		return uuid;
	}

	/**
	 * UUIDから Future を取得
	 * @param uuid UUID
	 * @return Future
	 */
	public Future<FutureResult> getFuture(UUID uuid) {
		return map.get(uuid);
	}

	/**
	 * システム停止時の処理
	 */
	public void destroy() {
		exec.shutdown();
	}
}
