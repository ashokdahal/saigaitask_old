
CREATE TABLE jsonimportapi_info (
	id bigserial not null primary key,
	localgovinfoid bigint,
	url text,
	authkey text,
	valid boolean,
	"interval" integer,
	note text
);
COMMENT ON TABLE jsonimportapi_info  IS 'JSON連携API設定';
COMMENT ON COLUMN jsonimportapi_info.id  IS 'ID';
COMMENT ON COLUMN jsonimportapi_info.localgovinfoid  IS '自治体ID';
COMMENT ON COLUMN jsonimportapi_info.url IS '本番環境URL';
COMMENT ON COLUMN jsonimportapi_info.authkey  IS '認証キー';
COMMENT ON COLUMN jsonimportapi_info.valid  IS '有効/無効';
COMMENT ON COLUMN jsonimportapi_info."interval"  IS '間隔';
COMMENT ON COLUMN jsonimportapi_info.note  IS '備考';

CREATE TABLE jsonimportlayer_info (
	id bigserial not null primary key,
	jsonimportapiinfoid bigint,
	category text,
	tablemasterinfoid bigint,
	noattr text,
	contentsattr text,
	categorytextattr text,
	subjectattr text,
	note text
);
ALTER TABLE jsonimportlayer_info ADD FOREIGN KEY (jsonimportapiinfoid) REFERENCES jsonimportapi_info(id);
COMMENT ON TABLE jsonimportlayer_info  IS 'JSON連携更新対象レイヤ';
COMMENT ON COLUMN jsonimportlayer_info.id  IS 'ID';
COMMENT ON COLUMN jsonimportlayer_info.jsonimportapiinfoid IS 'JSON連携API設定ID';
COMMENT ON COLUMN jsonimportlayer_info.category  IS '区分';
COMMENT ON COLUMN jsonimportlayer_info.tablemasterinfoid  IS 'テーブルID';
COMMENT ON COLUMN jsonimportlayer_info.noattr IS '整理番号属性';
COMMENT ON COLUMN jsonimportlayer_info.contentsattr  IS 'Contents属性';
COMMENT ON COLUMN jsonimportlayer_info.categorytextattr  IS 'Category_text属性';
COMMENT ON COLUMN jsonimportlayer_info.note  IS 'Subject属性';
COMMENT ON COLUMN jsonimportlayer_info.note  IS '備考';

