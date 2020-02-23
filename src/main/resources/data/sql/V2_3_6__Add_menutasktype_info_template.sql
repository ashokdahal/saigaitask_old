
--API Key

ALTER TABLE menutasktype_info add column template integer;
COMMENT ON COLUMN menutasktype_info.template  IS 'テンプレートフラグ';


-- 多言語化対応
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Template flag','Template flag');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Template flag','テンプレートフラグ');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Not template','Not template');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Not template','非テンプレート');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Template(Parent)','Template(Parent)');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Template(Parent)','テンプレート（親）');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Template(Child)','Template(Child)');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Template(Child)','テンプレート（子）');
