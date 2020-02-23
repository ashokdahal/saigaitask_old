/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */


--被災写真投稿レイヤ情報
create table postingphotolayer_info (
	id bigserial not null primary key, 
	localgovinfoid bigint,
	tablemasterinfoid bigint,
	comment_attrid text,
	group_attrid text,
	name_attrid text,
	contact_attrid text,
	time_attrid text,
	direction_attrid text,
	height_attrid text,
	flag_attrid text,
	copytablemasterinfoid bigint,
	copycomment_attrid text,
	copygroup_attrid text,
	copyname_attrid text,
	copycontact_attrid text,
	copytime_attrid text,
	copydirection_attrid text,
	copyheight_attrid text,
	maximagewidth int
);


COMMENT ON TABLE  postingphotolayer_info IS '被災写真投稿レイヤ情報';
COMMENT ON COLUMN postingphotolayer_info.id IS 'ID';
COMMENT ON COLUMN postingphotolayer_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN postingphotolayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN postingphotolayer_info.comment_attrid IS 'コメント属性ID';
COMMENT ON COLUMN postingphotolayer_info.group_attrid IS '所属班属性ID';
COMMENT ON COLUMN postingphotolayer_info.name_attrid IS '送信者氏名属性ID';
COMMENT ON COLUMN postingphotolayer_info.contact_attrid IS '連絡先属性ID';
COMMENT ON COLUMN postingphotolayer_info.time_attrid IS '撮影時刻属性ID';
COMMENT ON COLUMN postingphotolayer_info.direction_attrid IS '方位属性ID';
COMMENT ON COLUMN postingphotolayer_info.height_attrid IS '高度属性ID';
COMMENT ON COLUMN postingphotolayer_info.flag_attrid IS '災害フラグ属性ID';
COMMENT ON COLUMN postingphotolayer_info.copytablemasterinfoid IS 'コピー先テーブルID';
COMMENT ON COLUMN postingphotolayer_info.copycomment_attrid IS 'コピー先コメント属性ID';
COMMENT ON COLUMN postingphotolayer_info.copygroup_attrid IS 'コピー先所属班属性ID';
COMMENT ON COLUMN postingphotolayer_info.copyname_attrid IS 'コピー先送信者氏名属性ID';
COMMENT ON COLUMN postingphotolayer_info.copycontact_attrid IS 'コピー先連絡先属性ID';
COMMENT ON COLUMN postingphotolayer_info.copytime_attrid IS 'コピー先撮影時刻属性ID';
COMMENT ON COLUMN postingphotolayer_info.copydirection_attrid IS 'コピー先方位属性ID';
COMMENT ON COLUMN postingphotolayer_info.copyheight_attrid IS 'コピー先高度属性ID';
COMMENT ON COLUMN postingphotolayer_info.maximagewidth IS '表示画像幅（高さ）';


--被災写真投稿振り分けデータ
create table postingphotolayer_data (
	id bigserial not null primary key, 
	postingphotolayerinfoid bigint,
	layerid text,
	photogid bigint,
	copytrackdataid bigint,
	copylayerid text,
	copygid bigint,
	copytime timestamp
);
create index postingphotolayer_data_postingphotolayerinfoid_idx
	on postingphotolayer_data (postingphotolayerinfoid);

COMMENT ON TABLE  postingphotolayer_data IS '被災写真投稿振り分けデータ';
COMMENT ON COLUMN postingphotolayer_data.id IS 'ID';
COMMENT ON COLUMN postingphotolayer_data.postingphotolayerinfoid IS '被災写真投稿レイヤ情報ID';
COMMENT ON COLUMN postingphotolayer_data.layerid IS 'レイヤID';
COMMENT ON COLUMN postingphotolayer_data.photogid IS 'フィーチャID';
COMMENT ON COLUMN postingphotolayer_data.copytrackdataid IS 'コピー先記録ID';
COMMENT ON COLUMN postingphotolayer_data.copylayerid IS 'コピー先レイヤID';
COMMENT ON COLUMN postingphotolayer_data.copygid IS 'コピー先フィーチャID';
COMMENT ON COLUMN postingphotolayer_data.copytime IS 'コピー時間';


--振り分けメニュー
insert into menutype_master values(25, '投稿写真振り分け', 25);

