/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE mapreferencelayer_info ADD layeropacity FLOAT DEFAULT 1.0;

COMMENT ON COLUMN mapreferencelayer_info.layeropacity IS '透明度';
