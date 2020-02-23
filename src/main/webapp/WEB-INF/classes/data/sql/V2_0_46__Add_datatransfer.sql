/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */


CREATE TABLE datatransfer_info (
	id bigserial primary key,
	tablemasterinfoid bigint,
	format text,
	protocol text,
	host text,
	port text,
	authentication text,
	userid text,
	password text,
	directory text,
	transfertype int default 1,
	crontext text,
	valid boolean default true,
	note text
);
COMMENT ON TABLE datatransfer_info IS '';
COMMENT ON COLUMN datatransfer_info.id IS 'ID';
COMMENT ON COLUMN datatransfer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN datatransfer_info.format IS 'フォーマット';
COMMENT ON COLUMN datatransfer_info.protocol IS '転送プロトコル';
COMMENT ON COLUMN datatransfer_info.host IS 'ホスト';
COMMENT ON COLUMN datatransfer_info.port IS 'ポート番号';
COMMENT ON COLUMN datatransfer_info.authentication IS '認証';
COMMENT ON COLUMN datatransfer_info.userid IS 'ユーザID';
COMMENT ON COLUMN datatransfer_info.password IS 'パスワード';
COMMENT ON COLUMN datatransfer_info.directory IS 'ディレクトリ';
COMMENT ON COLUMN datatransfer_info.transfertype IS '1:更新時,2:定期的';
COMMENT ON COLUMN datatransfer_info.crontext IS '送信日時';
COMMENT ON COLUMN datatransfer_info.valid IS '有効・無効';
COMMENT ON COLUMN datatransfer_info.note IS '備考';
