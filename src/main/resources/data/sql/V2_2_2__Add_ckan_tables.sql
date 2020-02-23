
create table ckanauth_info (
	id bigserial not null primary key,
	localgovinfoid bigint,
	authkey text,
	note text
);
COMMENT ON TABLE ckanauth_info  IS 'CKAN認証情報テーブル';
COMMENT ON COLUMN ckanauth_info.id  IS 'ID';
COMMENT ON COLUMN ckanauth_info.localgovinfoid  IS '自治体ID';
COMMENT ON COLUMN ckanauth_info.authkey  IS 'CKAN認証キー';
COMMENT ON COLUMN ckanauth_info.note  IS '備考';

create table ckanmetadata_info (
	id bigserial not null primary key,
	localgovinfoid bigint,
	tablemasterinfoid bigint,
	name text,
	infotype text,
	ownerorg text,
	ownerorgtitle text,
	title text,
	note text,
	tags text
);
COMMENT ON TABLE ckanmetadata_info IS  'CKAN事前データ情報テーブル';
COMMENT ON COLUMN ckanmetadata_info.id  IS 'ID';
COMMENT ON COLUMN ckanmetadata_info.localgovinfoid  IS '自治体ID';
COMMENT ON COLUMN ckanmetadata_info.tablemasterinfoid  IS 'テーブルID';
COMMENT ON COLUMN ckanmetadata_info.name  IS 'CKAN名称';
COMMENT ON COLUMN ckanmetadata_info.infotype  IS '情報種別ID';
COMMENT ON COLUMN ckanmetadata_info.ownerorg  IS '組織キー';
COMMENT ON COLUMN ckanmetadata_info.ownerorgtitle  IS '組織名称';
COMMENT ON COLUMN ckanmetadata_info.title  IS 'タイトル';
COMMENT ON COLUMN ckanmetadata_info.note  IS '要約';
COMMENT ON COLUMN ckanmetadata_info.tags  IS '検索タグ';

create table ckanmetadatadefault_info (
	id bigserial not null primary key,
	localgovinfoid bigint,
	author text,
	authoremail text,
	maintainer text,
	maintaineremail text
);
COMMENT ON TABLE ckanmetadatadefault_info  IS 'CKANメタデータデフォルト設定情報';
COMMENT ON COLUMN ckanmetadatadefault_info.id  IS 'ID';
COMMENT ON COLUMN ckanmetadatadefault_info.localgovinfoid  IS '自治体ID';
COMMENT ON COLUMN ckanmetadatadefault_info.author  IS '作成者';
COMMENT ON COLUMN ckanmetadatadefault_info.authoremail  IS '作成者のメールアドレス';
COMMENT ON COLUMN ckanmetadatadefault_info.maintainer  IS 'メンテナー';
COMMENT ON COLUMN ckanmetadatadefault_info.maintaineremail  IS 'メンテナーのメールアドレス';
