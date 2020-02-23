/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
CREATE TABLE publiccommons_report_data_last_antidisaster
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 公共情報コモンズ発信データID
  hatureidatetime timestamp with time zone, -- 設置・解散日時
  issueorlift text, -- 設置or解散
  antidisasterkbn text, -- 本部種別
  name text, -- 本部名称
  CONSTRAINT publiccommons_report_data_last_system_information_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_antidisaster
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_antidisaster
  IS '公共情報コモンズ災害対策本部設置状況最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_antidisaster.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_antidisaster.pcommonsreportdataid IS '公共情報コモンズ発信データID';
COMMENT ON COLUMN publiccommons_report_data_last_antidisaster.hatureidatetime IS '設置・解散日時';
COMMENT ON COLUMN publiccommons_report_data_last_antidisaster.issueorlift IS '設置or解散';
COMMENT ON COLUMN publiccommons_report_data_last_antidisaster.antidisasterkbn IS '本部種別';
COMMENT ON COLUMN publiccommons_report_data_last_antidisaster.name IS '本部名称';
