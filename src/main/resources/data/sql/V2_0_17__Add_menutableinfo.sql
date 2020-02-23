/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE menutable_info ADD disporder integer;
COMMENT ON COLUMN menutable_info.disporder IS '表示順';
