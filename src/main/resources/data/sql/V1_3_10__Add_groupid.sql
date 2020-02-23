/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE disasterschool_history ADD COLUMN groupid text;
ALTER TABLE stationorder_history ADD COLUMN groupid text;
ALTER TABLE disasterlifeline_history ADD COLUMN groupid text;
ALTER TABLE disasterhouse_history ADD COLUMN groupid text;
ALTER TABLE disasterwelfare_history ADD COLUMN groupid text;
ALTER TABLE disastercasualties_history ADD COLUMN groupid text;
ALTER TABLE assemblestate_history ADD COLUMN groupid text;
ALTER TABLE disasterroad_history ADD COLUMN groupid text;
ALTER TABLE disasterhospital_history ADD COLUMN groupid text;
ALTER TABLE disasterfarm_history ADD COLUMN groupid text;
