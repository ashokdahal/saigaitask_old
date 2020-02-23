/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

CREATE TABLE oauthtoken_data (
    id bigserial primary key,
    consumer_key character varying(256) NOT NULL,
    request_token character varying(256) UNIQUE,
    access_token character varying(256) UNIQUE,
    token_secret character varying(256),
    verifier character varying(256),
    groupid bigint,
    created timestamp without time zone DEFAULT now(),
    last_access timestamp without time zone DEFAULT now()
);
ALTER TABLE oauthtoken_data ADD FOREIGN KEY (groupid) REFERENCES group_info(id);

COMMENT ON TABLE  oauthtoken_data IS 'OAuthトークンデータ';
COMMENT ON COLUMN oauthtoken_data.id IS 'ID';
COMMENT ON COLUMN oauthtoken_data.consumer_key IS 'クライアントキー';
COMMENT ON COLUMN oauthtoken_data.request_token IS 'リクエストトークン';
COMMENT ON COLUMN oauthtoken_data.access_token IS 'アクセストークン';
COMMENT ON COLUMN oauthtoken_data.token_secret IS '鍵トークン';
COMMENT ON COLUMN oauthtoken_data.verifier IS '認可コード';
COMMENT ON COLUMN oauthtoken_data.groupid IS '班ID';
COMMENT ON COLUMN oauthtoken_data.created IS '作成日';
COMMENT ON COLUMN oauthtoken_data.last_access IS '最終アクセス日時';

CREATE INDEX oauthtoken_data_groupid_idx
	ON oauthtoken_data (groupid);
