/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE assemblestate_history ADD safetystateinfoid int;
ALTER TABLE assemblestate_history ADD comment text;
ALTER TABLE assemblestate_history ADD loginstatetus boolean default false;
COMMENT ON COLUMN assemblestate_history.safetystateinfoid IS '安否応答状況';
COMMENT ON COLUMN assemblestate_history.comment IS 'コメント';
COMMENT ON COLUMN assemblestate_history.loginstatetus IS 'ログイン状態';

CREATE TABLE oauthconsumer_data(
  id bigserial NOT NULL,
  localgovinfoid bigint NOT NULL,
  applicationname text NOT NULL,
  consumerkey varchar(256) NOT NULL,
  consumerkeysecret varchar(256) NOT NULL,
  CONSTRAINT oauthconsumer_data_pkey PRIMARY KEY (id)
);
ALTER TABLE oauthconsumer_data OWNER TO postgres;
COMMENT ON TABLE oauthconsumer_data IS 'OAuthコンシューマデータ';
COMMENT ON COLUMN oauthconsumer_data.id IS 'ID';
COMMENT ON COLUMN oauthconsumer_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN oauthconsumer_data.consumerkey IS 'コンシューマキー';
COMMENT ON COLUMN oauthconsumer_data.consumerkeysecret IS 'コンシューマー秘密鍵';
