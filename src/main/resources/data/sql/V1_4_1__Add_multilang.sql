/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
CREATE TABLE multilang_info(
  id         bigserial   not null,
  code       text        not null,
  name       text        not null,
  disporder  int         not null,
  updatetime timestamp,
  CONSTRAINT multilang_info_pkey PRIMARY KEY (id)
);
ALTER TABLE multilang_info OWNER TO postgres;
COMMENT ON TABLE  multilang_info IS '言語情報';
COMMENT ON COLUMN multilang_info.id         IS 'ID';
COMMENT ON COLUMN multilang_info.code       IS '言語コード';
COMMENT ON COLUMN multilang_info.name       IS '言語名称';
COMMENT ON COLUMN multilang_info.disporder  IS '表示順';
COMMENT ON COLUMN multilang_info.updatetime IS '最終更新日時';

--multilangmes_infoの作成は別SQLファイルにて行う
ALTER TABLE localgov_info ADD multilanginfoid bigint;
COMMENT ON COLUMN localgov_info.multilanginfoid IS '言語情報ID';

--以下追記
--multilang_infoにレコードを挿入
INSERT INTO multilang_info (id,code,name,disporder,updatetime) VALUES (1,'en','English',1,'2015-09-28 11:11:11.111111'),
                                                                      (2,'ja','日本語',2,'2015-09-28 11:11:11.111111');

--スキーマの作成
CREATE SCHEMA en;
CREATE SCHEMA ja;

--enスキーマにalarmtype_masterテーブルを追加
CREATE TABLE en.alarmtype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT alarmtype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.alarmtype_master
  OWNER TO postgres;
COMMENT ON TABLE en.alarmtype_master
  IS 'アラームタイプマスタ';
COMMENT ON COLUMN en.alarmtype_master.id IS 'ID';
COMMENT ON COLUMN en.alarmtype_master.name IS '名称';
COMMENT ON COLUMN en.alarmtype_master.disporder IS '表示順';

INSERT INTO en.alarmtype_master (id,name,disporder) VALUES (1,'System',1),
                                                           (2,'Observed value',2);

--enスキーマにdisaster_masterテーブルを追加
CREATE TABLE en.disaster_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT disaster_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.disaster_master
  OWNER TO postgres;
COMMENT ON TABLE en.disaster_master
  IS '災害種別マスタ';
COMMENT ON COLUMN en.disaster_master.id IS 'ID';
COMMENT ON COLUMN en.disaster_master.name IS '名称';
COMMENT ON COLUMN en.disaster_master.disporder IS '表示順';

INSERT INTO en.disaster_master (id,name,disporder) VALUES (0,'DEFAULT',0),
                                                          (1,'FLOOD',1),
                                                          (2,'QUAKE･TSUNAMI',2),
                                                          (3,'VOCANO',3),
                                                          (6,'QUAKE',4);

