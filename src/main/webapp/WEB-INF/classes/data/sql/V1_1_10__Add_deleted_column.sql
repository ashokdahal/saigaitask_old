/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- メニュー情報
ALTER TABLE menu_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN menu_info.deleted IS '削除フラグ';

-- メニューテーブル情報
ALTER TABLE menutable_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN menutable_info.deleted IS '削除フラグ';

-- 地図レイヤ情報
ALTER TABLE maplayer_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN maplayer_info.deleted IS '削除フラグ';

-- テーブルマスタ情報
ALTER TABLE tablemaster_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN tablemaster_info.deleted IS '削除フラグ';

-- メニュー設定情報
ALTER TABLE menulogin_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN menulogin_info.deleted IS '削除フラグ';

-- メニュープロセス情報
ALTER TABLE menuprocess_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN menuprocess_info.deleted IS '削除フラグ';

-- メニュータスク情報
ALTER TABLE menutask_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN menutask_info.deleted IS '削除フラグ';

-- グループ情報
ALTER TABLE group_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN group_info.deleted IS '削除フラグ';

-- タスク種別情報
ALTER TABLE menutasktype_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN menutasktype_info.deleted IS '削除フラグ';
