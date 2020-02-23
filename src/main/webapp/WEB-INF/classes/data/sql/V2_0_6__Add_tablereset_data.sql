/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE tablemaster_info ADD COLUMN reset boolean default false;
ALTER TABLE tablemaster_info ALTER COLUMN reset set not null;
COMMENT ON COLUMN tablemaster_info.reset IS 'リセットフラグ';

-- テーブルリセット対象属性情報
CREATE TABLE tableresetcolumn_data (
    id bigserial not null primary key,
    attrid text,
    tablemasterinfoid bigint
);

COMMENT ON TABLE tableresetcolumn_data IS 'テーブルリセット対象属性情報';
COMMENT ON COLUMN tableresetcolumn_data.id IS 'ID';
COMMENT ON COLUMN tableresetcolumn_data.attrid IS '属性項目名';
COMMENT ON COLUMN tableresetcolumn_data.tablemasterinfoid IS 'テーブルID';
