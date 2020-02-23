/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- ツールボックス種別マスタ
CREATE TABLE toolboxtype_master (
    id bigserial not null primary key,
    name text,
    disporder int
);

COMMENT ON TABLE toolboxtype_master IS 'ツールボックス種別マスタ';
COMMENT ON COLUMN toolboxtype_master.id IS 'ID';
COMMENT ON COLUMN toolboxtype_master.name IS '名称';
COMMENT ON COLUMN toolboxtype_master.disporder IS '表示順';

-- ツールボックスの人口演算に使用する種別
INSERT INTO toolboxtype_master (name,disporder) VALUES ('人口レイヤ', 1);
INSERT INTO toolboxtype_master (name,disporder) VALUES ('250mメッシュレイヤ', 2);

-- ツールボックス種別マスタ(多言語化対応)
CREATE TABLE en.toolboxtype_master (
    id bigserial not null primary key,
    name text,
    disporder int
);

COMMENT ON TABLE en.toolboxtype_master IS 'ツールボックス種別マスタ';
COMMENT ON COLUMN en.toolboxtype_master.id IS 'ID';
COMMENT ON COLUMN en.toolboxtype_master.name IS '名称';
COMMENT ON COLUMN en.toolboxtype_master.disporder IS '表示順';

INSERT INTO toolboxtype_master (name,disporder) VALUES ('People layer', 1);
INSERT INTO toolboxtype_master (name,disporder) VALUES ('250m mesh layer', 2);


-- ツールボックス情報
CREATE TABLE toolbox_data (
    id bigserial not null primary key,
    localgovinfoid bigint,
    toolboxtypeid bigint,
    tablemasterinfoid bigint,
    attrid1 text,
    attrid2 text,
    note text,
    disporder int,
    valid boolean DEFAULT true NOT NULL
);
-- index, reference
CREATE INDEX toolbox_data_localgovinfoid_idx ON toolbox_data (localgovinfoid);
CREATE INDEX toolbox_data_toolboxtypeid_idx ON toolbox_data (toolboxtypeid);

ALTER TABLE toolbox_data ADD FOREIGN KEY (localgovinfoid) REFERENCES localgov_info(id);
ALTER TABLE toolbox_data ADD FOREIGN KEY (toolboxtypeid) REFERENCES toolboxtype_master(id);

COMMENT ON TABLE toolbox_data IS 'ツールボックス情報';
COMMENT ON COLUMN toolbox_data.id IS 'ID';
COMMENT ON COLUMN toolbox_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN toolbox_data.toolboxtypeid IS 'ツールボックス種別ID';
COMMENT ON COLUMN toolbox_data.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN toolbox_data.attrid1 IS '属性ID1';
COMMENT ON COLUMN toolbox_data.attrid2 IS '属性ID2';
COMMENT ON COLUMN toolbox_data.note IS '備考';
COMMENT ON COLUMN toolbox_data.disporder IS '表示順';
COMMENT ON COLUMN toolbox_data.valid IS '有効無効';
