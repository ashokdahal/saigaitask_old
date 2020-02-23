/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

--
-- 同時複数災害の対応
-- track_data.trackmapinfoid を追加
ALTER TABLE track_data ADD COLUMN trackmapinfoid bigint;
COMMENT ON COLUMN track_data.trackmapinfoid IS '地図情報ID';
ALTER TABLE track_data ADD FOREIGN KEY (trackmapinfoid) REFERENCES trackmap_info(id);
-- データをコピーする
UPDATE track_data
SET trackmapinfoid=(SELECT id FROM trackmap_info WHERE trackdataid=track_data.id)
WHERE EXISTS(SELECT * FROM trackmap_info WHERE trackdataid=track_data.id);
ALTER TABLE trackmap_info DROP COLUMN trackdataid;

-- 外部参照制約を追加
ALTER TABLE track_data ADD FOREIGN KEY (localgovinfoid) REFERENCES localgov_info(id);

-- localgov_info.preflocalgovinfoid を追加
ALTER TABLE localgov_info ADD COLUMN preflocalgovinfoid bigint;
COMMENT ON COLUMN localgov_info.preflocalgovinfoid IS '都道府県(自治体ID)';
ALTER TABLE localgov_info ADD FOREIGN KEY (preflocalgovinfoid) REFERENCES localgov_info(id);


--
-- 災害グループ
-- trackgroup_data を追加
CREATE TABLE trackgroup_data (
	id bigserial primary key,
	preftrackdataid bigint,
	citytrackdataid bigint
);
COMMENT ON TABLE trackgroup_data IS '記録グループデータ';
COMMENT ON COLUMN trackgroup_data.id IS '記録グループID';
COMMENT ON COLUMN trackgroup_data.preftrackdataid IS '記録データID(都道府県)';
COMMENT ON COLUMN trackgroup_data.citytrackdataid IS '記録データID(市町村)';
ALTER TABLE trackgroup_data ADD FOREIGN KEY (preftrackdataid) REFERENCES track_data(id);
ALTER TABLE trackgroup_data ADD FOREIGN KEY (citytrackdataid) REFERENCES track_data(id);

-- ALTER TABLE meteorequest_info DROP COLUMN map;


--
-- 体制のレイヤ化
-- DROP TABLE stationorder_data;
CREATE TABLE stationlayer_info (
	id bigserial primary key,
	tablemasterinfoid bigint,
	stationclassattrid text,
	shifttimeattrid text,
	registtimeattrid text,
	closetimeattrid text
);
COMMENT ON TABLE stationlayer_info IS '体制レイヤ情報';
COMMENT ON COLUMN stationlayer_info.id IS 'ID';
COMMENT ON COLUMN stationlayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN stationlayer_info.stationclassattrid IS '体制区分の属性ID';
COMMENT ON COLUMN stationlayer_info.shifttimeattrid IS '移行時間の属性ID';
COMMENT ON COLUMN stationlayer_info.registtimeattrid IS '登録時間の属性ID';
COMMENT ON COLUMN stationlayer_info.closetimeattrid IS '終了時間の属性ID';
ALTER TABLE stationlayer_info ADD FOREIGN KEY (tablemasterinfoid) REFERENCES tablemaster_info(id);
-- DELETE FROM tablemaster_info WHERE tablename='stationorder_data';

INSERT INTO timelinemenue_master VALUES (3, '体制', 3);

--
-- 都道府県の集計機能
-- 外部リスト集計項目を集計リスト項目情報に変更
ALTER TABLE externallistsummarycolumn_info RENAME TO summarylistcolumn_info;
COMMENT ON TABLE summarylistcolumn_info IS '集計リスト項目情報';
ALTER SEQUENCE externallistsummarycolumn_info_id_seq RENAME TO summarylistcolumn_info_id_seq;
ALTER INDEX externallistsummarycolumn_info_pkey RENAME TO summarylistcolumn_info_pkey;
ALTER INDEX externallistsummarycolumn_info_menuinfoid_idx RENAME TO ummarylistcolumn_info_menuinfoid_idx;

