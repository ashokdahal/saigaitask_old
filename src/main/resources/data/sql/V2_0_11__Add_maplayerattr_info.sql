/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table maplayerattr_info add column defaultcheck boolean default false;
comment on column maplayerattr_info.defaultcheck IS '初期チェック';

alter table maplayerattr_info add column groupdefaultcheck boolean default false;
comment on column maplayerattr_info.groupdefaultcheck IS 'グループ初期チェック';

alter table maplayerattr_info add column addable boolean default false;
comment on column maplayerattr_info.addable IS '一括追記';
