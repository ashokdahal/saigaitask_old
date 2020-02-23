CREATE TABLE localgovgroup_info (
	id bigserial not null primary key,
	name text,
	localgovinfoid bigint,
    disporder int,
    valid boolean default true
);
COMMENT ON TABLE  localgovgroup_info IS '自治体グループ情報';
COMMENT ON COLUMN localgovgroup_info.id IS 'ID';
COMMENT ON COLUMN localgovgroup_info.name IS '自治体グループ名';
COMMENT ON COLUMN localgovgroup_info.localgovinfoid IS '親自治体ID';
COMMENT ON COLUMN localgovgroup_info.disporder IS '表示順';
COMMENT ON COLUMN localgovgroup_info.valid IS '有効・無効';

ALTER TABLE localgovgroup_info ADD CONSTRAINT localgovinfoid_fkey FOREIGN KEY (localgovinfoid) REFERENCES localgov_info (id);

CREATE TABLE localgovgroupmember_info (
	id bigserial not null primary key,
    localgovgroupinfoid bigint,
	localgovinfoid bigint,
    disporder int,
    valid boolean default true
);
COMMENT ON TABLE  localgovgroupmember_info IS '自治体グループメンバー情報';
COMMENT ON COLUMN localgovgroupmember_info.id IS 'ID';
COMMENT ON COLUMN localgovgroupmember_info.localgovgroupinfoid IS '自治体グループID';
COMMENT ON COLUMN localgovgroupmember_info.localgovinfoid IS '子自治体ID';
COMMENT ON COLUMN localgovgroupmember_info.disporder IS '表示順';
COMMENT ON COLUMN localgovgroupmember_info.valid IS '有効・無効';

ALTER TABLE localgovgroupmember_info ADD CONSTRAINT localgovinfoid_fkey2 FOREIGN KEY (localgovinfoid) REFERENCES localgov_info (id);
ALTER TABLE localgovgroupmember_info ADD CONSTRAINT localgovgroupinfoid_fkey FOREIGN KEY (localgovgroupinfoid) REFERENCES localgovgroup_info (id);


