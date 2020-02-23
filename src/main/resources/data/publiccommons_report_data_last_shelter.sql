/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- Table: publiccommons_report_data_last_shelter

-- DROP TABLE publiccommons_report_data_last_shelter;

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
COMMENT ON COLUMN publiccommons_report_data_last_shelter.chikuname IS '地区名
';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.closetime IS '閉鎖時間';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.setuptime IS '開設時間';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.sheltercapacity IS '収容人数
';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.sheltername IS '避難所名
';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.shelterstatus IS '状態';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.shelter_last_cancel IS '取消用前回';

