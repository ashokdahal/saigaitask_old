
--paging settings

ALTER TABLE tablemaster_info add column paging boolean default true;
COMMENT ON COLUMN tablemaster_info.paging  IS 'ページング';

-- 多言語化対応
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Paging','Paging');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Paging','ページング');

