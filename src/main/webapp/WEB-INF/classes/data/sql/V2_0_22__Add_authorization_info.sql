/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/*
 * Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- 外部地図に関する認証情報を登録するテーブル
-- 現在はBasic認証のみであるが、将来的にOAuth等別の認証でも対応出来るよう、カラムは設定しておく
create table authorization_info (
  id bigserial not null primary key,
  authtype text,
  username text,
  userpass text,
  authword text
);

-- Insert例
-- INSERT INTO authorization_info (authtype, username, userpass,authword) VALUES ('Basic', 'test', 'system', '');

COMMENT ON TABLE authorization_info IS '認証情報';
COMMENT ON COLUMN authorization_info.id IS 'ID';
COMMENT ON COLUMN authorization_info.authtype IS '認証方式';
COMMENT ON COLUMN authorization_info.username IS 'ログイン名';
COMMENT ON COLUMN authorization_info.userpass IS 'ログインパスワード';
COMMENT ON COLUMN authorization_info.authword IS '認証情報フリーワード';

-- 外部地図、外部リスト、KMLで利用するテーブルに、認証情報テーブルのIDを格納する
alter table externalmapdata_info add column authorizationinfoid bigint;
COMMENT ON COLUMN externalmapdata_info.authorizationinfoid IS '認証情報ID';
ALTER TABLE externalmapdata_info ADD FOREIGN KEY (authorizationinfoid) REFERENCES authorization_info(id);

alter table externaltabledata_info add column authorizationinfoid bigint;
COMMENT ON COLUMN externaltabledata_info.authorizationinfoid IS '認証情報ID';
ALTER TABLE externaltabledata_info ADD FOREIGN KEY (authorizationinfoid) REFERENCES authorization_info(id);

alter table mapkmllayer_info add column authorizationinfoid bigint;
COMMENT ON COLUMN mapkmllayer_info.authorizationinfoid IS '認証情報ID';
ALTER TABLE mapkmllayer_info ADD FOREIGN KEY (authorizationinfoid) REFERENCES authorization_info(id);

