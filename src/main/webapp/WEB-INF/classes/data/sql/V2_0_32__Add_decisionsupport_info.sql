/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
-- 任意の演算対象レイヤならびに演算方法を定められるようにカラム追加
ALTER TABLE decisionsupport_info ADD COLUMN calculation_method text;
ALTER TABLE decisionsupport_info ADD COLUMN buffer int;

COMMENT ON COLUMN decisionsupport_info.calculation_method IS '演算対象日数と避難所率';
COMMENT ON COLUMN decisionsupport_info.buffer IS 'バッファ(m)';

-- マスターの追加
INSERT INTO decisionsupporttype_master (name,disporder) VALUES ('ライフライン等の被害エリアレイヤ',11);
INSERT INTO en.decisionsupporttype_master (name,disporder) VALUES ('Lifeline etc area layer',11);
