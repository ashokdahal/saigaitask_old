/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- 訓練プラン情報
CREATE TABLE trainingplan_data (
    id bigserial not null primary key,
    localgovinfoid bigint,
    disasterid integer,
    name text,
    note text,
    publiccommonsflag boolean,
    facebookflag boolean,
    twitterflag boolean,
    ecommapgwflag boolean,
    updatetime timestamp without time zone,
    deleted boolean
);
-- index, reference
CREATE INDEX trainingplan_data_localgovinfoid_idx ON trainingplan_data (localgovinfoid);
CREATE INDEX trainingplan_data_disasterid_idx ON trainingplan_data (disasterid);
CREATE INDEX trainingplan_data_deleted_idx ON trainingplan_data (deleted);
ALTER TABLE trainingplan_data ADD FOREIGN KEY (localgovinfoid) REFERENCES localgov_info(id);

COMMENT ON TABLE trainingplan_data IS '訓練プラン';
COMMENT ON COLUMN trainingplan_data.id IS 'ID';
COMMENT ON COLUMN trainingplan_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN trainingplan_data.disasterid IS '災害種別ID';
COMMENT ON COLUMN trainingplan_data.name IS '訓練プラン名';
COMMENT ON COLUMN trainingplan_data.note IS '備考';
COMMENT ON COLUMN trainingplan_data.publiccommonsflag IS '公共コモンズ利用制限フラグ';
COMMENT ON COLUMN trainingplan_data.facebookflag IS 'Facebook利用制限フラグ';
COMMENT ON COLUMN trainingplan_data.twitterflag IS 'Twitter利用制限フラグ';
COMMENT ON COLUMN trainingplan_data.ecommapgwflag IS 'eコミマップグループウェア利用制限フラグ';
COMMENT ON COLUMN trainingplan_data.updatetime IS '更新時刻';
COMMENT ON COLUMN trainingplan_data.deleted IS '削除フラグ';

-- 訓練プラン連携自治体情報
CREATE TABLE trainingplanlink_data (
    id bigserial not null primary key,
    trainingplandataid bigint,
    localgovinfoid bigint,
    updatetime timestamp without time zone
);
COMMENT ON TABLE trainingplanlink_data IS '訓練プラン連携自治体情報';
COMMENT ON COLUMN trainingplanlink_data.id IS '訓練プラン連携情報ID';
COMMENT ON COLUMN trainingplanlink_data.trainingplandataid IS '訓練プランID';
COMMENT ON COLUMN trainingplanlink_data.localgovinfoid IS '訓練プラン連携中自治体ID';
COMMENT ON COLUMN trainingplanlink_data.updatetime IS '更新時刻';
ALTER TABLE trainingplanlink_data ADD FOREIGN KEY (localgovinfoid) REFERENCES localgov_info(id);
ALTER TABLE trainingplanlink_data ADD FOREIGN KEY (trainingplandataid) REFERENCES trainingplan_data(id);

-- 訓練外部XMLデータ情報
CREATE TABLE trainingmeteo_data (
    id bigserial not null primary key,
    trainingplandataid bigint,
    meteourl text,
    meteotypeid int4,
    name text,
    note text,
    updatetime timestamp without time zone,
    deleted boolean
);
COMMENT ON TABLE trainingmeteo_data IS '訓練外部XMLデータ';
COMMENT ON COLUMN trainingmeteo_data.id IS 'ID';
COMMENT ON COLUMN trainingmeteo_data.trainingplandataid IS '訓練プランID';
COMMENT ON COLUMN trainingmeteo_data.meteourl IS '気象情報XMLファイルURL';
COMMENT ON COLUMN trainingmeteo_data.meteotypeid IS '気象情報種別';
COMMENT ON COLUMN trainingmeteo_data.name IS '外部データ設定の名称';
COMMENT ON COLUMN trainingmeteo_data.note IS '外部データ設定の備考';
COMMENT ON COLUMN trainingmeteo_data.updatetime IS '更新時刻';
COMMENT ON COLUMN trainingmeteo_data.deleted IS '削除フラグ';
ALTER TABLE trainingmeteo_data ADD FOREIGN KEY (trainingplandataid) REFERENCES trainingplan_data(id);

-- 記録データに訓練プランIDを持たせる
ALTER TABLE track_data ADD COLUMN trainingplandataid bigint;
COMMENT ON COLUMN track_data.trainingplandataid IS '訓練プランID';
ALTER TABLE track_data ADD FOREIGN KEY (trainingplandataid) REFERENCES trainingplan_data(id);
