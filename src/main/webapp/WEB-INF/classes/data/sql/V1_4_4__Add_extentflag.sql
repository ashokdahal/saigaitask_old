/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table group_info add extent text;
comment on column group_info.extent IS '地図範囲（WKT）';

alter table group_info add resolution double precision;
comment on column group_info.resolution IS '解像度（デフォルト０）';