-- DROP TABLE generalizationhistory_data;
CREATE TABLE generalizationhistory_data (
	id bigserial primary key,
	trackdataid bigint,
--	menutypeid bigint,
	pagetype text,
	listid text,
	csvpath text,
	pdfpath text,
	note text,
	registtime timestamp,
	deleted boolean
);
COMMENT ON TABLE generalizationhistory_data IS '総括表履歴データ';
COMMENT ON COLUMN generalizationhistory_data.id IS 'ID';
COMMENT ON COLUMN generalizationhistory_data.trackdataid IS '記録ID';
--COMMENT ON COLUMN generalizationhistory_data.menutypeid IS 'メニュータイプID';
COMMENT ON COLUMN generalizationhistory_data.pagetype IS 'ページタイプ';
COMMENT ON COLUMN generalizationhistory_data.listid IS 'リストID';
COMMENT ON COLUMN generalizationhistory_data.csvpath IS 'CSVファイルパス';
COMMENT ON COLUMN generalizationhistory_data.pdfpath IS 'PDFファイルパス';
COMMENT ON COLUMN generalizationhistory_data.note IS '備考';
COMMENT ON COLUMN generalizationhistory_data.registtime IS '登録日時';
COMMENT ON COLUMN generalizationhistory_data.deleted IS '削除フラグ';
ALTER TABLE generalizationhistory_data
	ADD FOREIGN KEY(trackdataid) REFERENCES track_data(id);
-- ALTER TABLE generalizationhistory_data
-- 	ADD FOREIGN KEY(menutypeid) REFERENCES menutype_master(id);

--
-- 震度情報の地図表示
-- 未使用のカラム map を削除
-- ALTER TABLE meteorequest_info DROP COLUMN map;
-- 気象情報トリガー情報に、レイヤ追加フラグ addlayer を追加
ALTER TABLE meteotrigger_info ADD COLUMN addlayer boolean default true;
COMMENT ON COLUMN meteotrigger_info.addlayer IS 'レイヤ追加フラグ';
-- 気象情報レイヤ情報 追加
CREATE TABLE meteolayer_info (
	id bigserial primary key,
	menuinfoid bigint,
	meteotypeid int,
	visible boolean,
	closed boolean,
	searchable boolean,
	valid boolean,
	disporder int
);
COMMENT ON TABLE meteolayer_info IS '気象情報レイヤ情報';
COMMENT ON COLUMN meteolayer_info.id IS 'ID';
COMMENT ON COLUMN meteolayer_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN meteolayer_info.meteotypeid IS '気象情報取得種別ID';
COMMENT ON COLUMN meteolayer_info.visible IS '初期表示フラグ';
COMMENT ON COLUMN meteolayer_info.closed IS '凡例折りたたみ';
COMMENT ON COLUMN meteolayer_info.searchable IS '検索フラグ';
COMMENT ON COLUMN meteolayer_info.valid IS '有効・無効';
COMMENT ON COLUMN meteolayer_info.disporder IS '表示順';
ALTER TABLE meteolayer_info ADD FOREIGN KEY (menuinfoid) REFERENCES menu_info(id);
ALTER TABLE meteolayer_info ADD FOREIGN KEY (meteotypeid) REFERENCES meteotype_master(id);
-- ALTER TABLE meteolayer_info ALTER COLUMN meteotypeid TYPE INTEGER;

