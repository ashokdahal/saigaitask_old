/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE whiteboard_data ADD COLUMN trackdataid bigint;
COMMENT ON COLUMN whiteboard_data.trackdataid IS '記録ID';