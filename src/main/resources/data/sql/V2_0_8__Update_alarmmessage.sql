/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

alter table alarmmessage_data add column unitid bigint;
alter table alarmmessage_data add column sendunitid bigint;
COMMENT ON COLUMN alarmmessage_data.unitid IS '課ID';
COMMENT ON COLUMN alarmmessage_data.sendunitid IS '送信課ID';
