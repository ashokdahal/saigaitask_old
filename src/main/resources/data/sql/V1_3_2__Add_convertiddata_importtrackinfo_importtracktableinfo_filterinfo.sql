/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
DROP TABLE IF EXISTS convertid_data cascade;

CREATE TABLE convertid_data (
  id BIGSERIAL NOT NULL,
  localgovinfoid BIGINT NOT NULL,
  entityname TEXT NOT NULL,
  idname TEXT NOT NULL,
  oldval TEXT NOT NULL,
  newval TEXT NOT NULL,
  PRIMARY KEY (id)
);
ALTER TABLE public.convertid_data OWNER TO postgres;
COMMENT ON COLUMN convertid_data.id IS 'ID';
COMMENT ON COLUMN convertid_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN convertid_data.entityname IS 'エンティティ名';
COMMENT ON COLUMN convertid_data.idname IS 'ID名';
COMMENT ON COLUMN convertid_data.oldval IS '変換前値';
COMMENT ON COLUMN convertid_data.newval IS '変更後値';



DROP TABLE IF EXISTS importtrack_info;
CREATE TABLE importtrack_info(
  id BIGSERIAL NOT NULL,
  localgovinfoid BIGINT NOT NULL,
  oldlocalgovinfoid BIGINT NOT NULL,
  trackdataid BIGINT NOT NULL,
  oldtrackdataid BIGINT NOT NULL,
  mapid BIGINT NOT NULL,
  oldmapid BIGINT NOT NULL,
  PRIMARY KEY (id)
);
ALTER TABLE public.importtrack_info OWNER TO postgres;
COMMENT ON TABLE  importtrack_info IS 'インポート記録情報';
COMMENT ON COLUMN importtrack_info.id IS 'ID';
COMMENT ON COLUMN importtrack_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN importtrack_info.oldlocalgovinfoid IS 'インポート元自治体ID';
COMMENT ON COLUMN importtrack_info.trackdataid IS '記録データID';
COMMENT ON COLUMN importtrack_info.oldtrackdataid IS 'インポート元記録データID';
COMMENT ON COLUMN importtrack_info.mapid IS '地図ID';
COMMENT ON COLUMN importtrack_info.oldmapid IS 'インポート元地図ID';

DROP TABLE IF EXISTS importtracktable_info;
CREATE TABLE importtracktable_info(
  id BIGSERIAL NOT NULL,
  importtrackinfoid BIGINT NOT NULL,
  tracktableinfoid BIGINT NOT NULL,
  layerid TEXT NOT NULL,
  oldlayerid TEXT NOT NULL,
  PRIMARY KEY (id)
);
ALTER TABLE public.importtracktable_info OWNER TO postgres;
COMMENT ON TABLE importtracktable_info IS 'インポート記録テーブル情報';
COMMENT ON COLUMN importtracktable_info.id IS 'ID';
COMMENT ON COLUMN importtracktable_info.importtrackinfoid IS 'インポート記録情報ID';
COMMENT ON COLUMN importtracktable_info.tracktableinfoid IS '記録テーブル情報ID';
COMMENT ON COLUMN importtracktable_info.layerid IS '旧レイヤID';
COMMENT ON COLUMN importtracktable_info.oldlayerid IS '旧レイヤID';


DROP TABLE IF EXISTS filter_info;
CREATE TABLE filter_info (
  id bigserial NOT NULL PRIMARY KEY, 
  menuinfoid BIGINT,
  name TEXT,
  filterid BIGINT,
  note TEXT,
  valid BOOLEAN DEFAULT TRUE,
  disporder INT
);
ALTER TABLE public.filter_info OWNER TO postgres;
COMMENT ON TABLE filter_info IS 'フィルター情報';
COMMENT ON COLUMN filter_info.id IS 'ID';
COMMENT ON COLUMN filter_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN filter_info.name IS '名称';
COMMENT ON COLUMN filter_info.filterid IS 'フィルターID';
COMMENT ON COLUMN filter_info.note IS '備考';
COMMENT ON COLUMN filter_info.valid IS '有効・無効';
COMMENT ON COLUMN filter_info.disporder IS '表示順';

