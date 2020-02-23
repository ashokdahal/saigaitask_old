/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */


--テロップ履歴
CREATE TABLE alertcontent_data (
	id bigserial not null primary key,
	trackdataid bigint,
	localgovinfoid bigint,
	teloptypeid int,
	receivetime timestamp,
	title text,
	content text,
	filepath text
);

CREATE INDEX alertcontent_data_trackdataid_idx
	ON alertcontent_data (trackdataid);

CREATE INDEX alertcontent_data_localgovinfoid_idx
	ON alertcontent_data (localgovinfoid);

CREATE INDEX alertcontent_data_receivetime_idx
	ON alertcontent_data (receivetime);

COMMENT ON TABLE alertcontent_data IS '気象情報等アラートデータ';
COMMENT ON COLUMN alertcontent_data.id IS 'ID';
COMMENT ON COLUMN alertcontent_data.trackdataid IS '記録ID';
COMMENT ON COLUMN alertcontent_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN alertcontent_data.teloptypeid IS 'テロップ種別';
COMMENT ON COLUMN alertcontent_data.receivetime IS '受信時刻';
COMMENT ON COLUMN alertcontent_data.title IS 'タイトル';
COMMENT ON COLUMN alertcontent_data.content IS '内容';
COMMENT ON COLUMN alertcontent_data.filepath IS 'ファイルパス';

--JAlert

create table jalertserver_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	serverurl text,
	userid text,
	password text,
	note text
);

COMMENT ON TABLE jalertserver_info IS 'JAlertサーバ情報';
COMMENT ON COLUMN jalertserver_info.id IS 'ID';
COMMENT ON COLUMN jalertserver_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN jalertserver_info.serverurl IS 'サーバURL';
COMMENT ON COLUMN jalertserver_info.userid IS 'ユーザ名';
COMMENT ON COLUMN jalertserver_info.password IS 'パスワード';
COMMENT ON COLUMN jalertserver_info.note IS '備考';


create table jalerttype_master (
	id serial not null primary key, 
	name text,
	disporder int
);

COMMENT ON TABLE jalerttype_master IS 'JAlert種別マスタ';
COMMENT ON COLUMN jalerttype_master.id IS 'ID';
COMMENT ON COLUMN jalerttype_master.name IS '名称';
COMMENT ON COLUMN jalerttype_master.disporder IS '表示順';


insert into jalerttype_master values(10, '緊急地震速報', 1);
insert into jalerttype_master values(3, '津波情報', 2);
insert into jalerttype_master values(9, '火山情報', 3);
insert into jalerttype_master values(2, '地震情報', 4);
insert into jalerttype_master values(1, '気象情報', 5);
insert into jalerttype_master values(11, '国民保護情報', 6);
insert into jalerttype_master values(12, '緊急連絡', 7);
select setval('jalerttype_master_id_seq', 12);

create table jalertrequest_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	jalerttypeid int,
	meteoareaid text,
	meteoareaid2 text,
	alarm boolean default true,
	view boolean default true,
	note text,
	valid boolean default true
);

COMMENT ON TABLE jalertrequest_info IS 'JAlert情報取得情報';
COMMENT ON COLUMN jalertrequest_info.id IS 'ID';
COMMENT ON COLUMN jalertrequest_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN jalertrequest_info.jalerttypeid IS 'JAlert情報種別';
COMMENT ON COLUMN jalertrequest_info.meteoareaid IS 'エリアID';
COMMENT ON COLUMN jalertrequest_info.meteoareaid2 IS 'エリアID予備';
COMMENT ON COLUMN jalertrequest_info.alarm IS 'アラームフラグ';
COMMENT ON COLUMN jalertrequest_info.view IS '表示フラグ';
COMMENT ON COLUMN jalertrequest_info.note IS '備考';
COMMENT ON COLUMN jalertrequest_info.valid IS '有効・無効';

create table jalertreceivefile_data (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	jalertrequestinfoid bigint,
	jalerttypeid int,
	receivetime timestamp,
	orgtextfilename text,
	orgdatafilename text,
	textfilepath text,
	filepath text
);
create index jalertreceivefile_data_orgtextfilename_idx
	on jalertreceivefile_data (orgtextfilename);

COMMENT ON TABLE jalertreceivefile_data IS 'JAlert受信データ情報';
COMMENT ON COLUMN jalertreceivefile_data.id IS 'ID';
COMMENT ON COLUMN jalertreceivefile_data.jalertrequestinfoid IS 'JAlert情報取得情報ID';
COMMENT ON COLUMN jalertreceivefile_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN jalertreceivefile_data.jalerttypeid IS 'ファイル種別';
COMMENT ON COLUMN jalertreceivefile_data.receivetime IS '受信日時';
COMMENT ON COLUMN jalertreceivefile_data.orgtextfilename IS 'テキストファイル名';
COMMENT ON COLUMN jalertreceivefile_data.orgdatafilename IS 'データファイル名';
COMMENT ON COLUMN jalertreceivefile_data.textfilepath IS 'テキストファイルパス';
COMMENT ON COLUMN jalertreceivefile_data.filepath IS 'ファイルパス';

