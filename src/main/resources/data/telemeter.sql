/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

---テレメータ---

create table telemeterserver_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	url text,
	userid text,
	password text,
	"interval" int,
	delay int default 60,
	note text default 0,
	valid boolean default true
);

COMMENT ON TABLE telemeterserver_info IS 'テレメータサーバ情報';
COMMENT ON COLUMN telemeterserver_info.id IS 'ID';
COMMENT ON COLUMN telemeterserver_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN telemeterserver_info.url IS 'URL';
COMMENT ON COLUMN telemeterserver_info.userid IS 'ユーザ名';
COMMENT ON COLUMN telemeterserver_info.password IS 'パスワード';
COMMENT ON COLUMN telemeterserver_info."interval" IS 'インターバル（秒）';
COMMENT ON COLUMN telemeterserver_info.delay IS '遅延';
COMMENT ON COLUMN telemeterserver_info.note IS '備考';
COMMENT ON COLUMN telemeterserver_info.valid IS '有効／無効';

create table telemeteroffice_info (
	id bigserial not null primary key, 
	telemeterserverinfoid bigint,
	officecode text,
	officename text,
	note text,
	valid boolean default true
);

COMMENT ON TABLE telemeteroffice_info IS 'テレメータ管理事務所情報';
COMMENT ON COLUMN telemeteroffice_info.id IS 'ID';
COMMENT ON COLUMN telemeteroffice_info.telemeterserverinfoid IS 'テレメータサーバ情報';
COMMENT ON COLUMN telemeteroffice_info.officecode IS '管理事務所番号';
COMMENT ON COLUMN telemeteroffice_info.officename IS '管理事務所名';
COMMENT ON COLUMN telemeteroffice_info.note IS '備考';
COMMENT ON COLUMN telemeteroffice_info.valid IS '有効／無効';

create table telemeterpoint_info (
	id bigserial not null primary key, 
	telemeterofficeinfoid bigint,
	itemkindcode int,
	pointcode bigint,
	itemcode text,
	note text,
	valid boolean default true
);

COMMENT ON TABLE telemeterpoint_info IS 'テレメータ観測所情報';
COMMENT ON COLUMN telemeterpoint_info.id IS 'ID';
COMMENT ON COLUMN telemeterpoint_info.telemeterofficeinfoid IS '管理事務所ID';
COMMENT ON COLUMN telemeterpoint_info.itemkindcode IS 'データ種別コード';
COMMENT ON COLUMN telemeterpoint_info.pointcode IS '観測所コード';
COMMENT ON COLUMN telemeterpoint_info.itemcode IS 'アイテムコードのCSV';
COMMENT ON COLUMN telemeterpoint_info.note IS '備考';
COMMENT ON COLUMN telemeterpoint_info.valid IS '有効／無効';

create table telemeterfile_data (
	id bigserial not null primary key, 
	telemeterofficeinfoid bigint,
	filename text,
	observtime timestamp
);
create index telemeterfile_data_filename_idx on telemeterfile_data (filename);

COMMENT ON TABLE telemeterfile_data IS 'テレメータファイル情報';
COMMENT ON COLUMN telemeterfile_data.id IS 'ID';
COMMENT ON COLUMN telemeterfile_data.telemeterofficeinfoid IS '管理事務所ID';
COMMENT ON COLUMN telemeterfile_data.filename IS 'ファイル名';
COMMENT ON COLUMN telemeterfile_data.observtime IS '取得時間';

create table telemeter_data (
	id bigserial not null primary key, 
	code bigint,
	itemcode int,
	contentscode int,
	observtime timestamp,
	val float
);
create index telemeter_data_code_idx on telemeter_data (code);
create index telemeter_data_itemcode_idx on telemeter_data (itemcode);

COMMENT ON TABLE telemeter_data IS 'テレメータデータ';
COMMENT ON COLUMN telemeter_data.id IS 'ID';
COMMENT ON COLUMN telemeter_data.code IS '観測所コード';
COMMENT ON COLUMN telemeter_data.itemcode IS 'データ項目コード';
COMMENT ON COLUMN telemeter_data.contentscode IS 'コンテンツコード';
COMMENT ON COLUMN telemeter_data.observtime IS '観測時刻';
COMMENT ON COLUMN telemeter_data.val IS '値';

create table telemetertime_data (
	id bigserial not null primary key, 
	code bigint,
	itemcode int,
	contentscode int,
	observtime timestamp,
	val timestamp
);
create index telemetertime_data_code_idx on telemetertime_data (code);
create index telemetertime_data_itemcode_idx on telemetertime_data (itemcode);

