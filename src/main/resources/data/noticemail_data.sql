/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- Table: noticemail_data

-- DROP TABLE noticemail_data;

CREATE TABLE noticemail_data
(
  id bigserial NOT NULL, -- ID
  trackdataid bigint, -- 記録ID
  noticetypeid integer, -- 通知種別
  mailto text, -- 宛先
  title text, -- タイトル
  content text, -- 内容
  sendtime timestamp without time zone, -- 送信時間
  send boolean, -- 送信フラグ
  attachfilename text,
  trackdataname text, -- 災害名称
  CONSTRAINT noticemail_data_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE noticemail_data
  OWNER TO postgres;
COMMENT ON TABLE noticemail_data
  IS '通知データ

通知履歴画面にて災害名追加のため
trackdatanameを追加。';
COMMENT ON COLUMN noticemail_data.id IS 'ID';
COMMENT ON COLUMN noticemail_data.trackdataid IS '記録ID';
COMMENT ON COLUMN noticemail_data.noticetypeid IS '通知種別';
COMMENT ON COLUMN noticemail_data.mailto IS '宛先';
COMMENT ON COLUMN noticemail_data.title IS 'タイトル';
COMMENT ON COLUMN noticemail_data.content IS '内容';
COMMENT ON COLUMN noticemail_data.sendtime IS '送信時間';
COMMENT ON COLUMN noticemail_data.send IS '送信フラグ';
COMMENT ON COLUMN noticemail_data.trackdataname IS '災害名称';


-- Index: noticemail_data_trackdataid_idx

-- DROP INDEX noticemail_data_trackdataid_idx;

CREATE INDEX noticemail_data_trackdataid_idx
  ON noticemail_data
  USING btree
  (trackdataid );