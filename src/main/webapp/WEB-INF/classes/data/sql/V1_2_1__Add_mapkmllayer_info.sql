/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

create table mapkmllayer_info (
       id bigserial not null primary key,
       menuinfoid bigint,
       layerid text,
       visible boolean,
       closed boolean,
       searchable boolean,
       valid boolean,
       disporder int
);
ALTER TABLE mapkmllayer_info ADD FOREIGN KEY (menuinfoid) REFERENCES menu_info(id);

COMMENT ON TABLE  mapkmllayer_info IS 'KMLレイヤ情報';
COMMENT ON COLUMN mapkmllayer_info.id IS 'ID';
COMMENT ON COLUMN mapkmllayer_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN mapkmllayer_info.layerid IS 'レイヤID';
COMMENT ON COLUMN mapkmllayer_info.visible IS '初期表示フラグ';
COMMENT ON COLUMN mapkmllayer_info.closed IS '凡例折りたたみ';
COMMENT ON COLUMN mapkmllayer_info.searchable IS '検索フラグ';
COMMENT ON COLUMN mapkmllayer_info.valid IS '有効・無効';
COMMENT ON COLUMN mapkmllayer_info.disporder IS '表示順';