COMMENT ON TABLE telemetertime_data IS 'テレメータ時刻データ';
COMMENT ON COLUMN telemetertime_data.id IS 'ID';
COMMENT ON COLUMN telemetertime_data.code IS '観測所コード';
COMMENT ON COLUMN telemetertime_data.itemcode IS 'データ項目コード';
COMMENT ON COLUMN telemetertime_data.contentscode IS 'コンテンツコード';
COMMENT ON COLUMN telemetertime_data.observtime IS '観測時間';
COMMENT ON COLUMN telemetertime_data.val IS '値';

---リストテーブル---

create table observ_master (
	id serial not null primary key, 
	name text,
	disporder int
);

COMMENT ON TABLE observ_master IS 'テレメータデータ';
COMMENT ON COLUMN observ_master.id IS 'ID';
COMMENT ON COLUMN observ_master.name IS '監視観測名';
COMMENT ON COLUMN observ_master.disporder IS '表示順';

create table observmenu_info (
	id bigserial not null primary key, 
	menuinfoid bigint,
	count int default 10,
	note text
);

COMMENT ON TABLE observmenu_info IS '監視観測メニュー情報';
COMMENT ON COLUMN observmenu_info.id IS 'ID';
COMMENT ON COLUMN observmenu_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN observmenu_info.count IS 'データ表示回数';
COMMENT ON COLUMN observmenu_info.note IS '備考';

create table observlist_info (
	id bigserial not null primary key, 
	observmenuinfoid bigint,
	observid int,
	observatoryinfoid bigint,
	itemcode int,
	disporder int,
	note text
);

COMMENT ON TABLE observlist_info IS '監視観測リスト情報';
COMMENT ON COLUMN observlist_info.id IS 'ID';
COMMENT ON COLUMN observlist_info.observmenuinfoid IS '監視観測メニューID';
COMMENT ON COLUMN observlist_info.observid IS '監視観測ID';
COMMENT ON COLUMN observlist_info.observatoryinfoid IS '観測所ID';
COMMENT ON COLUMN observlist_info.itemcode IS 'データ項目コード';
COMMENT ON COLUMN observlist_info.disporder IS '表示順';
COMMENT ON COLUMN observlist_info.note IS '備考';

create table observatoryrainlayer_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	tablemasterinfoid bigint,
	attrvalue text,
	attrtime text,
	attrlevel text,
	note text
);

COMMENT ON TABLE observatoryrainlayer_info IS '雨量観測地点レイヤ情報';
COMMENT ON COLUMN observatoryrainlayer_info.id IS 'ID';
COMMENT ON COLUMN observatoryrainlayer_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN observatoryrainlayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN observatoryrainlayer_info.attrvalue IS '値の属性ID';
COMMENT ON COLUMN observatoryrainlayer_info.attrtime IS '時刻の属性ID';
COMMENT ON COLUMN observatoryrainlayer_info.attrlevel IS 'レベルの属性ID';
COMMENT ON COLUMN observatoryrainlayer_info.note IS '備考';

create table observatoryrain_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	areacode int,
	officecode int,
	officename text,
	jurisfacilitycode int,
	jurisfacilityname text,
	basin text,
	river text,
	obsrvtncode int,
	name text,
	readname text,
	latitude float8,
	longitude float8,
	altitude text,
	prefname text,
	address text,
	tablemasterinfoid bigint,
	featureid bigint,
	iframe boolean default false,
	url text,
	width int,
	height int,
	note text
);

COMMENT ON TABLE observatoryrain_info IS '雨量観測地点情報';
COMMENT ON COLUMN observatoryrain_info.id IS 'ID';
COMMENT ON COLUMN observatoryrain_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN observatoryrain_info.areacode IS 'エリア番号';
COMMENT ON COLUMN observatoryrain_info.officecode IS '管理事務所番号';
COMMENT ON COLUMN observatoryrain_info.officename IS '管理事務所名';
COMMENT ON COLUMN observatoryrain_info.jurisfacilitycode IS '所管機関コード';
COMMENT ON COLUMN observatoryrain_info.jurisfacilityname IS '所管機関名';
COMMENT ON COLUMN observatoryrain_info.basin IS '水系名';
COMMENT ON COLUMN observatoryrain_info.river IS '河川名';
COMMENT ON COLUMN observatoryrain_info.obsrvtncode IS '観測所番号';
COMMENT ON COLUMN observatoryrain_info.name IS '観測所名';
COMMENT ON COLUMN observatoryrain_info.readname IS 'ふりがな';
COMMENT ON COLUMN observatoryrain_info.latitude IS '緯度';
COMMENT ON COLUMN observatoryrain_info.longitude IS '経度';
COMMENT ON COLUMN observatoryrain_info.altitude IS '標高';
COMMENT ON COLUMN observatoryrain_info.prefname IS '都道府県';
COMMENT ON COLUMN observatoryrain_info.address IS '所在地';
COMMENT ON COLUMN observatoryrain_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN observatoryrain_info.featureid IS 'フィーチャＩＤ';
COMMENT ON COLUMN observatoryrain_info.url IS 'URL';
COMMENT ON COLUMN observatoryrain_info.iframe IS 'true:iframe, false:popup';
COMMENT ON COLUMN observatoryrain_info.width IS '幅';
COMMENT ON COLUMN observatoryrain_info.height IS '高さ';
COMMENT ON COLUMN observatoryrain_info.note IS '備考';

