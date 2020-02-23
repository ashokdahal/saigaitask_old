/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

--  公共情報コモンズ発信先情報
COMMENT ON TABLE publiccommons_send_to_info IS '公共情報コモンズ発信先情報';
ALTER TABLE publiccommons_send_to_info ADD COLUMN endpoint_url_backup text; 
COMMENT ON COLUMN publiccommons_send_to_info.endpoint_url_backup IS 'エンドポイントURLバックアップノード';
ALTER TABLE publiccommons_send_to_info ADD COLUMN username_backup text; 
COMMENT ON COLUMN publiccommons_send_to_info.username_backup IS 'ユーザ名バックアップノード';
ALTER TABLE publiccommons_send_to_info ADD COLUMN password_backup text; 
COMMENT ON COLUMN publiccommons_send_to_info.password_backup IS 'パスワードバックアップノード';