-- AreaInformationCity を１つのテーブルに統合するため、関連テーブルを削除
-- DROP TABLE meteowarnarea_master;
-- DROP TABLE meteolandslidearea_master;
-- DROP TABLE meteotatsumakiarea_master;
CREATE TABLE meteoareainformationcity_master (
	id bigserial primary key,
	code text,
	name text,
	namewarn text,
	warnflag boolean,
	tatsumakiflag boolean,
	landslideflag boolean,
	riverflag boolean,
	nameseismic text,
	namevolcano text,
	point text,
	line text,
	polygon text,
	disporder int
);
COMMENT ON TABLE meteoareainformationcity_master IS '気象情報レイヤ情報';
COMMENT ON COLUMN meteoareainformationcity_master.id IS 'ID';
COMMENT ON COLUMN meteoareainformationcity_master.code IS 'コード';
COMMENT ON COLUMN meteoareainformationcity_master.name IS 'コードが示す地域等';
COMMENT ON COLUMN meteoareainformationcity_master.namewarn IS '市町村名（気象関係）';
COMMENT ON COLUMN meteoareainformationcity_master.warnflag IS '"気象警報・注意報"及び"気象特別警報報知"で使用';
COMMENT ON COLUMN meteoareainformationcity_master.tatsumakiflag IS '"竜巻注意情報"で使用';
COMMENT ON COLUMN meteoareainformationcity_master.landslideflag IS '"土砂災害警戒情報"で使用';
COMMENT ON COLUMN meteoareainformationcity_master.riverflag IS '"指定河川洪水予報"で使用';
COMMENT ON COLUMN meteoareainformationcity_master.nameseismic IS '市町村名（地震津波関係）';
COMMENT ON COLUMN meteoareainformationcity_master.namevolcano IS '市町村名（火山関係）';
COMMENT ON COLUMN meteoareainformationcity_master.point IS '点（WKT）';
COMMENT ON COLUMN meteoareainformationcity_master.line IS '線（WKT）';
COMMENT ON COLUMN meteoareainformationcity_master.polygon IS '面（WKT）';
COMMENT ON COLUMN meteoareainformationcity_master.disporder IS '表示順';
CREATE INDEX meteoareainformationcity_master_code_idx
	ON meteoareainformationcity_master (code);

-- 震度グループレイヤデータ 追加
CREATE TABLE earthquakegrouplayer_data (
	id bigserial not null primary key,
	trackmapinfoid bigint,
	mapmasterinfoid bigint,
	layerid text
);
COMMENT ON TABLE earthquakegrouplayer_data IS '震度グループレイヤデータ';
COMMENT ON COLUMN earthquakegrouplayer_data.id IS 'ID';
COMMENT ON COLUMN earthquakegrouplayer_data.trackmapinfoid IS '地図情報ID';
COMMENT ON COLUMN earthquakegrouplayer_data.mapmasterinfoid IS '地図マスタID';
COMMENT ON COLUMN earthquakegrouplayer_data.layerid IS 'レイヤID';
ALTER TABLE earthquakegrouplayer_data ADD FOREIGN KEY (trackmapinfoid) REFERENCES trackmap_info(id);
ALTER TABLE earthquakegrouplayer_data ADD FOREIGN KEY (mapmasterinfoid) REFERENCES mapmaster_info(id);

-- alter table earthquakegrouplayer_data drop column trackdataid;
-- alter table earthquakegrouplayer_data add column trackmapinfoid bigint;
-- ALTER TABLE earthquakegrouplayer_data ADD FOREIGN KEY (trackmapinfoid) REFERENCES trackmap_info(id);

-- 震度レイヤデータ 追加
CREATE TABLE earthquakelayer_data (
	id bigserial not null primary key,
	meteodataid bigint,
	earthquakegrouplayerid bigint,
	eventid text,
	origintime timestamp,
	reportdatetime timestamp,
	layerid text
);
COMMENT ON TABLE earthquakelayer_data IS '震度レイヤデータ';
COMMENT ON COLUMN earthquakelayer_data.id IS 'ID';
COMMENT ON COLUMN earthquakelayer_data.meteodataid IS '気象情報取得データID';
COMMENT ON COLUMN earthquakelayer_data.earthquakegrouplayerid IS '震度グループレイヤID';
COMMENT ON COLUMN earthquakelayer_data.eventid IS '地震識別番号';
COMMENT ON COLUMN earthquakelayer_data.origintime IS '地震発生日時';
COMMENT ON COLUMN earthquakelayer_data.reportdatetime IS '発表時刻';
COMMENT ON COLUMN earthquakelayer_data.layerid IS 'レイヤID';
ALTER TABLE earthquakelayer_data ADD FOREIGN KEY (meteodataid) REFERENCES meteo_data(id);
ALTER TABLE earthquakelayer_data ADD FOREIGN KEY (earthquakegrouplayerid) REFERENCES earthquakegrouplayer_data(id);