create table observatoryriverlayer_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	tablemasterinfoid bigint,
	attrvalue text,
	attrtime text,
	attrlevel text,
	note text
);

COMMENT ON TABLE observatoryriverlayer_info IS '河川水位観測地点レイヤ情報';
COMMENT ON COLUMN observatoryriverlayer_info.id IS 'ID';
COMMENT ON COLUMN observatoryriverlayer_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN observatoryriverlayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN observatoryriverlayer_info.attrvalue IS '値の属性ID';
COMMENT ON COLUMN observatoryriverlayer_info.attrtime IS '時刻の属性ID';
COMMENT ON COLUMN observatoryriverlayer_info.attrlevel IS 'レベルの属性ID';
COMMENT ON COLUMN observatoryriverlayer_info.note IS '備考';

create table observatoryriver_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	areacode int,
	officecode int,
	officename text,
	jurisfacilitycode int,
	jurisfacilityname text,
	basin text,
	river text,
	obsrvtncode int,
	name text,
	readname text,
	latitude float8,
	longitude float8,
	altitude text,
	prefname text,
	address text,
	waterlevel1 float4,
	waterlevel2 float4,
	waterlevel3 float4,
	waterlevel4 float4,
	waterlevel5 float4,
	levelmax float4,
	levelmin float4,
	levelbase float4,
	tablemasterinfoid bigint,
	featureid bigint,
	iframe boolean default false,
	url text,
	width int,
	height int,
	note text
);

COMMENT ON TABLE observatoryriver_info IS '河川水位観測地点情報';
COMMENT ON COLUMN observatoryriver_info.id IS 'ID';
COMMENT ON COLUMN observatoryriver_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN observatoryriver_info.areacode IS 'エリア番号';
COMMENT ON COLUMN observatoryriver_info.officecode IS '管理事務所番号';
COMMENT ON COLUMN observatoryriver_info.officename IS '管理事務所名';
COMMENT ON COLUMN observatoryriver_info.jurisfacilitycode IS '所管機関コード';
COMMENT ON COLUMN observatoryriver_info.jurisfacilityname IS '所管機関名';
COMMENT ON COLUMN observatoryriver_info.basin IS '水系名';
COMMENT ON COLUMN observatoryriver_info.river IS '河川名';
COMMENT ON COLUMN observatoryriver_info.obsrvtncode IS '観測所番号';
COMMENT ON COLUMN observatoryriver_info.name IS '観測所名';
COMMENT ON COLUMN observatoryriver_info.readname IS 'ふりがな';
COMMENT ON COLUMN observatoryriver_info.latitude IS '緯度';
COMMENT ON COLUMN observatoryriver_info.longitude IS '経度';
COMMENT ON COLUMN observatoryriver_info.altitude IS '標高';
COMMENT ON COLUMN observatoryriver_info.prefname IS '都道府県';
COMMENT ON COLUMN observatoryriver_info.address IS '所在地';
COMMENT ON COLUMN observatoryriver_info.waterlevel1 IS '水防団待機水位';
COMMENT ON COLUMN observatoryriver_info.waterlevel2 IS '氾濫注意水位';
COMMENT ON COLUMN observatoryriver_info.waterlevel3 IS '避難判断水位';
COMMENT ON COLUMN observatoryriver_info.waterlevel4 IS '氾濫危険水位';
COMMENT ON COLUMN observatoryriver_info.waterlevel5 IS '計画高水位';
COMMENT ON COLUMN observatoryriver_info.levelmax IS '表示最大水位';
COMMENT ON COLUMN observatoryriver_info.levelmin IS '表示最小水位';
COMMENT ON COLUMN observatoryriver_info.levelbase IS '基準標高';
COMMENT ON COLUMN observatoryriver_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN observatoryriver_info.featureid IS 'フィーチャＩＤ';
COMMENT ON COLUMN observatoryriver_info.url IS 'URL';
COMMENT ON COLUMN observatoryriver_info.iframe IS 'true:iframe, false:popup';
COMMENT ON COLUMN observatoryriver_info.width IS '幅';
COMMENT ON COLUMN observatoryriver_info.height IS '高さ';
COMMENT ON COLUMN observatoryriver_info.note IS '備考';

