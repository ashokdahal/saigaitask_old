/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

insert into localgov_info (id, valid) values(0, false);

insert into menulogin_info (id, groupid, disasterid, valid) values(0, 0, 0, true);

insert into menutasktype_info (id, localgovinfoid, name, disporder) values(20, 0, '本部会議', 20);

insert into disaster_master (id, name, disporder) values (0, 'デフォルト', 0);
