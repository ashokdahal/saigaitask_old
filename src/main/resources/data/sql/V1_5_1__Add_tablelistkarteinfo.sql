/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
CREATE TABLE tablelistkarte_info(
	id bigserial not null primary key,
	menutableinfoid bigint,
	attrid text,
	name text,
	editable boolean default false,
	highlight boolean default false,
	closed boolean default false,
	disporder int,
	tips text
);

COMMENT ON COLUMN tablelistkarte_info.id IS 'ID';
COMMENT ON COLUMN tablelistkarte_info.menutableinfoid IS 'メニューテーブルID';
COMMENT ON COLUMN tablelistkarte_info.attrid IS 'テーブル項目名';
COMMENT ON COLUMN tablelistkarte_info.name IS '名称';
COMMENT ON COLUMN tablelistkarte_info.editable IS '編集可';
COMMENT ON COLUMN tablelistkarte_info.highlight IS '強調表示';
COMMENT ON COLUMN tablelistkarte_info.closed IS 'グループ折り畳み';
COMMENT ON COLUMN tablelistkarte_info.disporder IS '表示順';
COMMENT ON COLUMN tablelistkarte_info.disporder IS 'Tips';

CREATE INDEX tablelistkarte_info_menutableinfoid_idx on tablelistkarte_info(menutableinfoid);
