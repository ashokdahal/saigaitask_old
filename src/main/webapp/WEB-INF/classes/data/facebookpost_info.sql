/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- Table: facebookpost_info

-- DROP TABLE facebookpost_info;

CREATE TABLE facebookpost_info
(
  id bigserial NOT NULL, -- ID
  localgovinfoid bigint, -- 自治体ID
  name text, -- セット名
  note text, -- 備考
  disporder integer, -- 表示順
  valid boolean, -- 有効・無効
  pageid text, -- 投稿先のFacebook自アカウントID (Facebookホーム)、FacebookページIDまたはFacebookグループID
  pagetype integer, -- 1：Facebookホーム、2：Facebookページ、3：Facebookグループ
  CONSTRAINT facebookpost_info_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE facebookpost_info
  OWNER TO postgres;
COMMENT ON TABLE facebookpost_info
  IS 'Facebook投稿先情報';
COMMENT ON COLUMN facebookpost_info.id IS 'ID';
COMMENT ON COLUMN facebookpost_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN facebookpost_info.name IS 'セット名';
COMMENT ON COLUMN facebookpost_info.note IS '備考';
COMMENT ON COLUMN facebookpost_info.disporder IS '表示順';
COMMENT ON COLUMN facebookpost_info.valid IS '有効・無効';
COMMENT ON COLUMN facebookpost_info.pageid IS '投稿先のFacebook自アカウントID (Facebookホーム)、FacebookページIDまたはFacebookグループID';
COMMENT ON COLUMN facebookpost_info.pagetype IS '1：Facebookホーム、2：Facebookページ、3：Facebookグループ';

