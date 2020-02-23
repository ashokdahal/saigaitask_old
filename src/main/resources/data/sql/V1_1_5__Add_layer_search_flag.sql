/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

/* 地物の一括選択機能 ----------------------------------------------------------- */
/* 地図レイヤ情報に カラム追加 */
ALTER TABLE maplayer_info ADD COLUMN searchable boolean default true;
COMMENT ON COLUMN maplayer_info.searchable IS '検索フラグ';
/* 既存のレイヤの検索フラグをすべて有効に設定する */
UPDATE maplayer_info SET searchable=true;

/* 地図参照レイヤ情報に カラム追加 */
ALTER TABLE mapreferencelayer_info ADD COLUMN searchable boolean default true;
COMMENT ON COLUMN mapreferencelayer_info.searchable IS '検索フラグ';
/* 既存のレイヤの検索フラグをすべて有効に設定する */
UPDATE mapreferencelayer_info SET searchable=true;

/* 外部地図の集計機能 ----------------------------------------------------------- */
create table externallistsummarycolumn_info (
	id bigserial not null primary key,
	menuinfoid bigint,
	name text,
	function text,
	condition text,
	valid boolean default true,
	disporder int
);
create index externallistsummarycolumn_info_menuinfoid_idx
	on externallistsummarycolumn_info (menuinfoid);
COMMENT ON TABLE externallistsummarycolumn_info  IS '外部リスト集計項目情報';
COMMENT ON COLUMN externallistsummarycolumn_info.id  IS 'ID';
COMMENT ON COLUMN externallistsummarycolumn_info.menuinfoid  IS 'メニューID';
COMMENT ON COLUMN externallistsummarycolumn_info.name  IS '集計項目名';
COMMENT ON COLUMN externallistsummarycolumn_info.function  IS '演算';
COMMENT ON COLUMN externallistsummarycolumn_info.condition  IS '絞り込み条件';
COMMENT ON COLUMN externallistsummarycolumn_info.valid  IS '有効・無効';
COMMENT ON COLUMN externallistsummarycolumn_info.disporder  IS '表示順';
