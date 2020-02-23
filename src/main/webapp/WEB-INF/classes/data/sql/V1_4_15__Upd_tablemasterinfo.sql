/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
-- 職員参集 安否／参集状況更新のため
update tablemaster_info set updatecolumn='updatetime' where tablename='assemblestate_data';
