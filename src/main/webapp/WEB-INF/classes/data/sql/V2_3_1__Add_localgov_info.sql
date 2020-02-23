
--logo image file

ALTER TABLE localgov_info add column logoimagefile text;
COMMENT ON COLUMN localgov_info.logoimagefile  IS 'ロゴ画像ファイル';

-- 多言語化対応
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Logo image file','Logo image file');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Logo image file','ロゴ画像ファイル');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Logo image file upload','Logo image file upload');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Logo image file upload','ロゴ画像ファイルアップロード');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Logo image file: only image format is valid.','Logo image file: only image format is valid.');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Logo image file: only image format is valid.','画像形式のファイルのみアップロードして下さい。');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Logo image file upload complete.','Logo image file upload complete.');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Logo image file upload complete.','ロゴ画像ファイルをアップロードしました。');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Failed to upload the logo image file.','Failed to upload the logo image file.');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Failed to upload the logo image file.','ロゴ画像ファイルのアップロードに失敗しました。');


