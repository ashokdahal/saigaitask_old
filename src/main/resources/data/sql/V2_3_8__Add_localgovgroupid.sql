ALTER TABLE localgovgroup_info ADD COLUMN deleted boolean not null default false;
COMMENT ON COLUMN localgovgroup_info.deleted IS '削除フラグ';

ALTER TABLE track_data ADD COLUMN localgovgroupinfoid bigint;
COMMENT ON COLUMN track_data.localgovgroupinfoid IS '自治体グループID';
ALTER TABLE trainingplan_data ADD COLUMN localgovgroupinfoid bigint;
COMMENT ON COLUMN trainingplan_data.localgovgroupinfoid IS '自治体グループID';

ALTER TABLE track_data         ADD CONSTRAINT fkey1 FOREIGN KEY (localgovgroupinfoid) REFERENCES localgovgroup_info (id);
ALTER TABLE trainingplan_data  ADD CONSTRAINT fkey2 FOREIGN KEY (localgovgroupinfoid) REFERENCES localgovgroup_info (id);
