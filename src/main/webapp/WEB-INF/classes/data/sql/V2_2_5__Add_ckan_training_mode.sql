
ALTER TABLE ckanauth_info add column trainingauthkey text;
COMMENT ON COLUMN ckanauth_info.trainingauthkey  IS '訓練用CKAN認証キー';

ALTER TABLE ckanmetadata_info add column layerid text;
COMMENT ON COLUMN ckanmetadata_info.layerid  IS 'レイヤID';
