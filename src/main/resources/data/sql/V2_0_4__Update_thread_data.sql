/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

alter table threadsendto_data add column unitid bigint;
COMMENT ON COLUMN threadsendto_data.unitid IS '課ID';

alter table threadresponse_data add column unitid bigint;
COMMENT ON COLUMN threadresponse_data.unitid IS '課ID';

alter table thread_data add column unitid bigint;
COMMENT ON COLUMN thread_data.unitid IS '課ID';
