/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

update multilangmes_info set message='Messenger' where messageid='Board' and multilanginfoid=1;
update multilangmes_info set message='メッセンジャー' where messageid='Board' and multilanginfoid=2;

-- whiteboard
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Whiteboard','Whiteboard');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Whiteboard','ホワイトボード');

