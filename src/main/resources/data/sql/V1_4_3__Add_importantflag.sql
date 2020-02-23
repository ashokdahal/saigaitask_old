/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table menuprocess_info add important boolean;
COMMENT ON COLUMN menuprocess_info.important IS '重要フラグ';

alter table menutask_info add important boolean;
COMMENT ON COLUMN menutask_info.important IS '重要フラグ';

alter table menutaskmenu_info add important boolean;
COMMENT ON COLUMN menutaskmenu_info.important IS '重要フラグ';
