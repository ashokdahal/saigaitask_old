/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table adminmenu_info add column unitid bigint;
COMMENT ON COLUMN adminmenu_info.unitid IS '課ID';
