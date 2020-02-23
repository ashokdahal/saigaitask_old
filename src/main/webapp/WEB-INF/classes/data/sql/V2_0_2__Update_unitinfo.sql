/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

alter table unit_info add column localgovinfoid bigint;
alter table unit_info add column password text;
alter table unit_info add column ecomuser text;
alter table unit_info add column deleted boolean default false;
COMMENT ON COLUMN unit_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN unit_info.password IS 'パスワード';
COMMENT ON COLUMN unit_info.ecomuser IS 'eコミマップのアカウント';
COMMENT ON COLUMN unit_info.deleted IS '削除';

alter table user_info add column groupid bigint;
COMMENT ON COLUMN user_info.groupid IS '班ID';


update user_info set groupid = (select groupid from unit_info where unit_info.id = user_info.unitid);
update unit_info set localgovinfoid = (select localgovinfoid from group_info where unit_info.groupid = group_info.id);

alter table login_data add column unitid bigint;
COMMENT ON COLUMN login_data.unitid IS '課ID';

alter table unit_info add column admin boolean default false;
COMMENT ON COLUMN unit_info.admin IS '管理権限';

alter table menulogin_info add column unitid bigint;
COMMENT ON COLUMN menulogin_info.unitid IS 'ユニットID';
