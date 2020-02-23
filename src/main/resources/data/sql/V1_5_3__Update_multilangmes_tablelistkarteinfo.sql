/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
DELETE FROM multilangmes_info WHERE messageid='Group folder';
DELETE FROM multilangmes_info WHERE messageid='Group folder(*)';
DELETE FROM multilangmes_info WHERE messageid='Group';
DELETE FROM multilangmes_info WHERE messageid='Edit - Table list karte info';
DELETE FROM multilangmes_info WHERE messageid='Add - Table list karte info';
DELETE FROM multilangmes_info WHERE messageid='Delete - Table list karte info';
DELETE FROM multilangmes_info WHERE messageid='Record copy - Table list karte info';

INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Group','Group');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Group','班');

INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Group folder','グループ折り畳み');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Group folder(*)','グループ折り畳み(*)');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Group<!--2-->','グループ');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Edit - Table list karte info','編集 - テーブルリストカルテ情報');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Add - Table list karte info','追加 - テーブルリストカルテ情報');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Delete - Table list karte info','削除 - テーブルリストカルテ情報');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Record copy - Table list karte info','レコードコピー - テーブルリストカルテ情報');

INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Group folder','Group folde');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Group folder(*)','Group folder(*)');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Group<!--2-->','Group');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Edit - Table list karte info','Edit - Table list karte info');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Add - Table list karte info','Add - Table list karte info');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Delete - Table list karte info','Delete - Table list karte info');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Record copy - Table list karte info','Record copy - Table list karte info');
