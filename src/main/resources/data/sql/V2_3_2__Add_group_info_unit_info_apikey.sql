
--API Key

ALTER TABLE group_info add column apikey text;
COMMENT ON COLUMN group_info.apikey  IS 'APIキー';

ALTER TABLE unit_info add column apikey text;
COMMENT ON COLUMN unit_info.apikey  IS 'APIキー';

-- 多言語化対応
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'API key','API key');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Reissue','Reissue');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'API key','APIキー');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Reissue','再発行');
