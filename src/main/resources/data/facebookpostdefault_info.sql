/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

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

