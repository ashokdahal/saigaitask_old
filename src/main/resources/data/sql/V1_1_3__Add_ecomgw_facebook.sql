/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

DROP TABLE notice2_template;

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
COMMENT ON COLUMN ecomgwpost_info.blockid IS 'パーツ個別ID。APIのblockidパラメータに該当。';
COMMENT ON COLUMN ecomgwpost_info.posturl IS '投稿先URL（APIのURL）。';
COMMENT ON COLUMN ecomgwpost_info.note IS '備考';
COMMENT ON COLUMN ecomgwpost_info.disporder IS '表示順';
COMMENT ON COLUMN ecomgwpost_info.valid IS '有効・無効';


-- Table: ecomgwpostdefault_info

-- DROP TABLE ecomgwpostdefault_info;

CREATE TABLE ecomgwpostdefault_info
(
  id bigserial NOT NULL, -- ID
  noticedefaultinfoid bigint, -- 通知デフォルトID
  ecomgwpostinfoid bigint, -- eコミグループウェア投稿先情報ID
  defaulton boolean, -- デフォルトON/OFF
  CONSTRAINT ecomgwpostdefault_info_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ecomgwpostdefault_info
  OWNER TO postgres;
COMMENT ON COLUMN ecomgwpostdefault_info.id IS 'ID';
COMMENT ON COLUMN ecomgwpostdefault_info.noticedefaultinfoid IS '通知デフォルトID';
COMMENT ON COLUMN ecomgwpostdefault_info.ecomgwpostinfoid IS 'eコミグループウェア投稿先情報ID';
COMMENT ON COLUMN ecomgwpostdefault_info.defaulton IS 'デフォルトON/OFF';




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






-- Table: facebookpostdefault_info

-- DROP TABLE facebookpostdefault_info;

CREATE TABLE facebookpostdefault_info
(
  id bigserial NOT NULL, -- ID
  noticedefaultinfoid bigint, -- 通知デフォルトID
  facebookpostinfoid bigint, -- Facebook投稿先情報ID
  defaulton boolean, -- デフォルトON/OFF
  CONSTRAINT facebookpostdefault_info_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE facebookpostdefault_info
  OWNER TO postgres;
COMMENT ON COLUMN facebookpostdefault_info.id IS 'ID';
COMMENT ON COLUMN facebookpostdefault_info.noticedefaultinfoid IS '通知デフォルトID';
COMMENT ON COLUMN facebookpostdefault_info.facebookpostinfoid IS 'Facebook投稿先情報ID';
COMMENT ON COLUMN facebookpostdefault_info.defaulton IS 'デフォルトON/OFF';

-- 管理画面 広報
INSERT INTO adminmenu_info VALUES (876, '001008003000000000000', 'Facebook投稿先情報
', 3, 1, 0, '../facebookpostInfo', true);
INSERT INTO adminmenu_info VALUES (877, '001008004000000000000', 'eコミGW投稿先情報
', 3, 1, 0, '../ecomgwpostInfo', true);

insert into noticetype_master VALUES(8, 'eコミグループウェア', 8);
insert into noticetemplatetype_master values(10, '入山規制', 10);
insert into noticetemplatetype_master values(11, '道路規制・復旧', 11);