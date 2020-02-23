/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- 意思決定支援レイヤタイプマスタ
CREATE TABLE decisionsupporttype_master (
    id bigserial not null primary key,
    name text,
    disporder int
);

COMMENT ON TABLE decisionsupporttype_master IS '意思決定支援レイヤ種別マスタ';
COMMENT ON COLUMN decisionsupporttype_master.id IS 'ID';
COMMENT ON COLUMN decisionsupporttype_master.name IS '名称';
COMMENT ON COLUMN decisionsupporttype_master.disporder IS '表示順';

INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('建物被害推定レイヤ', 1);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('人口レイヤ', 2);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('停電エリアレイヤ', 3);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('断水エリアレイヤ', 4);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('250mメッシュレイヤ', 5);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('建物被害による避難者数推定レイヤ', 6);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('停電、断水による避難者数推定レイヤ', 7);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('帰宅困難者推定レイヤ', 8);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('総避難者数推定レイヤ', 9);
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('避難所レイヤ', 10);

-- 意思決定支援レイヤタイプマスタ(多言語化対応)
CREATE TABLE en.decisionsupporttype_master (
    id bigserial not null primary key,
    name text,
    disporder int
);

COMMENT ON TABLE en.decisionsupporttype_master IS '意思決定支援レイヤ種別マスタ';
COMMENT ON COLUMN en.decisionsupporttype_master.id IS 'ID';
COMMENT ON COLUMN en.decisionsupporttype_master.name IS '名称';
COMMENT ON COLUMN en.decisionsupporttype_master.disporder IS '表示順';

INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Building damage estimated layer', 1);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('People layer', 2);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Power outage area layer', 3);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Suspension of water supply area layer', 4);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('250m mesh layer', 5);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Evacuees layer due to building damage estimated', 6);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Evacuees layer due to Power outage or Suspension of water supply', 7);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Evacuees layer due to Stranded commuters', 8);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Total evacuees layer', 9);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Shelter layer', 10);


-- 意思決定支援レイヤ情報
-- decisionsupporttypeid = 1 -> attrid1 = 全壊率, attrid2 = 半壊率
-- decisionsupporttypeid = 2 -> attrid1 = 人口数, attrid2 = 世帯数
-- decisionsupporttypeid = 3 -> Geometryのみを利用する為、attrid1,2の設定不要
-- decisionsupporttypeid = 4 -> Geometryのみを利用する為、attrid1,2の設定不要
-- decisionsupporttypeid = 5 -> Geometryのみを利用する為、attrid1,2の設定不要
-- decisionsupporttypeid = 6 -> attrid1 = 避難者数
-- decisionsupporttypeid = 7 -> attrid1 = 避難者数
-- decisionsupporttypeid = 8 -> attrid1 = 避難者数
-- decisionsupporttypeid = 9 -> attrid1 = 避難者数
-- decisionsupporttypeid =10 -> attrid1 = 推定避難者数, attrid2 = 収容定員数
CREATE TABLE decisionsupport_info (
    id bigserial not null primary key,
    localgovinfoid bigint,
    decisionsupporttypeid int,
    tablemasterinfoid bigint,
    attrid1 text,
    attrid2 text,
    note text,
    disporder int,
    valid boolean
);
-- index, reference
CREATE INDEX decisionsupport_info_localgovinfoid_idx ON decisionsupport_info (localgovinfoid);
CREATE INDEX decisionsupport_info_decisionsupporttypeid_idx ON decisionsupport_info (decisionsupporttypeid);

ALTER TABLE decisionsupport_info ADD FOREIGN KEY (localgovinfoid) REFERENCES localgov_info(id);
ALTER TABLE decisionsupport_info ADD FOREIGN KEY (decisionsupporttypeid) REFERENCES decisionsupporttype_master(id);

COMMENT ON TABLE decisionsupport_info IS '意思決定支援レイヤ情報';
COMMENT ON COLUMN decisionsupport_info.id IS 'ID';
COMMENT ON COLUMN decisionsupport_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN decisionsupport_info.decisionsupporttypeid IS '意思決定支援レイヤ種別ID';
COMMENT ON COLUMN decisionsupport_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN decisionsupport_info.attrid1 IS '属性ID1';
COMMENT ON COLUMN decisionsupport_info.attrid2 IS '属性ID2';
COMMENT ON COLUMN decisionsupport_info.note IS '備考';
COMMENT ON COLUMN decisionsupport_info.disporder IS '表示順';
COMMENT ON COLUMN decisionsupport_info.valid IS '有効無効';

