/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- コメントが日本語になっていたので修正
COMMENT ON TABLE en.toolboxtype_master IS 'Toolbox type master';
COMMENT ON COLUMN en.toolboxtype_master.id IS 'ID';
COMMENT ON COLUMN en.toolboxtype_master.name IS 'name';
COMMENT ON COLUMN en.toolboxtype_master.disporder IS 'disporder';

-- 英語テーブルへのInsert
INSERT INTO en.toolboxtype_master (name,disporder) VALUES ('People layer', 1);
INSERT INTO en.toolboxtype_master (name,disporder) VALUES ('250m mesh layer', 2);

-- 日本語テーブルへ誤ってInsertしたレコードの削除とsetvalでの再定義
delete from toolboxtype_master where id in (3,4);
select setval('toolboxtype_master_id_seq', 2);

-- 多言語化対応
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Tool box type master','Tool box type master');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Tool box type master','ツールボックス種別マスタ');

INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Tool box<!--1-->','Tool box');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Tool box<!--1-->','ツールボックス');

