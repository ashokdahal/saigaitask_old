/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
DROP TABLE IF EXISTS landmark_data;
DROP TABLE IF EXISTS landmark_info;

CREATE TABLE landmark_info (
 id bigserial NOT NULL primary key,
 localgovinfoid bigint NOT NULL,
 valid boolean DEFAULT true NOT NULL
);
ALTER TABLE public.landmark_info OWNER TO postgres;
ALTER TABLE landmark_info ADD CONSTRAINT landmark_info_localgovinfoid_key UNIQUE (localgovinfoid);
COMMENT ON TABLE landmark_info IS 'ランドマーク情報';
COMMENT ON COLUMN landmark_info.id IS 'ID';
COMMENT ON COLUMN landmark_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN landmark_info.valid IS '利用可否';


CREATE TABLE landmark_data (
 id bigserial NOT NULL primary key,
 landmarkinfoid bigint NOT NULL,
 groupid bigint NOT NULL,
 landmark text NOT NULL,
 latitude float8 NOT NULL,
 longitude float8 NOT NULL
);
CREATE INDEX landmark_data_landmarkinfoid_idx ON landmark_data (landmarkinfoid);
ALTER TABLE landmark_data ADD FOREIGN KEY (landmarkinfoid) REFERENCES landmark_info(id);
ALTER TABLE public.landmark_data OWNER TO postgres;
COMMENT ON TABLE landmark_data IS 'ランドマークデータ';
COMMENT ON COLUMN landmark_data.id IS 'ID';
COMMENT ON COLUMN landmark_data.landmarkinfoid IS 'ランドマーク情報ID';
COMMENT ON COLUMN landmark_data.groupid IS 'ランドマークデータ登録者のグループID';
COMMENT ON COLUMN landmark_data.landmark IS 'ランドマークデータ文字列';
COMMENT ON COLUMN landmark_data.latitude IS 'ランドマークデータ緯度';
COMMENT ON COLUMN landmark_data.longitude IS 'ランドマークデータ経度';