create table observatorydamlayer_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	tablemasterinfoid bigint,
	attrvalue text,
	attrtime text,
	attrlevel text,
	note text
);

COMMENT ON TABLE observatorydamlayer_info IS 'ダム観測所レイヤ情報';
COMMENT ON COLUMN observatorydamlayer_info.id IS 'ID';
COMMENT ON COLUMN observatorydamlayer_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN observatorydamlayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN observatorydamlayer_info.attrvalue IS '値の属性ID';
COMMENT ON COLUMN observatorydamlayer_info.attrtime IS '時刻の属性ID';
COMMENT ON COLUMN observatorydamlayer_info.attrlevel IS 'レベルの属性ID';
COMMENT ON COLUMN observatorydamlayer_info.note IS '備考';

create table observatorydam_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	areacode int,
	officecode int,
	officename text,
	jurisfacilitycode int,
	jurisfacilityname text,
	obsrvtncode int,
	name text,
	readname text,
	latitude float8,
	longitude float8,
	prefname text,
	address text,
	tablemasterinfoid bigint,
	featureid bigint,
	iframe boolean default false,
	url text,
	width int,
	height int,
	note text
);

COMMENT ON TABLE observatorydam_info IS 'ダム観測所情報';
COMMENT ON COLUMN observatorydam_info.id IS 'ID';
COMMENT ON COLUMN observatorydam_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN observatorydam_info.areacode IS 'エリア番号';
COMMENT ON COLUMN observatorydam_info.officecode IS '管理事務所番号';
COMMENT ON COLUMN observatorydam_info.officename IS '管理事務所名';
COMMENT ON COLUMN observatorydam_info.jurisfacilitycode IS '所管機関コード';
COMMENT ON COLUMN observatorydam_info.jurisfacilityname IS '所管機関名';
COMMENT ON COLUMN observatorydam_info.obsrvtncode IS '観測所番号';
COMMENT ON COLUMN observatorydam_info.name IS '観測所名';
COMMENT ON COLUMN observatorydam_info.readname IS 'ふりがな';
COMMENT ON COLUMN observatorydam_info.latitude IS '緯度';
COMMENT ON COLUMN observatorydam_info.longitude IS '経度';
COMMENT ON COLUMN observatorydam_info.prefname IS '都道府県';
COMMENT ON COLUMN observatorydam_info.address IS '所在地';
COMMENT ON COLUMN observatorydam_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN observatorydam_info.featureid IS 'フィーチャＩＤ';
COMMENT ON COLUMN observatorydam_info.url IS 'URL';
COMMENT ON COLUMN observatorydam_info.iframe IS 'true:iframe, false:popup';
COMMENT ON COLUMN observatorydam_info.width IS '幅';
COMMENT ON COLUMN observatorydam_info.height IS '高さ';
COMMENT ON COLUMN observatorydam_info.note IS '備考';

---判定---

create table judgeman_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	disasterid int,
	name text,
	"interval" int default 60,
	delay int default 0,
	judgeorder int,
	note text,
	valid boolean default false
);

COMMENT ON TABLE judgeman_info IS 'データ判定管理情報';
COMMENT ON COLUMN judgeman_info.id IS 'ID';
COMMENT ON COLUMN judgeman_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN judgeman_info.disasterid IS '災害種別ID';
COMMENT ON COLUMN judgeman_info.name IS '名称';
COMMENT ON COLUMN judgeman_info."interval" IS '判定インターバル（分）';
COMMENT ON COLUMN judgeman_info.delay IS '遅延（分）';
COMMENT ON COLUMN judgeman_info.judgeorder IS '判定順';
COMMENT ON COLUMN judgeman_info.note IS '備考';
COMMENT ON COLUMN judgeman_info.valid IS '有効・無効';

create table judge_info (
	id bigserial not null primary key, 
	judgemaninfoid bigint,
	telemeterdatacode bigint,
	itemcode int,
	name text,
	val text,
	judgeformulaid int,
	note text,
	valid boolean default true
);

