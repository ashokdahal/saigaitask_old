/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

create table disastersummaryhistory_data (
  ID bigserial not null
  , localgovinfoid bigint not null
  , trackdataid bigint not null
  , period timestamp not null
  , usertime text
  , areanum integer not null
  , area1 text
  , area2 text
  , area3 text
  , area4 text
  , area5 text
  , area6 text
  , area7 text
  , area8 text
  , area9 text
  , area10 text
  , area11 text
  , area12 text
  , area13 text
  , area14 text
  , area15 text
  , area16 text
  , area17 text
  , area18 text
  , area19 text
  , area20 text
  , constraint disastersummaryhistory_data_pkc primary key (ID)
) ;
COMMENT ON TABLE disastersummaryhistory_data IS '被災集計履歴データ';
COMMENT ON COLUMN disastersummaryhistory_data.id IS 'ID';
COMMENT ON COLUMN disastersummaryhistory_data.localgovinfoid IS '自冶体ID';
COMMENT ON COLUMN disastersummaryhistory_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disastersummaryhistory_data.period IS '期間';
COMMENT ON COLUMN disastersummaryhistory_data.usertime IS 'ユーザ日時';
COMMENT ON COLUMN disastersummaryhistory_data.areanum IS '地区数';
COMMENT ON COLUMN disastersummaryhistory_data.area1 IS '地区１';
COMMENT ON COLUMN disastersummaryhistory_data.area2 IS '地区２';
COMMENT ON COLUMN disastersummaryhistory_data.area3 IS '地区３';
COMMENT ON COLUMN disastersummaryhistory_data.area4 IS '地区４';
COMMENT ON COLUMN disastersummaryhistory_data.area5 IS '地区５';
COMMENT ON COLUMN disastersummaryhistory_data.area6 IS '地区６';
COMMENT ON COLUMN disastersummaryhistory_data.area7 IS '地区７';
COMMENT ON COLUMN disastersummaryhistory_data.area8 IS '地区８';
COMMENT ON COLUMN disastersummaryhistory_data.area9 IS '地区９';
COMMENT ON COLUMN disastersummaryhistory_data.area10 IS '地区１０';
COMMENT ON COLUMN disastersummaryhistory_data.area11 IS '地区１１';
COMMENT ON COLUMN disastersummaryhistory_data.area12 IS '地区１２';
COMMENT ON COLUMN disastersummaryhistory_data.area13 IS '地区１３';
COMMENT ON COLUMN disastersummaryhistory_data.area14 IS '地区１４';
COMMENT ON COLUMN disastersummaryhistory_data.area15 IS '地区１５';
COMMENT ON COLUMN disastersummaryhistory_data.area16 IS '地区１６';
COMMENT ON COLUMN disastersummaryhistory_data.area17 IS '地区１７';
COMMENT ON COLUMN disastersummaryhistory_data.area18 IS '地区１８';
COMMENT ON COLUMN disastersummaryhistory_data.area19 IS '地区１９';
COMMENT ON COLUMN disastersummaryhistory_data.area20 IS '地区２０';

