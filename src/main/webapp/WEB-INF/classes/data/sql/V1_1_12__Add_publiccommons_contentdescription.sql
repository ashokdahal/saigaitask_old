/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

ALTER TABLE publiccommons_report_data ADD contentdescription text;
COMMENT ON COLUMN publiccommons_report_data.contentdescription IS '見出し';