COMMENT ON TABLE judge_info IS 'データ判定情報';
COMMENT ON COLUMN judge_info.id IS 'ID';
COMMENT ON COLUMN judge_info.judgemaninfoid IS 'データ判定管理ID';
COMMENT ON COLUMN judge_info.telemeterdatacode IS '観測所コード';
COMMENT ON COLUMN judge_info.itemcode IS 'データ項目コード';
COMMENT ON COLUMN judge_info.name IS '名称';
COMMENT ON COLUMN judge_info.val IS '値';
COMMENT ON COLUMN judge_info.judgeformulaid IS '式ID';
COMMENT ON COLUMN judge_info.note IS '備考';
COMMENT ON COLUMN judge_info.valid IS '有効・無効';

create table judgeresult_data (
	id bigserial not null primary key, 
	judgemaninfoid bigint,
	judgetime timestamp without time zone,
	val text,
	canceltime timestamp without time zone,
	bcancel boolean default false
);

COMMENT ON TABLE judgeresult_data IS '判定結果データ';
COMMENT ON COLUMN judgeresult_data.id IS 'ID';
COMMENT ON COLUMN judgeresult_data.judgemaninfoid IS 'データ判定管理ID';
COMMENT ON COLUMN judgeresult_data.judgetime IS '判定時刻';
COMMENT ON COLUMN judgeresult_data.val IS '値';
COMMENT ON COLUMN judgeresult_data.canceltime IS '解除時刻';
COMMENT ON COLUMN judgeresult_data.bcancel IS '解除済みフラグ';

create table judgealarm_info (
	id bigserial not null primary key, 
	judgemaninfoid bigint,
	alarmmessageinfoid bigint,
	note text,
	valid boolean default true
);

COMMENT ON TABLE judgealarm_info IS 'データ判定アラーム情報';
COMMENT ON COLUMN judgealarm_info.id IS 'ID';
COMMENT ON COLUMN judgealarm_info.judgemaninfoid IS 'データ判定管理ID';
COMMENT ON COLUMN judgealarm_info.alarmmessageinfoid IS 'アラーム情報ID';
COMMENT ON COLUMN judgealarm_info.note IS '備考';
COMMENT ON COLUMN judgealarm_info.valid IS '有効・無効';

create table judgenotice_info (
	id bigserial not null primary key, 
	judgealarminfoid bigint,
	noticegroupinfoid bigint,
	note text,
	valid boolean default true
);

COMMENT ON TABLE judgenotice_info IS 'データ判定通知情報';
COMMENT ON COLUMN judgenotice_info.id IS 'ID';
COMMENT ON COLUMN judgenotice_info.judgealarminfoid IS 'データ判定アラームID';
COMMENT ON COLUMN judgenotice_info.noticegroupinfoid IS '通知グループID';
COMMENT ON COLUMN judgenotice_info.note IS '備考';
COMMENT ON COLUMN judgenotice_info.valid IS '有効・無効';

create table judgeresultstyle_info (
	id bigserial not null primary key, 
	judgemaninfoid bigint,
	style text,
	note text,
	valid boolean default true
);

COMMENT ON TABLE judgeresultstyle_info IS 'データ判定結果スタイル情報';
COMMENT ON COLUMN judgeresultstyle_info.id IS 'ID';
COMMENT ON COLUMN judgeresultstyle_info.judgemaninfoid IS 'データ判定管理ID';
COMMENT ON COLUMN judgeresultstyle_info.style IS 'スタイル文字列';
COMMENT ON COLUMN judgeresultstyle_info.note IS '備考';
COMMENT ON COLUMN judgeresultstyle_info.valid IS '有効・無効';

create table judgeresultstyle_data (
	id bigserial not null primary key, 
	judageresultstyleinfoid bigint,
	telemeterdataid bigint
);

COMMENT ON TABLE judgeresultstyle_data IS 'データ判定結果スタイルデータ';
COMMENT ON COLUMN judgeresultstyle_data.id IS 'ID';
COMMENT ON COLUMN judgeresultstyle_data.judageresultstyleinfoid IS 'データ判定結果スタイルID';
COMMENT ON COLUMN judgeresultstyle_data.telemeterdataid IS '観測値ID';


create table judgeformula_master (
	id serial not null primary key, 
	formula text,
	name text,
	disporder int
);
create index judgeresultstyle_data_telemeterdataid_idx
	on judgeresultstyle_data (telemeterdataid);

COMMENT ON TABLE judgeformula_master IS 'データ判定式マスタ';
COMMENT ON COLUMN judgeformula_master.id IS 'ID';
COMMENT ON COLUMN judgeformula_master.formula IS '式';
COMMENT ON COLUMN judgeformula_master.name IS '名称';
COMMENT ON COLUMN judgeformula_master.disporder IS '表示順';