create table jalerttrigger_info (
	id bigserial not null primary key, 
	jalertrequestinfoid bigint,
	trigger text,
	startup boolean default false,
	noticegroupinfoid bigint,
	stationclassinfoid bigint,
	assemblemail boolean default true,
	issuetablemasterinfoid bigint,
	issueattrid text,
	issuetext text,
	publiccommons boolean default true,
	publiccommonsmail boolean default false,
	sns boolean default true,
	addlayer boolean default false,
	note text,
	valid boolean default true
);

COMMENT ON TABLE jalerttrigger_info IS 'JAlert情報トリガー情報';
COMMENT ON COLUMN jalerttrigger_info.id IS 'ID';
COMMENT ON COLUMN jalerttrigger_info.jalertrequestinfoid IS 'JAlert情報取得ID';
COMMENT ON COLUMN jalerttrigger_info.trigger IS 'トリガー';
COMMENT ON COLUMN jalerttrigger_info.startup IS '災害モード起動フラグ';
COMMENT ON COLUMN jalerttrigger_info.noticegroupinfoid IS '通知グループID';
COMMENT ON COLUMN jalerttrigger_info.stationclassinfoid IS '体制ID';
COMMENT ON COLUMN jalerttrigger_info.assemblemail IS '職員参集メール送信フラグ';
COMMENT ON COLUMN jalerttrigger_info.issuetablemasterinfoid IS '避難勧告テーブル';
COMMENT ON COLUMN jalerttrigger_info.issueattrid IS '避難勧告属性項目';
COMMENT ON COLUMN jalerttrigger_info.issuetext IS '避難情報の文字列';
COMMENT ON COLUMN jalerttrigger_info.publiccommons IS '公共コモンズ送信フラグ';
COMMENT ON COLUMN jalerttrigger_info.publiccommonsmail IS 'エリアメール送信フラグ';
COMMENT ON COLUMN jalerttrigger_info.sns IS 'SNS送信フラグ';
COMMENT ON COLUMN jalerttrigger_info.addlayer IS 'レイヤ追加フラグ';
COMMENT ON COLUMN jalerttrigger_info.note IS '備考';
COMMENT ON COLUMN jalerttrigger_info.valid IS '有効・無効';

create table jalerttrigger_data (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	trackdataid bigint,
	jalerttriggerinfoid bigint,
	triggertime timestamp,
	startup boolean default false,
	noticegroupinfoid bigint,
	stationclassinfoid bigint,
	assemblemail boolean default false,
	issue boolean,
	issuetext text,
	publiccommons boolean default false,
	publiccommonsmail boolean default false,
	sns boolean default false
);

COMMENT ON TABLE jalerttrigger_data IS 'JAlert情報トリガーデータ';
COMMENT ON COLUMN jalerttrigger_data.id IS 'ID';
COMMENT ON COLUMN jalerttrigger_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN jalerttrigger_data.trackdataid IS '記録ID';
COMMENT ON COLUMN jalerttrigger_data.jalerttriggerinfoid IS 'トリガーID';
COMMENT ON COLUMN jalerttrigger_data.triggertime IS 'トリガー発生時刻';
COMMENT ON COLUMN jalerttrigger_data.startup IS '災害モード起動';
COMMENT ON COLUMN jalerttrigger_data.noticegroupinfoid IS 'メール通知';
COMMENT ON COLUMN jalerttrigger_data.stationclassinfoid IS '体制移行';
COMMENT ON COLUMN jalerttrigger_data.assemblemail IS '職員参集';
COMMENT ON COLUMN jalerttrigger_data.issue IS '避難勧告';
COMMENT ON COLUMN jalerttrigger_data.issuetext IS '避難情報文字列';
COMMENT ON COLUMN jalerttrigger_data.publiccommons IS '公共コモンズ送信';
COMMENT ON COLUMN jalerttrigger_data.publiccommonsmail IS 'エリアメール';
COMMENT ON COLUMN jalerttrigger_data.sns IS 'SNS';

insert into meteotype_master values(10, '緊急地震速報', 'kinkyuJishinSokuhou', null, 10);

insert into meteoxslt_info (localgovinfoid, meteotypeid, filepath) values(0, 10, 'kinkyuuJishinSokuhou.xsl');
