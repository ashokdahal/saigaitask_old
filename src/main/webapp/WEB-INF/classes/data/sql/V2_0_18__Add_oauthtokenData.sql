/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- OAuthトークン生成時のキーに、班IDだけでなく課IDも対応出来るようにカラム追加
ALTER TABLE oauthtoken_data ADD COLUMN unitid bigint;
COMMENT ON COLUMN oauthtoken_data.unitid IS '課ID';
