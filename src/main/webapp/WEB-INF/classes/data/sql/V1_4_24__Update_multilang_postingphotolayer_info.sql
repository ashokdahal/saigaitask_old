/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
DELETE FROM multilangmes_info WHERE messageid = 'Add ? posted photo layer info';
DELETE FROM multilangmes_info WHERE messageid = 'Delete - posted photo layer info';
DELETE FROM multilangmes_info WHERE messageid = 'Edit - posted photo layer info';
DELETE FROM multilangmes_info WHERE messageid = 'Record copy - Posted photo layer info';

insert into multilangmes_info values (DEFAULT,2,'Add - posted photo layer info','追加 - 投稿写真レイヤ情報');
insert into multilangmes_info values (DEFAULT,2,'Delete - posted photo layer info','削除 - 投稿写真レイヤ情報');
insert into multilangmes_info values (DEFAULT,2,'Edit - posted photo layer info','編集 - 投稿写真レイヤ情報');
insert into multilangmes_info values (DEFAULT,2,'Record copy - Posted photo layer info','レコードコピー - 投稿写真レイヤ情報');
insert into multilangmes_info values (DEFAULT,1,'Add - posted photo layer info','Add - posted photo layer info');
insert into multilangmes_info values (DEFAULT,1,'Delete - posted photo layer info','Delete - posted photo layer info');
insert into multilangmes_info values (DEFAULT,1,'Edit - posted photo layer info','Edit - posted photo layer info');
insert into multilangmes_info values (DEFAULT,1,'Record copy - Posted photo layer info','Record copy - Posted photo layer info');

INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Notification template type ID','通知テンプレート種別ID');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Notification template type ID','Notification template type ID');
