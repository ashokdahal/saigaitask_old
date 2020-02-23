/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

alter table localgov_info add column coordinatedecimal boolean default false;

COMMENT ON COLUMN localgov_info.coordinatedecimal IS '座標10進法表示';
