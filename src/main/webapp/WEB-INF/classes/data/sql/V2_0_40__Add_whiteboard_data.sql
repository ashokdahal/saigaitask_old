/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
CREATE TABLE whiteboard_data (
	id bigserial not null primary key,
	groupid bigint,
	message text,
	registtime timestamp
);

COMMENT ON COLUMN whiteboard_data.id IS 'ID';
COMMENT ON COLUMN whiteboard_data.groupid IS '班ID';
COMMENT ON COLUMN whiteboard_data.message IS 'メッセージ';
COMMENT ON COLUMN whiteboard_data.registtime IS '登録日時';