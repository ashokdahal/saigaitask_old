/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE mapmaster_info ADD COLUMN restrictedextent TEXT;
COMMENT ON COLUMN mapmaster_info.restrictedextent IS '表示制限範囲のBBOX';
