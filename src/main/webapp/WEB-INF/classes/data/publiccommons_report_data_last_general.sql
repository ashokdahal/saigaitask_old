/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

CREATE TABLE publiccommons_report_data_last_general
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 記録データID
  chikuname text, -- 地区名
  area text, -- 場所
  title text, -- タイトル
  text text, -- 本文
  notificationuri text, -- 告知URI
  fileuri text, -- ファイルURI
  mediatype text, -- メディアタイプ
  documentid text, -- ドキュメントID
  division text, -- 分類
  localgovinfoid bigint, -- 自治体ID
  documentrevision bigint, -- 版数
  disasterinformationtype text, -- 情報識別区分
  validdatetime text, -- 希望公開終了日時
  distributiontype text, -- 更新種別
  filecaption text, -- ファイルタイトル
  emailtitle text, -- メールタイトル
  content text, -- 送信本文
  CONSTRAINT publiccommons_report_data_last_general_information_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_general
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_general
  IS '公共情報コモンズお知らせ情報最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_general.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_general.pcommonsreportdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data_last_general.chikuname IS '地区名';
COMMENT ON COLUMN publiccommons_report_data_last_general.area IS '場所';
COMMENT ON COLUMN publiccommons_report_data_last_general.title IS 'タイトル';
COMMENT ON COLUMN publiccommons_report_data_last_general.text IS '本文';
COMMENT ON COLUMN publiccommons_report_data_last_general.notificationuri IS '告知URI';
COMMENT ON COLUMN publiccommons_report_data_last_general.fileuri IS 'ファイルURI';
COMMENT ON COLUMN publiccommons_report_data_last_general.mediatype IS 'メディアタイプ';
COMMENT ON COLUMN publiccommons_report_data_last_general.documentid IS 'ドキュメントID';
COMMENT ON COLUMN publiccommons_report_data_last_general.division IS '分類';
COMMENT ON COLUMN publiccommons_report_data_last_general.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_data_last_general.documentrevision IS '版数';
COMMENT ON COLUMN publiccommons_report_data_last_general.disasterinformationtype IS '情報識別区分';
COMMENT ON COLUMN publiccommons_report_data_last_general.validdatetime IS '希望公開終了日時';
COMMENT ON COLUMN publiccommons_report_data_last_general.distributiontype IS '更新種別';
COMMENT ON COLUMN publiccommons_report_data_last_general.filecaption IS 'ファイルタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_general.emailtitle IS 'メールタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_general.content IS '送信本文';
