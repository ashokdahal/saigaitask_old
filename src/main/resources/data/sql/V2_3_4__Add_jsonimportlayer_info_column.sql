
ALTER TABLE jsonimportlayer_info ADD COLUMN receptiondatetimeattr text;
COMMENT ON COLUMN jsonimportlayer_info.receptiondatetimeattr  IS '��M��������';
COMMENT ON COLUMN jsonimportlayer_info.subjectattr  IS 'Subject����';

COMMENT ON COLUMN jsonimportapi_info.url IS 'URL';
COMMENT ON COLUMN jsonimportapi_info.authkey  IS '�F�؃L�[';
