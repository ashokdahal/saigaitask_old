
ALTER TABLE jsonimportlayer_info ADD COLUMN receptiondatetimeattr text;
COMMENT ON COLUMN jsonimportlayer_info.receptiondatetimeattr  IS '受信日時属性';
COMMENT ON COLUMN jsonimportlayer_info.subjectattr  IS 'Subject属性';

COMMENT ON COLUMN jsonimportapi_info.url IS 'URL';
COMMENT ON COLUMN jsonimportapi_info.authkey  IS '認証キー';