--
-- 掲示板
create table thread_data (
	id bigserial not null primary key,
	trackdataid bigint,
	groupid bigint,
	title text,
	priority int,
	registtime timestamp,
	closetime timestamp,
	deleted boolean
);
COMMENT ON TABLE thread_data IS 'スレッドデータ';
COMMENT ON COLUMN thread_data.id IS 'ID';
COMMENT ON COLUMN thread_data.trackdataid IS '記録ID';
COMMENT ON COLUMN thread_data.groupid IS 'スレッド作成班ID';
COMMENT ON COLUMN thread_data.title IS 'スレッドタイトル';
COMMENT ON COLUMN thread_data.priority IS '優先度(1: 緊急、2:高、3:中、4:低)';
COMMENT ON COLUMN thread_data.registtime IS '登録日時';
COMMENT ON COLUMN thread_data.closetime IS '閉鎖日時';
COMMENT ON COLUMN thread_data.deleted IS '削除フラグ';
ALTER TABLE thread_data ADD FOREIGN KEY (trackdataid)
	REFERENCES track_data(id);
ALTER TABLE thread_data ADD FOREIGN KEY (groupid)
	REFERENCES group_info(id);

create table threadsendto_data (
	id bigserial not null primary key,
	threaddataid bigint,
	groupid bigint,
	updatetime timestamp
);
COMMENT ON TABLE threadsendto_data IS 'スレッド送信先データ';
COMMENT ON COLUMN threadsendto_data.id IS 'ID';
COMMENT ON COLUMN threadsendto_data.threaddataid IS 'スレッドID';
COMMENT ON COLUMN threadsendto_data.groupid IS '班ID';
COMMENT ON COLUMN threadsendto_data.updatetime IS '最終更新時間（既読とした時間）';
ALTER TABLE threadsendto_data ADD FOREIGN KEY (threaddataid)
	REFERENCES thread_data(id);
ALTER TABLE threadsendto_data ADD FOREIGN KEY (groupid)
	REFERENCES group_info(id);

create table threadresponse_data (
	id bigserial not null primary key,
	threaddataid bigint,
	groupid bigint,
	message text,
	fileflag boolean,
	url text,
	registtime timestamp,
	updatetime timestamp,
	deletetime timestamp
);
COMMENT ON TABLE threadresponse_data IS 'スレッドレスポンスデータ';
COMMENT ON COLUMN threadresponse_data.id IS 'ID';
COMMENT ON COLUMN threadresponse_data.threaddataid IS 'スレッドID';
COMMENT ON COLUMN threadresponse_data.groupid IS '班ID';
COMMENT ON COLUMN threadresponse_data.message IS '送信メッセージ or ファイルパス';
COMMENT ON COLUMN threadresponse_data.fileflag IS 'True : ファイル false : 送信メッセージ';
COMMENT ON COLUMN threadresponse_data.url IS '再現URL';
COMMENT ON COLUMN threadresponse_data.registtime IS '送信時間';
COMMENT ON COLUMN threadresponse_data.updatetime IS '更新時間';
COMMENT ON COLUMN threadresponse_data.deletetime IS '削除時間';
ALTER TABLE threadresponse_data ADD FOREIGN KEY (threaddataid)
	REFERENCES thread_data(id);
ALTER TABLE threadresponse_data ADD FOREIGN KEY (groupid)
	REFERENCES group_info(id);

-- メニュー種別の ID=18 に追加
insert into menutype_master(id, name, disporder) values(18, '4号様式の集計・総括', 18);
-- メニュー種別の ID=19 に追加
insert into menutype_master(id, name, disporder) values(19, '体制の集計・総括', 19);
-- メニュー種別の ID=20 に追加
insert into menutype_master(id, name, disporder) values(20, '公共情報コモンズ（メディア）：おしらせ', 20);
-- メニュー種別の ID=21 に追加
insert into menutype_master(id, name, disporder) values(21, '公共情報コモンズ（メディア）：イベント情報', 21);
-- メニュー種別の ID=22 に追加
insert into menutype_master(id, name, disporder) values(22, '公共情報コモンズ（メディア）：被害情報', 22);
-- メニュー種別の ID=23 に追加
insert into menutype_master(id, name, disporder) values(23, '【利用不可】公共情報コモンズ（メディア）：国民保護情報', 23);
-- メニュー種別の ID=24 に追加
insert into menutype_master(id, name, disporder) values(24, '被災集計の総括表', 24);
