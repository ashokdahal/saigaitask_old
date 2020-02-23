/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

ALTER TABLE earthquakelayer_data ADD COLUMN earthquakelayerinfoid bigint;
COMMENT ON COLUMN earthquakelayer_data.earthquakelayerinfoid IS '震度レイヤ設定ID';
