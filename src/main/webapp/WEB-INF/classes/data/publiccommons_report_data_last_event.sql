/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

CREATE TABLE publiccommons_report_data_last_event
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
  personresponsible text, -- 担当者名
  phone text, -- 電話番号
  fax text, -- FAX
  email text, -- メールアドレス
  officename text, -- 部署名
  officenamekana text, -- 部署名(カナ)
  officelocationarea text, -- 部署住所
  eventfrom text, -- 開催開始日時
  eventto text, -- 開催終了日時
  eventfee text, -- 参加料金
  filecaption text, -- ファイルタイトル
  emailtitle text, -- メールタイトル
  content text, -- 送信本文
  CONSTRAINT publiccommons_report_data_last_event_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_event
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_event
  IS '公共情報コモンズイベント情報最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_event.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_event.pcommonsreportdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data_last_event.chikuname IS '地区名';
COMMENT ON COLUMN publiccommons_report_data_last_event.area IS '場所';
COMMENT ON COLUMN publiccommons_report_data_last_event.title IS 'タイトル';
COMMENT ON COLUMN publiccommons_report_data_last_event.text IS '本文';
COMMENT ON COLUMN publiccommons_report_data_last_event.notificationuri IS '告知URI';
COMMENT ON COLUMN publiccommons_report_data_last_event.fileuri IS 'ファイルURI';
COMMENT ON COLUMN publiccommons_report_data_last_event.mediatype IS 'メディアタイプ';
COMMENT ON COLUMN publiccommons_report_data_last_event.documentid IS 'ドキュメントID';
COMMENT ON COLUMN publiccommons_report_data_last_event.division IS '分類';
COMMENT ON COLUMN publiccommons_report_data_last_event.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_data_last_event.documentrevision IS '版数';
COMMENT ON COLUMN publiccommons_report_data_last_event.disasterinformationtype IS '情報識別区分';
COMMENT ON COLUMN publiccommons_report_data_last_event.validdatetime IS '希望公開終了日時';
COMMENT ON COLUMN publiccommons_report_data_last_event.distributiontype IS '更新種別';
COMMENT ON COLUMN publiccommons_report_data_last_event.personresponsible IS '担当者名';
COMMENT ON COLUMN publiccommons_report_data_last_event.phone IS '電話番号';
COMMENT ON COLUMN publiccommons_report_data_last_event.fax IS 'FAX';
COMMENT ON COLUMN publiccommons_report_data_last_event.email IS 'メールアドレス';
COMMENT ON COLUMN publiccommons_report_data_last_event.officename IS '部署名';
COMMENT ON COLUMN publiccommons_report_data_last_event.officenamekana IS '部署名(カナ)';
COMMENT ON COLUMN publiccommons_report_data_last_event.officelocationarea IS '部署住所';
COMMENT ON COLUMN publiccommons_report_data_last_event.eventfrom IS '開催開始日時';
COMMENT ON COLUMN publiccommons_report_data_last_event.eventto IS '開催終了日時';
COMMENT ON COLUMN publiccommons_report_data_last_event.eventfee IS '参加料金';
COMMENT ON COLUMN publiccommons_report_data_last_event.filecaption IS 'ファイルタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_event.emailtitle IS 'メールタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_event.content IS '送信本文';
