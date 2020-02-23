/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE externalmapdata_info ADD layerparent BIGINT DEFAULT 0;
ALTER TABLE externalmapdata_info ADD attribution TEXT;
ALTER TABLE externalmapdata_info ADD layeropacity FLOAT DEFAULT 1.0;
ALTER TABLE externalmapdata_info ADD wmscapsurl TEXT;
ALTER TABLE externalmapdata_info ADD wmsurl TEXT;
ALTER TABLE externalmapdata_info ADD wmsformat TEXT;
ALTER TABLE externalmapdata_info ADD wmslegendurl TEXT;
ALTER TABLE externalmapdata_info ADD wmsfeatureurl TEXT;
ALTER TABLE externalmapdata_info ADD featuretypeid TEXT;
ALTER TABLE externalmapdata_info ADD layerdescription TEXT;
ALTER TABLE externalmapdata_info ADD searchable BOOLEAN DEFAULT true;

COMMENT ON COLUMN externalmapdata_info.layerparent IS '親レイヤのID';
COMMENT ON COLUMN externalmapdata_info.attribution IS '著作者情報';
COMMENT ON COLUMN externalmapdata_info.layeropacity IS '透明度';
COMMENT ON COLUMN externalmapdata_info.wmscapsurl IS 'WMSCapabilities UR';
COMMENT ON COLUMN externalmapdata_info.wmsurl IS 'WMS URL';
COMMENT ON COLUMN externalmapdata_info.wmsformat IS 'WMS フォーマット';
COMMENT ON COLUMN externalmapdata_info.wmslegendurl IS 'WMS Legend URL';
COMMENT ON COLUMN externalmapdata_info.wmsfeatureurl IS 'WMSフィーチャー URL';
COMMENT ON COLUMN externalmapdata_info.featuretypeid IS '親レイヤのID';
COMMENT ON COLUMN externalmapdata_info.layerdescription IS 'サブレイヤのフィーチャーID';
COMMENT ON COLUMN externalmapdata_info.searchable IS '検索フラグ';
