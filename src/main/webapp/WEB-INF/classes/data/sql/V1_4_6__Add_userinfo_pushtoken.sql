/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
-- ユーザ情報
ALTER TABLE user_info ADD COLUMN pushtoken text;
COMMENT ON COLUMN user_info.pushtoken IS 'Push通知用トークン';
