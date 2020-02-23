/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
-- 地図マスター情報情報
ALTER TABLE mapmaster_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN mapmaster_info.deleted IS '削除フラグ';
