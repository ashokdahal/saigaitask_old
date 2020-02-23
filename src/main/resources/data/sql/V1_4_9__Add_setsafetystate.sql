/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
-- 警戒情報コードマスタ
CREATE TABLE meteowarningcode_master (
    id serial NOT NULL,
    meteotypemasterid integer NOT NULL,
    code text NOT NULL,
    name text NOT NULL,
    note text,
    disporder integer NOT NULL,
    valid boolean DEFAULT true NOT NULL
);
ALTER TABLE public.meteowarningcode_master OWNER TO postgres;

COMMENT ON TABLE meteowarningcode_master IS '警戒情報コードマスタ';
COMMENT ON COLUMN meteowarningcode_master.id IS 'ID';
COMMENT ON COLUMN meteowarningcode_master.meteotypemasterid IS '気象情報等取得種別マスタID';
COMMENT ON COLUMN meteowarningcode_master.code IS 'コード';
COMMENT ON COLUMN meteowarningcode_master.name IS '名称';
COMMENT ON COLUMN meteowarningcode_master.note IS '備考';
COMMENT ON COLUMN meteowarningcode_master.disporder IS '表示順';
COMMENT ON COLUMN meteowarningcode_master.valid IS '利用可否';

INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (1,'03','大雨警報','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'5+','震度5強','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (4,'5+','震度5強','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (3,'51','津波警報','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (3,'52','大津波警報','',2,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (5,'21','はん濫注意情報','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (5,'22','はん濫注意情報(警戒情報解除)','',2,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (6,'3','警戒','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (7,'1','記録的短時間大雨情報','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (8,'1','竜巻注意情報','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (9,'01','噴火警報','',1,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (9,'02','火口周辺警報','',2,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'6-','震度6弱','',2,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'6+','震度6強','',3,true);
INSERT INTO meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'7','震度7','',4,true);


-- 警戒情報コードマスタ（多言語化対応）
CREATE TABLE en.meteowarningcode_master (
    id serial NOT NULL,
    meteotypemasterid integer NOT NULL,
    code text NOT NULL,
    name text NOT NULL,
    note text,
    disporder integer NOT NULL,
    valid boolean DEFAULT true NOT NULL
);
ALTER TABLE en.meteowarningcode_master OWNER TO postgres;

COMMENT ON TABLE en.meteowarningcode_master IS '警戒情報コードマスタ';
COMMENT ON COLUMN en.meteowarningcode_master.id IS 'ID';
COMMENT ON COLUMN en.meteowarningcode_master.meteotypemasterid IS '気象情報等取得種別マスタID';
COMMENT ON COLUMN en.meteowarningcode_master.code IS 'コード';
COMMENT ON COLUMN en.meteowarningcode_master.name IS '名称';
COMMENT ON COLUMN en.meteowarningcode_master.note IS '備考';
COMMENT ON COLUMN en.meteowarningcode_master.disporder IS '表示順';
COMMENT ON COLUMN en.meteowarningcode_master.valid IS '利用可否';

INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (1,'03','Torrential Rain Warning','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'5+','Seismic Intensity 5+','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (4,'5+','Seismic Intensity 5+','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (3,'51','Tsunami Advisory','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (3,'52','Major Tsunami Warning','',2,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (5,'21','Flood Warning','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (5,'22','Flood Warning Release','',2,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (6,'3','Warning','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (7,'1','Record Heavy Rain Info','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (8,'1','Tornado Info','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (9,'01','Volcanic Warning','',1,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (9,'02','Near-crater Warning','',2,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'6-','Seismic Intensity 6-','',2,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'6+','Seismic Intensity 6+','',3,true);
INSERT INTO en.meteowarningcode_master (meteotypemasterid, code, name, note, disporder, valid) values (2,'7','Seismic Intensity 7','',4,true);

-- 安否応答状況情報
CREATE TABLE safetystate_info (
    id serial not null primary key,
    localgovinfoid bigint not null,
    name text not null,
    valid boolean,
    disporder int
);
-- index, reference
ALTER TABLE safetystate_info ADD FOREIGN KEY (localgovinfoid) REFERENCES localgov_info(id);

COMMENT ON TABLE safetystate_info IS '安否応答状況情報';
COMMENT ON COLUMN safetystate_info.id IS 'ID';
COMMENT ON COLUMN safetystate_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN safetystate_info.name IS '名称';
COMMENT ON COLUMN safetystate_info.valid IS '有効／無効';
COMMENT ON COLUMN safetystate_info.disporder IS '表示順';

--職員参集状況
ALTER TABLE assemblestate_data ADD safetystateinfoid int;
ALTER TABLE assemblestate_data ADD comment text;
ALTER TABLE assemblestate_data ADD loginstatetus boolean default false;
COMMENT ON COLUMN assemblestate_data.safetystateinfoid IS '安否応答状況';
COMMENT ON COLUMN assemblestate_data.comment IS 'コメント';
COMMENT ON COLUMN assemblestate_data.loginstatetus IS 'ログイン状態';

ALTER TABLE alarmdefaultgroup_info ADD messagetype text;
COMMENT ON COLUMN alarmdefaultgroup_info.messagetype IS 'メッセージタイプ';

