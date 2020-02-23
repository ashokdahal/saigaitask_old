/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

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
  issueorlift text, -- 発令or解除
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
COMMENT ON COLUMN publiccommons_report_data_last_refuge.issueorlift IS '発令or解除';