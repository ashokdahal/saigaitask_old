/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

CREATE TABLE importtablemaster_data (
    id bigserial NOT NULL PRIMARY KEY,
    localgovinfoid bigint NOT NULL,
    tablemasterinfoid bigint NOT NULL,
    mapmasterinfoid bigint,
    layerid text,
    tablename text,
    name text,
    geometrytype text,
    copy boolean,
    addresscolumn text,
    updatecolumn text,
    coordinatecolumn text,
    mgrscolumn text,
    mgrsdigit integer DEFAULT 4,
    note text,
    deleted boolean
);


ALTER TABLE public.importtablemaster_data OWNER TO postgres;
COMMENT ON TABLE importtablemaster_data IS 'インポートテーブルマスター情報';
COMMENT ON COLUMN importtablemaster_data.id IS 'ID';
COMMENT ON COLUMN importtablemaster_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN importtablemaster_data.tablemasterinfoid IS 'テーブルマスター情報ID';
COMMENT ON COLUMN importtablemaster_data.mapmasterinfoid IS '地図マスター情報ID';
COMMENT ON COLUMN importtablemaster_data.layerid IS 'レイヤID';
COMMENT ON COLUMN importtablemaster_data.tablename IS 'テーブル名';
COMMENT ON COLUMN importtablemaster_data.name IS '名称';
COMMENT ON COLUMN importtablemaster_data.geometrytype IS 'ジオメトリのタイプ';
COMMENT ON COLUMN importtablemaster_data.copy IS 'コピーフラグ';
COMMENT ON COLUMN importtablemaster_data.addresscolumn IS '住所項目名';
COMMENT ON COLUMN importtablemaster_data.updatecolumn IS '更新日時項目名';
COMMENT ON COLUMN importtablemaster_data.coordinatecolumn IS '座標表示目名';
COMMENT ON COLUMN importtablemaster_data.mgrscolumn IS 'MRGSグリッド項目名';
COMMENT ON COLUMN importtablemaster_data.mgrsdigit IS 'MRGS桁数';
COMMENT ON COLUMN importtablemaster_data.note IS '備考';
COMMENT ON COLUMN importtablemaster_data.deleted IS '削除フラグ';


