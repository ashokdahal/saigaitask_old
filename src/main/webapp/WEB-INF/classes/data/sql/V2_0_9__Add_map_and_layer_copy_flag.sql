/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

/* 地図コピーフラグを追加、V1.4系からの更新の場合はデフォルトtrueとする */
ALTER TABLE mapmaster_info ADD COLUMN copy BOOLEAN DEFAULT TRUE;

/** 今後、新規に追加される mapmaster_info はデフォルトfalseとする */
ALTER TABLE mapmaster_info ALTER COLUMN copy SET DEFAULT FALSE;

COMMENT ON COLUMN tablemaster_info.copy IS 'コピーフラグ';

/** レイヤのコピーフラグを boolean から smallint に拡張 */
ALTER TABLE tablemaster_info ALTER COLUMN copy TYPE smallint USING CASE WHEN copy=TRUE THEN 1 ELSE 0 END;