create table disastersituationhistory_data (
  id bigserial not null
  , disastersummaryhistoryid bigint not null
  , lineno integer not null
  , damageitem text
  , dispflag boolean default true not null
  , unit text
  , area1people integer default 0
  , area2people integer default 0
  , area3people integer default 0
  , area4people integer default 0
  , area5people integer default 0
  , area6people integer default 0
  , area7people integer default 0
  , area8people integer default 0
  , area9people integer default 0
  , area10people integer default 0
  , area11people integer default 0
  , area12people integer default 0
  , area13people integer default 0
  , area14people integer default 0
  , area15people integer default 0
  , area16people integer default 0
  , area17people integer default 0
  , area18people integer default 0
  , area19people integer default 0
  , area20people integer default 0
  , autototal integer default 0
  , manualtotal integer default 0
  , note text
  , constraint disastersituationhistory_data_PKC primary key (id)
) ;
COMMENT ON TABLE disastersituationhistory_data IS '被災状況履歴データ';
COMMENT ON COLUMN disastersituationhistory_data.id IS 'ID';
COMMENT ON COLUMN disastersituationhistory_data.disastersummaryhistoryid IS '被災集計履歴ID';
COMMENT ON COLUMN disastersituationhistory_data.lineno IS '行番号';
COMMENT ON COLUMN disastersituationhistory_data.damageitem IS '被害項目';
COMMENT ON COLUMN disastersituationhistory_data.dispflag IS '表示フラグ true:表示,false:非表示';
COMMENT ON COLUMN disastersituationhistory_data.unit IS '単位';
COMMENT ON COLUMN disastersituationhistory_data.area1people IS '地区１人数';
COMMENT ON COLUMN disastersituationhistory_data.area2people IS '地区２人数';
COMMENT ON COLUMN disastersituationhistory_data.area3people IS '地区３人数';
COMMENT ON COLUMN disastersituationhistory_data.area4people IS '地区４人数';
COMMENT ON COLUMN disastersituationhistory_data.area5people IS '地区５人数';
COMMENT ON COLUMN disastersituationhistory_data.area6people IS '地区６人数';
COMMENT ON COLUMN disastersituationhistory_data.area7people IS '地区７人数';
COMMENT ON COLUMN disastersituationhistory_data.area8people IS '地区８人数';
COMMENT ON COLUMN disastersituationhistory_data.area9people IS '地区９人数';
COMMENT ON COLUMN disastersituationhistory_data.area10people IS '地区１０人数';
COMMENT ON COLUMN disastersituationhistory_data.area11people IS '地区１１人数';
COMMENT ON COLUMN disastersituationhistory_data.area12people IS '地区１２人数';
COMMENT ON COLUMN disastersituationhistory_data.area13people IS '地区１３人数';
COMMENT ON COLUMN disastersituationhistory_data.area14people IS '地区１４人数';
COMMENT ON COLUMN disastersituationhistory_data.area15people IS '地区１５人数';
COMMENT ON COLUMN disastersituationhistory_data.area16people IS '地区１６人数';
COMMENT ON COLUMN disastersituationhistory_data.area17people IS '地区１７人数';
COMMENT ON COLUMN disastersituationhistory_data.area18people IS '地区１８人数';
COMMENT ON COLUMN disastersituationhistory_data.area19people IS '地区１９人数';
COMMENT ON COLUMN disastersituationhistory_data.area20people IS '地区２０人数';
COMMENT ON COLUMN disastersituationhistory_data.autototal IS '合計自動集計';
COMMENT ON COLUMN disastersituationhistory_data.manualtotal IS '合計手入力';
COMMENT ON COLUMN disastersituationhistory_data.note IS '備考';


create table disasteritem_info (
  id bigserial not null
  , localgovinfoid bigint not null
  , line integer not null
  , name text
  , constraint disasteritem_info_PKC primary key (id)
) ;

COMMENT ON TABLE disasteritem_info IS '被害項目情報';
COMMENT ON COLUMN disasteritem_info.id IS 'ID';
COMMENT ON COLUMN disasteritem_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN disasteritem_info.line IS '行番号';
COMMENT ON COLUMN disasteritem_info.name IS '項目名称';

INSERT INTO pagebutton_master VALUES (13, '過去集計', 'javascript:showHistory()', 12);
SELECT pg_catalog.setval('pagebutton_master_id_seq', 13, true);

INSERT INTO menutype_master VALUES (15, '被災集計', 15);
SELECT pg_catalog.setval('menutype_master_id_seq', 15, true);

-- 管理画面 その他
INSERT INTO adminmenu_info VALUES (826, '001010000000000000000', 'その他', 2, 1, 0, '', true);
INSERT INTO adminmenu_info VALUES (878, '001010002000000000000', '被災項目情報
', 3, 1, 0, '../disasteritemInfo', true);