--enスキーマにfacebook_masterテーブルを追加
CREATE TABLE en.facebook_master
(
  id serial NOT NULL,
  appid text,
  appsecret text,
  CONSTRAINT facebook_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.facebook_master
  OWNER TO postgres;

--facebook_masterは現時点で空なのでINSERTの必要なし

--enスキーマにjudgeformula_masterテーブルを追加
CREATE TABLE en.judgeformula_master
(
  id serial NOT NULL, -- ID
  formula text, -- 式
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT judgeformula_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.judgeformula_master
  OWNER TO postgres;
COMMENT ON TABLE en.judgeformula_master
  IS 'データ判定式マスタ';
COMMENT ON COLUMN en.judgeformula_master.id IS 'ID';
COMMENT ON COLUMN en.judgeformula_master.formula IS '式';
COMMENT ON COLUMN en.judgeformula_master.name IS '名称';
COMMENT ON COLUMN en.judgeformula_master.disporder IS '表示順';

INSERT INTO en.judgeformula_master (id,formula,name,disporder) VALUES (1,'r1 <= v1','or more',1),
                                                                      (2,'r1 >= v1','or less',2),
                                                                      (3,'r1 < v1','more than',3),
                                                                      (4,'r1 > v1','less than',4),
                                                                      (5,'r1 == v1','equal',5);

--enスキーマにlocalgovtype_masterテーブルを追加
CREATE TABLE en.localgovtype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT localgovtype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.localgovtype_master
  OWNER TO postgres;
COMMENT ON TABLE en.localgovtype_master
  IS '地方自治体種別';
COMMENT ON COLUMN en.localgovtype_master.id IS 'ID';
COMMENT ON COLUMN en.localgovtype_master.name IS '名称';
COMMENT ON COLUMN en.localgovtype_master.disporder IS '表示順';

INSERT INTO en.localgovtype_master (id,name,disporder) VALUES (1,'Prefecture',1),
                                                              (2,'City',2),
                                                              (3,'Other',3);


--enスキーマにmenutype_masterテーブルを追加
CREATE TABLE en.menutype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT menutype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.menutype_master
  OWNER TO postgres;
COMMENT ON TABLE en.menutype_master
  IS 'ページタイプマスタ';
COMMENT ON COLUMN en.menutype_master.id IS 'ID';
COMMENT ON COLUMN en.menutype_master.name IS '名称';
COMMENT ON COLUMN en.menutype_master.disporder IS '表示順';


INSERT INTO en.menutype_master (id,name,disporder) VALUES (1,'List (with map)',1),
                                                          (2,'List (without map)',2),
                                                          (3,'Map (with list)',3),
                                                          (4,'Map (without list)',4),
                                                          (5,'Request',5),
                                                          (6,'No. 4 format',6),
                                                          (7,'L-Alert (emergency e-mail)',7),
                                                          (8,'L-Alert (media): evacuation advisory',8),
                                                          (9,'SNS notifications',9),
                                                          (10,'Twitter',10),
                                                          (11,'Assemble staff',11),
                                                          (12,'L-Alert (media): shelter',12),
                                                          (13,'Response history',13),
                                                          (14,'Monitoring/observation',14),
                                                          (15,'Disaster summary',15),
                                                          (16,'Disseminate by e-Com GW',16),
                                                          (17,'Notice history',17),
                                                          (18,'Summary of No.4 format',18),
                                                          (19,'Summary of system',19),
                                                          (20,'L-Alert (media): announcement',20),
                                                          (21,'L-Alert (media): event info',21),
                                                          (22,'L-Alert (media): damage info',22),
                                                          (23,'[Unavailable] L-Alert (media): public protection info',23),
                                                          (24,'Summary table of disaster',24);



--enスキーマにmeteotype_masterテーブルを追加
CREATE TABLE en.meteotype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  type text, -- 取得種別名
  note text, -- 備考
  disporder integer, -- 表示順
  CONSTRAINT meteotype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.meteotype_master
  OWNER TO postgres;
COMMENT ON TABLE en.meteotype_master
  IS '気象情報等取得種別マスタ';
COMMENT ON COLUMN en.meteotype_master.id IS 'ID';
COMMENT ON COLUMN en.meteotype_master.name IS '名称';
COMMENT ON COLUMN en.meteotype_master.type IS '取得種別名';
COMMENT ON COLUMN en.meteotype_master.note IS '備考';
COMMENT ON COLUMN en.meteotype_master.disporder IS '表示順';

INSERT INTO en.meteotype_master (id,name,type,note,disporder) VALUES (1,'Weather emergency warning, warning, advisory','kishoukeihouChuihou','',1),
                                                                     (2,'Seismic intensity bulletin','shindoSokuhou','',2),
                                                                     (4,'Epicenter, seismic intensity info','shingenShindoJouhou','',3),
                                                                     (3,'Tsunami warning, advisory, forecast','tsunamikeihouChuihou','',4),
                                                                     (5,'Designated river flood forecast','shiteiKasenKouzuiYohou','',5),
                                                                     (6,'Landslide disaster warning info','dosyasaigaiKeikaiJouhou','',6),
                                                                     (7,'Record-breaking intense rainfall info','kirokutekiTanjikanOoameJouhou','',7),
                                                                     (8,'Tornado warning info','tastumakiChuuiJouhou','',8),
                                                                     (9,'Eruption warning, forecast','funkakeihouYohou','',9),
                                                                     (10,'Earthquake Early Warning','kinkyuJishinSokuhou','',10);

--enスキーマにnoticetemplatetype_masterテーブルを追加
CREATE TABLE en.noticetemplatetype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT noticetemplatetype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.noticetemplatetype_master
  OWNER TO postgres;
COMMENT ON TABLE en.noticetemplatetype_master
  IS '通知テンプレート種別マスタ';
COMMENT ON COLUMN en.noticetemplatetype_master.id IS 'ID';
COMMENT ON COLUMN en.noticetemplatetype_master.name IS '名称';
COMMENT ON COLUMN en.noticetemplatetype_master.disporder IS '表示順';

INSERT INTO en.noticetemplatetype_master (id,name,disporder) VALUES (1,'Evacuation advisory, instructions',1),
                                                                    (2,'Evacuation before tsunami arrival',2),
                                                                    (3,'Life-saving',3),
                                                                    (4,'Establish HQ',4),
                                                                    (5,'Shelter',5),
                                                                    (6,'Road restriction/clearing',6),
                                                                    (7,'Other requests',7),
                                                                    (8,'Open shelter',8),
                                                                    (9,'Manage shelter',9),
                                                                    (10,'Mountain entry restrictions',10),
                                                                    (11,'Road restrictions/re-opening',11);


--enスキーマにnoticetype_masterテーブルを追加
CREATE TABLE en.noticetype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT noticetype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.noticetype_master
  OWNER TO postgres;
COMMENT ON TABLE en.noticetype_master
  IS '通知種別マスタ';
COMMENT ON COLUMN en.noticetype_master.id IS 'ID';
COMMENT ON COLUMN en.noticetype_master.name IS '名称';
COMMENT ON COLUMN en.noticetype_master.disporder IS '表示順';

INSERT INTO en.noticetype_master (id,name,disporder) VALUES (1,'E-mail',1),
                                                            (2,'Alarms',2),
                                                            (3,'Captions',3),
                                                            (4,'Send L-Alert (emergency e-mail)',4),
                                                            (5,'Send L-Alert (media)',5),
                                                            (6,'Facebook',6),
                                                            (7,'Twitter',7),
                                                            (8,'e-Com GW',8);
--enスキーマにobserv_masterテーブルを追加
CREATE TABLE en.observ_master
(
  id serial NOT NULL, -- ID
  name text, -- 監視観測名
  disporder integer, -- 表示順
  CONSTRAINT observ_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.observ_master
  OWNER TO postgres;
COMMENT ON TABLE en.observ_master
  IS 'テレメータデータ';
COMMENT ON COLUMN en.observ_master.id IS 'ID';
COMMENT ON COLUMN en.observ_master.name IS '監視観測名';
COMMENT ON COLUMN en.observ_master.disporder IS '表示順';

INSERT INTO en.observ_master (id,name,disporder) VALUES (1,'Rainfall',1),
                                                        (2,'Water level',2),
                                                        (3,'Dam data',3);

--enスキーマにpagebutton_masterテーブルを追加
CREATE TABLE en.pagebutton_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  href text, -- リンク
  disporder integer, -- 表示順
  CONSTRAINT pagebutton_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.pagebutton_master
  OWNER TO postgres;
COMMENT ON TABLE en.pagebutton_master
  IS 'ページボタンマスタ';
COMMENT ON COLUMN en.pagebutton_master.id IS 'ID';
COMMENT ON COLUMN en.pagebutton_master.name IS '名称';
COMMENT ON COLUMN en.pagebutton_master.href IS 'リンク';
COMMENT ON COLUMN en.pagebutton_master.disporder IS '表示順';

INSERT INTO en.pagebutton_master (id,name,href,disporder) VALUES (1,'&nbsp;&nbsp;','',1),
                                                              (2,'Print','javascript:pdfDialog()',2),
                                                              (3,'Send info','javascript:sendData() ',3),
                                                              (4,'Update','javascript:saveData()',4),
                                                              (5,'Register','javascript:addLine()',5),
                                                              (6,'Send','javascript:sendMessage()',6),
                                                              (7,'Road damage status','',7),
                                                              (8,'Airport damage status','',8),
                                                              (9,'Seaway damage status','',9),
                                                              (10,'Bulk update','javascript:slimerDialog()',10),
                                                              (11,'CSV','javascript:csvDialog()',11),
                                                              (12,'Send (unavailable)','sendMessage()',12),
                                                              (13,'Past summary','javascript:showHistory()',13);

--enスキーマにsafetystate_masterテーブルを追加
CREATE TABLE en.safetystate_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT safetystate_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.safetystate_master
  OWNER TO postgres;
COMMENT ON TABLE en.safetystate_master
  IS '安否確認状況マスタ';
COMMENT ON COLUMN en.safetystate_master.id IS 'ID';
COMMENT ON COLUMN en.safetystate_master.name IS '名称';
COMMENT ON COLUMN en.safetystate_master.disporder IS '表示順';

INSERT INTO en.safetystate_master (id,name,disporder) VALUES (1,'Unsent',1),
                                                             (2,'E-mail sent',2),
                                                             (3,'New e-mail received',3),
                                                             (4,'Assembling',4),
                                                             (5,'Assembly completed',5),
                                                             (6,'Assembly not possible',6);

--enスキーマにstation_masterテーブルを追加
CREATE TABLE en.station_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT station_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.station_master
  OWNER TO postgres;
COMMENT ON TABLE en.station_master
  IS '体制マスタ';
COMMENT ON COLUMN en.station_master.id IS 'ID';
COMMENT ON COLUMN en.station_master.name IS '名称';
COMMENT ON COLUMN en.station_master.disporder IS '表示順';

INSERT INTO en.station_master (id,name,disporder) VALUES (1,'Prevention HQ',1),
                                                         (2,'Response HQ',2),
                                                         (0,'Cancel',3);

/*tablecolumn_master  日本語部分がないので不要
--tablecolumn_master
CREATE TABLE en.tablecolumn_master
(
  id serial NOT NULL, -- ID
  tablename text, -- テーブル名
  columnname text, -- 項目名
  nullable boolean, -- 空欄可
  CONSTRAINT tablecolumn_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.tablecolumn_master
  OWNER TO postgres;
COMMENT ON TABLE en.tablecolumn_master
  IS 'テーブル項目マスタ';
COMMENT ON COLUMN en.tablecolumn_master.id IS 'ID';
COMMENT ON COLUMN en.tablecolumn_master.tablename IS 'テーブル名';
COMMENT ON COLUMN en.tablecolumn_master.columnname IS '項目名';
COMMENT ON COLUMN en.tablecolumn_master.nullable IS '空欄可';
*/

--enスキーマにteloptype_masterテーブルを追加
CREATE TABLE en.teloptype_master
(
  id serial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT teloptype_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.teloptype_master
  OWNER TO postgres;
COMMENT ON TABLE en.teloptype_master
  IS 'テロップ種別マスタ';
COMMENT ON COLUMN en.teloptype_master.id IS 'ID';
COMMENT ON COLUMN en.teloptype_master.name IS '名称';
COMMENT ON COLUMN en.teloptype_master.disporder IS '表示順';

INSERT INTO en.teloptype_master (id,name,disporder) VALUES (1,'Weather emergency warning, warning, advisory',1),
                                                           (2,'Seismic intensity bulletin',2),
                                                           (4,'Epicenter, seismic intensity info',3),
                                                           (3,'Tsunami warning, advisory, forecast',4),
                                                           (5,'Designated river flood forecast',5),
                                                           (6,'Landslide disaster warning info',6),
                                                           (7,'Record-breaking intense rainfall info',7),
                                                           (8,'Tornado warning info',8),
                                                           (9,'Eruption warning, forecast',9);

--enスキーマにtimelinemenue_masterテーブルを追加
CREATE TABLE en.timelinemenue_master
(
  id bigserial NOT NULL, -- ID
  name text, -- 名称
  disporder integer, -- 表示順
  CONSTRAINT timelinemenu_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.timelinemenue_master
  OWNER TO postgres;
COMMENT ON TABLE en.timelinemenue_master
  IS 'タイムラインメニュー';
COMMENT ON COLUMN en.timelinemenue_master.id IS 'ID';
COMMENT ON COLUMN en.timelinemenue_master.name IS '名称';
COMMENT ON COLUMN en.timelinemenue_master.disporder IS '表示順';

INSERT INTO en.timelinemenue_master (id,name,disporder) VALUES (1,'Evacuation advisory/order',1),
                                                               (2,'Life-saving',2),
                                                               (3,'System',3),
                                                               (4,'Open shelter',4),
                                                               (5,'Shelter status',5),
                                                               (6,'Road restrictions/clearing',6);

--enスキーマにtwitter_masterテーブルを追加
CREATE TABLE en.twitter_master
(
  id serial NOT NULL, -- ID
  consumerkey text, -- Consumer key
  consumersecret text, -- Consumer secret
  CONSTRAINT twitter_master_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE en.twitter_master
  OWNER TO postgres;
COMMENT ON TABLE en.twitter_master
  IS 'Twitterマスタ';
COMMENT ON COLUMN en.twitter_master.id IS 'ID';
COMMENT ON COLUMN en.twitter_master.consumerkey IS 'Consumer key';
COMMENT ON COLUMN en.twitter_master.consumersecret IS 'Consumer secret';

--twitter_masterは現時点で空なのでINSERTの必要なし
