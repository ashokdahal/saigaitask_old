/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

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

