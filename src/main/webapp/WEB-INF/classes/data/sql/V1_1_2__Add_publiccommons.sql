/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

ALTER TABLE publiccommons_report_data ADD distribution_type text;
COMMENT ON COLUMN publiccommons_report_data.distribution_type IS '更新種別';
ALTER TABLE publiccommons_report_data ADD valid_date_time text;
COMMENT ON COLUMN publiccommons_report_data.valid_date_time IS '希望公開終了日時';

CREATE TABLE publiccommons_report_data_last_refuge
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 記録データID
  chikuname text, -- 地区名
  hatureikbn_last text, -- 最終発令状況
  hatureikbn_last_cancel text, -- 取消用最終発令状況
  people integer, -- 人数
  targethouseholds integer, -- 対象世帯数
  hatureidatetime timestamp with time zone, -- 発令日時
  CONSTRAINT publiccommons_report_data_last_evacuation_order_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_refuge
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_refuge
  IS '公共情報コモンズ避難勧告／避難指示最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.pcommonsreportdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.chikuname IS '地区名';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.hatureikbn_last IS '最終発令状況';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.hatureikbn_last_cancel IS '取消用最終発令状況';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.people IS '人数';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.targethouseholds IS '対象世帯数';
COMMENT ON COLUMN publiccommons_report_data_last_refuge.hatureidatetime IS '発令日時';


CREATE TABLE publiccommons_report_data_last_shelter
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 記録データＩＤ
  chikuname text, -- 地区名
  closetime timestamp without time zone, -- 閉鎖時間
  setuptime timestamp without time zone, -- 開設時間
  shelteraddress text,
  sheltercapacity text, -- 収容人数
  shelterfax text,
  sheltername text, -- 避難所名
  shelterphone text,
  shelterstaff text,
  shelterstatus text, -- 状態
  shelter_last_cancel text, -- 取消用前回
  CONSTRAINT publiccommons_report_data_last_shelter_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_shelter
  OWNER TO postgres;
COMMENT ON COLUMN publiccommons_report_data_last_shelter.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.pcommonsreportdataid IS '記録データＩＤ';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.chikuname IS '地区名';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.closetime IS '閉鎖時間';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.setuptime IS '開設時間';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.sheltercapacity IS '収容人数';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.sheltername IS '避難所名';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.shelterstatus IS '状態';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.shelter_last_cancel IS '取消用前回';

INSERT INTO menutype_master VALUES (16, 'eコミグループウェア周知', 16);
INSERT INTO menutype_master VALUES (17, '通知履歴', 17);
SELECT pg_catalog.setval('menutype_master_id_seq', 17, true);
