/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
DELETE FROM multilangmes_info WHERE messageid = 'e-Com?GW posting info';
DELETE FROM multilangmes_info WHERE messageid = 'e-Com?GW posting info ID';
DELETE FROM multilangmes_info WHERE messageid = 'e-Com?GW posting info ID(*)';

INSERT INTO multilangmes_info VALUES (DEFAULT,2,'e-Com GW posting info','eコミグループウェア投稿先情報');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'e-Com GW posting info ID','eコミグループウェア投稿先情報ID');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'e-Com GW posting info ID(*)','eコミグループウェア投稿先情報ID(*)');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'e-Com GW posting info','e-Com GW posting info');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'e-Com GW posting info ID','e-Com GW posting info ID');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'e-Com GW posting info ID(*)','e-Com GW posting info ID(*)');

