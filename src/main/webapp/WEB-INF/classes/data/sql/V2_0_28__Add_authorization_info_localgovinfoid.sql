/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table authorization_info add localgovinfoid bigint;
comment on column authorization_info.localgovinfoid IS '自治体ID';
