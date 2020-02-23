/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
create table earthquakelayer_info(
	id bigserial primary key,
	localgovinfoid bigint,
	tablemasterinfoid bigint
);

COMMENT ON TABLE earthquakelayer_info IS '震度レイヤ設定';
COMMENT ON COLUMN earthquakelayer_info.id IS 'ID';
COMMENT ON COLUMN earthquakelayer_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN earthquakelayer_info.tablemasterinfoid IS 'テーブルID';
