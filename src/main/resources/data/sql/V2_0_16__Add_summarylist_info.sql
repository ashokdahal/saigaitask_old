/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
CREATE TABLE summarylist_info (
	id bigserial not null primary key,
	localgovinfoid bigint,
	targettablemasterinfoid bigint,
	tablemasterinfoid bigint,
	localgovcode text
);

COMMENT ON COLUMN summarylist_info.id IS 'ID';
COMMENT ON COLUMN summarylist_info.localgovinfoid IS '地方自治体情報ID';
COMMENT ON COLUMN summarylist_info.targettablemasterinfoid IS '集計対象テーブルID';
COMMENT ON COLUMN summarylist_info.tablemasterinfoid IS 'テーブルマスタ情報ID';
COMMENT ON COLUMN summarylist_info.localgovcode IS '自治体コード項目名';

-- 旧バージョンの自治体セットアッパーのインポート用で残す
-- ALTER TABLE summarylistcolumn_info DROP COLUMN menuinfoid ;
-- ALTER TABLE summarylistcolumn_info DROP COLUMN name ;
-- ALTER TABLE summarylistcolumn_info DROP COLUMN disporder ;
-- ALTER TABLE summarylistcolumn_info ADD COLUMN menuinfoid bigint;
-- ALTER TABLE summarylistcolumn_info ADD COLUMN name text;
-- ALTER TABLE summarylistcolumn_info ADD COLUMN disporder int;

ALTER TABLE summarylistcolumn_info ADD COLUMN summarylistinfoid bigint;
COMMENT ON COLUMN summarylistcolumn_info.summarylistinfoid IS '集計リスト情報ID';
ALTER TABLE summarylistcolumn_info ADD COLUMN attrid text;
COMMENT ON COLUMN summarylistcolumn_info.attrid IS '集計結果保存属性';
