/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

ALTER TABLE observlist_info RENAME COLUMN observeid TO observid;

-- 河川情報
INSERT INTO adminmenu_info VALUES (865, '001005002000000000000', '河川情報', 3, 1, 0, '', true);
INSERT INTO adminmenu_info VALUES (866, '001005002001000000000', 'テレメータ
', 4, 1, 0, '../telemeterserverInfo', true);
INSERT INTO adminmenu_info VALUES (867, '001005002002000000000', 'メニュー設定
', 4, 1, 0, '../observmenuInfo', true);
INSERT INTO adminmenu_info VALUES (868, '001005002003000000000', '観測地点', 4, 1, 0, '', true);
INSERT INTO adminmenu_info VALUES (869, '001005002003001000000', '雨量レイヤ
', 5, 1, 0, '../observatoryrainlayerInfo', true);
INSERT INTO adminmenu_info VALUES (870, '001005002003002000000', '雨量観測地点
', 5, 1, 0, '../observatoryrainInfo', true);
INSERT INTO adminmenu_info VALUES (871, '001005002003003000000', '河川水位レイヤ
', 5, 1, 0, '../observatoryriverlayerInfo', true);
INSERT INTO adminmenu_info VALUES (872, '001005002003004000000', '河川水位観測地点
', 5, 1, 0, '../observatoryriverInfo', true);
INSERT INTO adminmenu_info VALUES (873, '001005002003005000000', 'ダムレイや
', 5, 1, 0, '../observatorydamlayerInfo', true);
INSERT INTO adminmenu_info VALUES (874, '001005002003006000000', 'ダム観測地点
', 5, 1, 0, '../observatorydamInfo', true);
INSERT INTO adminmenu_info VALUES (875, '001005002004000000000', '判定', 4, 1, 0, '../judgemanInfo', true);
