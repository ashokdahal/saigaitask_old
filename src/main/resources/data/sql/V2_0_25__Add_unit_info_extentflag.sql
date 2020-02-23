/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table unit_info add extent text;
comment on column unit_info.extent IS 'ホーム（WKT）';

alter table unit_info add resolution double precision;
comment on column unit_info.resolution IS '解像度（デフォルト０）';
