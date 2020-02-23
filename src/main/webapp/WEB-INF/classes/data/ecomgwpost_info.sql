/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- Table: ecomgwpost_info

-- DROP TABLE ecomgwpost_info;

CREATE TABLE ecomgwpost_info
(
  id bigserial NOT NULL, -- ID
  localgovinfoid bigint, -- 自治体ID
  name text, -- セット名
  groupid text, -- 投稿先グループID。APIのgroupidパラメータに該当。
  partsid text, -- パーツ共通ID。APIのpartsidパラメータに該当。
  blockid text, -- パーツ個別ID。APIのblockidパラメータに該当。
  posturl text, -- 投稿先URL（APIのURL）。
  note text, -- 備考
  disporder integer, -- 表示順
  valid boolean, -- 有効・無効
  CONSTRAINT ecomgwpost_info_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ecomgwpost_info
  OWNER TO postgres;
COMMENT ON COLUMN ecomgwpost_info.id IS 'ID';
COMMENT ON COLUMN ecomgwpost_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN ecomgwpost_info.name IS 'セット名';
COMMENT ON COLUMN ecomgwpost_info.groupid IS '投稿先グループID。APIのgroupidパラメータに該当。';
COMMENT ON COLUMN ecomgwpost_info.partsid IS 'パーツ共通ID。APIのpartsidパラメータに該当。';
COMMENT ON COLUMN ecomgwpost_info.blockid IS 'パーツ個別ID。APIのblockidパラメータに該当。
';
COMMENT ON COLUMN ecomgwpost_info.posturl IS '投稿先URL（APIのURL）。';
COMMENT ON COLUMN ecomgwpost_info.note IS '備考';
COMMENT ON COLUMN ecomgwpost_info.disporder IS '表示順';
COMMENT ON COLUMN ecomgwpost_info.valid IS '有効・無効';

