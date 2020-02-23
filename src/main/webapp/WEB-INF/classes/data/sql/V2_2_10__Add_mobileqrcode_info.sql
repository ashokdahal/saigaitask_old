CREATE TABLE mobileqrcode_info (
	id bigserial not null primary key,
	localgovinfoid bigint,
	oauthconsumerid bigint,
	title text,
	groupid bigint,
	unitid bigint,
	tablemasterinfoid bigint,
	authenticationdate timestamp,
	note text,
    disporder int,
    valid boolean default true
);
COMMENT ON TABLE mobileqrcode_info IS  '投稿アプリ認証QR設定';
COMMENT ON COLUMN mobileqrcode_info.id  IS 'ID';
COMMENT ON COLUMN mobileqrcode_info.localgovinfoid  IS '自治体ID';
COMMENT ON COLUMN mobileqrcode_info.oauthconsumerid  IS 'OAuthコンシューマID';
COMMENT ON COLUMN mobileqrcode_info.title IS 'QRコード名称';
COMMENT ON COLUMN mobileqrcode_info.groupid  IS '認証班ID';
COMMENT ON COLUMN mobileqrcode_info.unitid  IS '認証課ID';
COMMENT ON COLUMN mobileqrcode_info.tablemasterinfoid  IS '投稿先テーブルID';
COMMENT ON COLUMN mobileqrcode_info.authenticationdate  IS '認証終了日';
COMMENT ON COLUMN mobileqrcode_info.note IS '備考';
COMMENT ON COLUMN mobileqrcode_info.disporder IS '表示順';
COMMENT ON COLUMN mobileqrcode_info.valid IS '有効・無効';

-- ALTER TABLE mobileqrcode_info ADD CONSTRAINT localgovinfoid_fkey FOREIGN KEY (localgovinfoid) REFERENCES localgov_info (id);
-- ALTER TABLE mobileqrcode_info ADD CONSTRAINT groupid_fkey FOREIGN KEY (groupid) REFERENCES group_info (id);
-- ALTER TABLE mobileqrcode_info ADD CONSTRAINT unitid_fkey FOREIGN KEY (unitid) REFERENCES unit_info (id);
-- ALTER TABLE mobileqrcode_info ADD CONSTRAINT tablemasterinfoid_fkey FOREIGN KEY (tablemasterinfoid) REFERENCES tablemaster_info (id);
