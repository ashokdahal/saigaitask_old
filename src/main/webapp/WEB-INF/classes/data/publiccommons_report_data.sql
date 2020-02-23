/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

CREATE TABLE publiccommons_report_data
(
  id bigserial NOT NULL, -- ID
  localgovinfoid bigint, -- 自治体ID
  trackdataid bigint, -- 記録データID
  category text, -- 情報種別
  distribution_id text, -- メッセージID
  document_id text, -- ドキュメントID
  document_id_serial integer, -- ドキュメントID連番
  document_revision integer, -- ドキュメント版番号
  filename text, -- ファイル名
  status text, -- ステータス
  createtime timestamp without time zone, -- 作成日時
  reporttime timestamp without time zone, -- 発表日時
  startsendtime timestamp without time zone, -- 発信開始日時
  sendtime timestamp without time zone, -- 発信成功日時
  success boolean, -- 発信成功フラグ
  registtime timestamp without time zone, -- 登録日時
  distribution_type text, -- 更新種別
  valid_date_time text, -- 希望公開終了日時
  CONSTRAINT publiccommons_report_data_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data
  IS '公共情報コモンズ発信データ';
COMMENT ON COLUMN publiccommons_report_data.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_data.trackdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data.category IS '情報種別';
COMMENT ON COLUMN publiccommons_report_data.distribution_id IS 'メッセージID';
COMMENT ON COLUMN publiccommons_report_data.document_id IS 'ドキュメントID';
COMMENT ON COLUMN publiccommons_report_data.document_id_serial IS 'ドキュメントID連番';
COMMENT ON COLUMN publiccommons_report_data.document_revision IS 'ドキュメント版番号';
COMMENT ON COLUMN publiccommons_report_data.filename IS 'ファイル名';
COMMENT ON COLUMN publiccommons_report_data.status IS 'ステータス';
COMMENT ON COLUMN publiccommons_report_data.createtime IS '作成日時';
COMMENT ON COLUMN publiccommons_report_data.reporttime IS '発表日時';
COMMENT ON COLUMN publiccommons_report_data.startsendtime IS '発信開始日時';
COMMENT ON COLUMN publiccommons_report_data.sendtime IS '発信成功日時';
COMMENT ON COLUMN publiccommons_report_data.success IS '発信成功フラグ';
COMMENT ON COLUMN publiccommons_report_data.registtime IS '登録日時';
COMMENT ON COLUMN publiccommons_report_data.distribution_type IS '更新種別';
COMMENT ON COLUMN publiccommons_report_data.valid_date_time IS '希望公開終了日時';


-- Index: publiccommons_report_data_localgovinfoid_idx

-- DROP INDEX publiccommons_report_data_localgovinfoid_idx;

CREATE INDEX publiccommons_report_data_localgovinfoid_idx
  ON publiccommons_report_data
  USING btree
  (localgovinfoid);

-- Index: publiccommons_report_data_trackdataid_idx

-- DROP INDEX publiccommons_report_data_trackdataid_idx;

CREATE INDEX publiccommons_report_data_trackdataid_idx
  ON publiccommons_report_data
  USING btree
  (trackdataid);