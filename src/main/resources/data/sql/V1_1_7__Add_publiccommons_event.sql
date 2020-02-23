/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- お知らせ情報
CREATE TABLE publiccommons_report_data_last_general
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 記録データID
  chikuname text, -- 地区名
  area text, -- 場所
  title text, -- タイトル
  text text, -- 本文
  notificationuri text, -- 告知URI
  fileuri text, -- ファイルURI
  mediatype text, -- メディアタイプ
  documentid text, -- ドキュメントID
  division text, -- 分類
  localgovinfoid bigint, -- 自治体ID
  documentrevision bigint, -- 版数
  disasterinformationtype text, -- 情報識別区分
  validdatetime text, -- 希望公開終了日時
  distributiontype text, -- 更新種別
  filecaption text, -- ファイルタイトル
  emailtitle text, -- メールタイトル
  content text, -- 送信本文
  CONSTRAINT publiccommons_report_data_last_general_information_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_general
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_general
  IS '公共情報コモンズお知らせ情報最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_general.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_general.pcommonsreportdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data_last_general.chikuname IS '地区名';
COMMENT ON COLUMN publiccommons_report_data_last_general.area IS '場所';
COMMENT ON COLUMN publiccommons_report_data_last_general.title IS 'タイトル';
COMMENT ON COLUMN publiccommons_report_data_last_general.text IS '本文';
COMMENT ON COLUMN publiccommons_report_data_last_general.notificationuri IS '告知URI';
COMMENT ON COLUMN publiccommons_report_data_last_general.fileuri IS 'ファイルURI';
COMMENT ON COLUMN publiccommons_report_data_last_general.mediatype IS 'メディアタイプ';
COMMENT ON COLUMN publiccommons_report_data_last_general.documentid IS 'ドキュメントID';
COMMENT ON COLUMN publiccommons_report_data_last_general.division IS '分類';
COMMENT ON COLUMN publiccommons_report_data_last_general.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_data_last_general.documentrevision IS '版数';
COMMENT ON COLUMN publiccommons_report_data_last_general.disasterinformationtype IS '情報識別区分';
COMMENT ON COLUMN publiccommons_report_data_last_general.validdatetime IS '希望公開終了日時';
COMMENT ON COLUMN publiccommons_report_data_last_general.distributiontype IS '更新種別';
COMMENT ON COLUMN publiccommons_report_data_last_general.filecaption IS 'ファイルタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_general.emailtitle IS 'メールタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_general.content IS '送信本文';

-- イベント情報
CREATE TABLE publiccommons_report_data_last_event
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 記録データID
  chikuname text, -- 地区名
  area text, -- 場所
  title text, -- タイトル
  text text, -- 本文
  notificationuri text, -- 告知URI
  fileuri text, -- ファイルURI
  mediatype text, -- メディアタイプ
  documentid text, -- ドキュメントID
  division text, -- 分類
  localgovinfoid bigint, -- 自治体ID
  documentrevision bigint, -- 版数
  disasterinformationtype text, -- 情報識別区分
  validdatetime text, -- 希望公開終了日時
  distributiontype text, -- 更新種別
  personresponsible text, -- 担当者名
  phone text, -- 電話番号
  fax text, -- FAX
  email text, -- メールアドレス
  officename text, -- 部署名
  officenamekana text, -- 部署名(カナ)
  officelocationarea text, -- 部署住所
  eventfrom text, -- 開催開始日時
  eventto text, -- 開催終了日時
  eventfee text, -- 参加料金
  filecaption text, -- ファイルタイトル
  emailtitle text, -- メールタイトル
  content text, -- 送信本文
  CONSTRAINT publiccommons_report_data_last_event_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_event
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_event
  IS '公共情報コモンズイベント情報最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_event.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_event.pcommonsreportdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data_last_event.chikuname IS '地区名';
COMMENT ON COLUMN publiccommons_report_data_last_event.area IS '場所';
COMMENT ON COLUMN publiccommons_report_data_last_event.title IS 'タイトル';
COMMENT ON COLUMN publiccommons_report_data_last_event.text IS '本文';
COMMENT ON COLUMN publiccommons_report_data_last_event.notificationuri IS '告知URI';
COMMENT ON COLUMN publiccommons_report_data_last_event.fileuri IS 'ファイルURI';
COMMENT ON COLUMN publiccommons_report_data_last_event.mediatype IS 'メディアタイプ';
COMMENT ON COLUMN publiccommons_report_data_last_event.documentid IS 'ドキュメントID';
COMMENT ON COLUMN publiccommons_report_data_last_event.division IS '分類';
COMMENT ON COLUMN publiccommons_report_data_last_event.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_data_last_event.documentrevision IS '版数';
COMMENT ON COLUMN publiccommons_report_data_last_event.disasterinformationtype IS '情報識別区分';
COMMENT ON COLUMN publiccommons_report_data_last_event.validdatetime IS '希望公開終了日時';
COMMENT ON COLUMN publiccommons_report_data_last_event.distributiontype IS '更新種別';
COMMENT ON COLUMN publiccommons_report_data_last_event.personresponsible IS '担当者名';
COMMENT ON COLUMN publiccommons_report_data_last_event.phone IS '電話番号';
COMMENT ON COLUMN publiccommons_report_data_last_event.fax IS 'FAX';
COMMENT ON COLUMN publiccommons_report_data_last_event.email IS 'メールアドレス';
COMMENT ON COLUMN publiccommons_report_data_last_event.officename IS '部署名';
COMMENT ON COLUMN publiccommons_report_data_last_event.officenamekana IS '部署名(カナ)';
COMMENT ON COLUMN publiccommons_report_data_last_event.officelocationarea IS '部署住所';
COMMENT ON COLUMN publiccommons_report_data_last_event.eventfrom IS '開催開始日時';
COMMENT ON COLUMN publiccommons_report_data_last_event.eventto IS '開催終了日時';
COMMENT ON COLUMN publiccommons_report_data_last_event.eventfee IS '参加料金';
COMMENT ON COLUMN publiccommons_report_data_last_event.filecaption IS 'ファイルタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_event.emailtitle IS 'メールタイトル';
COMMENT ON COLUMN publiccommons_report_data_last_event.content IS '送信本文';

-- 避難勧告・指示情報
ALTER TABLE publiccommons_report_data_last_refuge ADD COLUMN issueorlift text;
COMMENT ON COLUMN publiccommons_report_data_last_refuge.issueorlift IS '発令or解除';







--
-- DBの変更、追加等 11/18 trunk rev.2733(自治体版)
--


-- ■group_info
ALTER TABLE group_info ADD namekana text;
ALTER TABLE group_info ADD phone text;
ALTER TABLE group_info ADD fax text;
ALTER TABLE group_info ADD email text;
ALTER TABLE group_info ADD address text;
ALTER TABLE group_info ADD domain text;
COMMENT ON COLUMN group_info.namekana IS '班名カナ(コモンズ発信作成組織情報)';
COMMENT ON COLUMN group_info.phone IS '電話番号(コモンズ発信作成組織情報)';
COMMENT ON COLUMN group_info.fax IS 'FAX番号(コモンズ発信作成組織情報)';
COMMENT ON COLUMN group_info.email IS 'Eメールアドレス(コモンズ発信作成組織情報)';
COMMENT ON COLUMN group_info.address IS '住所(コモンズ発信作成組織情報)';
COMMENT ON COLUMN group_info.domain IS 'ドメイン(コモンズ発信作成組織情報)';



-- ■localgov_info
ALTER TABLE localgov_info DROP COLUMN groupinfoname;
ALTER TABLE localgov_info ADD autostartgroupinfoid bigint;
COMMENT ON COLUMN localgov_info.autostartgroupinfoid IS '自動発報時の班ID(コモンズ発信作成組織情報)';



-- ■publiccommons_report_data_last_damage
CREATE TABLE publiccommons_report_data_last_damage
(
  id bigserial NOT NULL, -- ID
  pcommonsreportdataid bigint, -- 公共情報コモンズ発信データID
  remarks text, -- 備考
  deadpeople text, -- 死者
  missingpeople text, -- 行方不明者数
  seriouslyinjuredpeople text, -- 負傷者 重傷
  slightlyinjuredpeople text, -- 負傷者 軽傷
  totalcollapsebuilding text, -- 全壊 棟
  totalcollapsehousehold text, -- 全壊 世帯
  totalcollapsehuman text, -- 全壊 人
  halfcollapsebuilding text, -- 半壊 棟
  halfcollapsehousehold text, -- 半壊 世帯
  halfcollapsehuman text, -- 半壊 人
  somecollapsebuilding text, -- 一部破壊 棟
  somecollapsehousehold text, -- 一部破壊 世帯
  somecollapsehuman text, -- 一部破壊 人
  overinundationbuilding text, -- 床上浸水 棟
  overinundationhousehold text, -- 床上浸水 世帯
  overinundationhuman text, -- 床上浸水 人
  underinundationbuilding text, -- 床下浸水 棟
  underinundationhousehold text, -- 床下浸水 世帯
  underinundationhuman text, -- 床下浸水 人
  publicbuilding text, -- 公共建物 棟
  otherbuilding text, -- その他 棟
  ricefieldoutflowburied text, -- 田_流出埋没
  ricefieldflood text, -- 田_冠水
  fieldoutflowburied text, -- 畑_流出埋没
  fieldflood text, -- 畑_冠水
  educationalfacilities text, -- 文教施設
  hospital text, -- 病院
  road text, -- 道路
  bridge text, -- 橋りょう
  river text, -- 河川
  port text, -- 港湾
  sedimentcontrol text, -- 砂防
  cleaningfacility text, -- 清掃施設
  cliffcollapse text, -- 崖崩れ
  railwayinterruption text, -- 鉄道不通
  ship text, -- 被害船舶
  water text, -- 水道
  phone text, -- 電話
  electric text, -- 電気
  gas text, -- ガス
  blockwalls_etc text, -- ブロック塀等
  suffererhousehold text, -- り災世帯数
  suffererhuman text, -- り災者数
  firebuilding text, -- 火災 建物
  firedangerousgoods text, -- 火災 危険物
  otherfire text, -- 火災 その他
  publicscoolfacillities text, -- 公共文教施設
  agriculturefacilities text, -- 農林水産業施設
  publicengineeringfacilities text, -- 公共土木施設
  subtotaldamagefacilities text, -- 施設被害小計
  farmingdamage text, -- 農業被害
  forestrydamage text, -- 林業被害
  animaldamage text, -- 畜産被害
  fisheriesdamage text, -- 水産被害
  commerceandindustrydamage text, -- 商工被害
  otherdamageother text, -- その他被害 その他
  totaldamage text, -- 被害総計
  schoolmount text, -- 文教被害額
  farmmount text, -- 農林被害額
  subtotalotherdamage text, -- その他被害小計
  fireman1 text, -- 消防職員出動延人数
  fireman2 text, -- 消防団員出動延人数
  CONSTRAINT publiccommons_report_data_last_damage_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE publiccommons_report_data_last_damage
  OWNER TO postgres;
COMMENT ON TABLE publiccommons_report_data_last_damage
  IS '公共情報コモンズ被害情報最終送信履歴管理テーブル';
COMMENT ON COLUMN publiccommons_report_data_last_damage.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data_last_damage.pcommonsreportdataid IS '公共情報コモンズ発信データID';
COMMENT ON COLUMN publiccommons_report_data_last_damage.remarks IS '備考';
COMMENT ON COLUMN publiccommons_report_data_last_damage.deadpeople IS '死者';
COMMENT ON COLUMN publiccommons_report_data_last_damage.missingpeople IS '行方不明者数';
COMMENT ON COLUMN publiccommons_report_data_last_damage.seriouslyinjuredpeople IS '負傷者 重傷';
COMMENT ON COLUMN publiccommons_report_data_last_damage.slightlyinjuredpeople IS '負傷者 軽傷';
COMMENT ON COLUMN publiccommons_report_data_last_damage.totalcollapsebuilding IS '全壊 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.totalcollapsehousehold IS '全壊 世帯';
COMMENT ON COLUMN publiccommons_report_data_last_damage.totalcollapsehuman IS '全壊 人';
COMMENT ON COLUMN publiccommons_report_data_last_damage.halfcollapsebuilding IS '半壊 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.halfcollapsehousehold IS '半壊 世帯';
COMMENT ON COLUMN publiccommons_report_data_last_damage.halfcollapsehuman IS '半壊 人';
COMMENT ON COLUMN publiccommons_report_data_last_damage.somecollapsebuilding IS '一部破壊 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.somecollapsehousehold IS '一部破壊 世帯';
COMMENT ON COLUMN publiccommons_report_data_last_damage.somecollapsehuman IS '一部破壊 人';
COMMENT ON COLUMN publiccommons_report_data_last_damage.overinundationbuilding IS '床上浸水 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.overinundationhousehold IS '床上浸水 世帯';
COMMENT ON COLUMN publiccommons_report_data_last_damage.overinundationhuman IS '床上浸水 人';
COMMENT ON COLUMN publiccommons_report_data_last_damage.underinundationbuilding IS '床下浸水 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.underinundationhousehold IS '床下浸水 世帯';
COMMENT ON COLUMN publiccommons_report_data_last_damage.underinundationhuman IS '床下浸水 人';
COMMENT ON COLUMN publiccommons_report_data_last_damage.publicbuilding IS '公共建物 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.otherbuilding IS 'その他 棟';
COMMENT ON COLUMN publiccommons_report_data_last_damage.ricefieldoutflowburied IS '田_流出埋没';
COMMENT ON COLUMN publiccommons_report_data_last_damage.ricefieldflood IS '田_冠水';
COMMENT ON COLUMN publiccommons_report_data_last_damage.fieldoutflowburied IS '畑_流出埋没';
COMMENT ON COLUMN publiccommons_report_data_last_damage.fieldflood IS '畑_冠水';
COMMENT ON COLUMN publiccommons_report_data_last_damage.educationalfacilities IS '文教施設';
COMMENT ON COLUMN publiccommons_report_data_last_damage.hospital IS '病院';
COMMENT ON COLUMN publiccommons_report_data_last_damage.road IS '道路';
COMMENT ON COLUMN publiccommons_report_data_last_damage.bridge IS '橋りょう';
COMMENT ON COLUMN publiccommons_report_data_last_damage.river IS '河川';
COMMENT ON COLUMN publiccommons_report_data_last_damage.port IS '港湾';
COMMENT ON COLUMN publiccommons_report_data_last_damage.sedimentcontrol IS '砂防';
COMMENT ON COLUMN publiccommons_report_data_last_damage.cleaningfacility IS '清掃施設';
COMMENT ON COLUMN publiccommons_report_data_last_damage.cliffcollapse IS '崖崩れ';
COMMENT ON COLUMN publiccommons_report_data_last_damage.railwayinterruption IS '鉄道不通';
COMMENT ON COLUMN publiccommons_report_data_last_damage.ship IS '被害船舶';
COMMENT ON COLUMN publiccommons_report_data_last_damage.water IS '水道';
COMMENT ON COLUMN publiccommons_report_data_last_damage.phone IS '電話';
COMMENT ON COLUMN publiccommons_report_data_last_damage.electric IS '電気';
COMMENT ON COLUMN publiccommons_report_data_last_damage.gas IS 'ガス';
COMMENT ON COLUMN publiccommons_report_data_last_damage.blockwalls_etc IS 'ブロック塀等';
COMMENT ON COLUMN publiccommons_report_data_last_damage.suffererhousehold IS 'り災世帯数';
COMMENT ON COLUMN publiccommons_report_data_last_damage.suffererhuman IS 'り災者数';
COMMENT ON COLUMN publiccommons_report_data_last_damage.firebuilding IS '火災 建物';
COMMENT ON COLUMN publiccommons_report_data_last_damage.firedangerousgoods IS '火災 危険物';
COMMENT ON COLUMN publiccommons_report_data_last_damage.otherfire IS '火災 その他';
COMMENT ON COLUMN publiccommons_report_data_last_damage.publicscoolfacillities IS '公共文教施設';
COMMENT ON COLUMN publiccommons_report_data_last_damage.agriculturefacilities IS '農林水産業施設';
COMMENT ON COLUMN publiccommons_report_data_last_damage.publicengineeringfacilities IS '公共土木施設';
COMMENT ON COLUMN publiccommons_report_data_last_damage.subtotaldamagefacilities IS '施設被害小計';
COMMENT ON COLUMN publiccommons_report_data_last_damage.farmingdamage IS '農業被害';
COMMENT ON COLUMN publiccommons_report_data_last_damage.forestrydamage IS '林業被害';
COMMENT ON COLUMN publiccommons_report_data_last_damage.animaldamage IS '畜産被害';
COMMENT ON COLUMN publiccommons_report_data_last_damage.fisheriesdamage IS '水産被害';
COMMENT ON COLUMN publiccommons_report_data_last_damage.commerceandindustrydamage IS '商工被害';
COMMENT ON COLUMN publiccommons_report_data_last_damage.otherdamageother IS 'その他被害 その他';
COMMENT ON COLUMN publiccommons_report_data_last_damage.totaldamage IS '被害総計';
COMMENT ON COLUMN publiccommons_report_data_last_damage.schoolmount IS '文教被害額';
COMMENT ON COLUMN publiccommons_report_data_last_damage.farmmount IS '農林被害額';
COMMENT ON COLUMN publiccommons_report_data_last_damage.subtotalotherdamage IS 'その他被害小計';
COMMENT ON COLUMN publiccommons_report_data_last_damage.fireman1 IS '消防職員出動延人数';
COMMENT ON COLUMN publiccommons_report_data_last_damage.fireman2 IS '消防団員出動延人数';





-- ■publiccommons_report_shelter_info
ALTER TABLE publiccommons_report_shelter_info ADD attrtype text;
ALTER TABLE publiccommons_report_shelter_info ADD attrcircle text;
ALTER TABLE publiccommons_report_shelter_info ADD attrheadcount text;
ALTER TABLE publiccommons_report_shelter_info ADD attrheadcountvoluntary text;
ALTER TABLE publiccommons_report_shelter_info ADD attrhouseholds text;
ALTER TABLE publiccommons_report_shelter_info ADD attrhouseholdsvoluntary text;

COMMENT ON COLUMN publiccommons_report_shelter_info.attrtype IS '種別';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrcircle IS '座標';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrheadcount IS '避難人数';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrheadcountvoluntary IS '避難人数(うち自主避難)';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrhouseholds IS '避難世帯数';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrhouseholdsvoluntary IS '避難世帯数(うち自主避難)';

-- #管理画面で追加項目に属性を入力した後に、以下を実行してください。
-- 
-- ALTER TABLE publiccommons_report_shelter_info ALTER COLUMN attrtype SET NOT NULL;
-- ALTER TABLE publiccommons_report_shelter_info ALTER COLUMN attrcircle SET NOT NULL;
-- ALTER TABLE publiccommons_report_shelter_info ALTER COLUMN attrheadcount SET NOT NULL;
-- ALTER TABLE publiccommons_report_shelter_info ALTER COLUMN attrheadcountvoluntary SET NOT NULL;
-- ALTER TABLE publiccommons_report_shelter_info ALTER COLUMN attrhouseholds SET NOT NULL;
-- ALTER TABLE publiccommons_report_shelter_info ALTER COLUMN attrhouseholdsvoluntary SET NOT NULL;



-- ■publiccommons_report_data_last_shelter
ALTER TABLE publiccommons_report_data_last_shelter DROP COLUMN shelter_last_cancel;
ALTER TABLE publiccommons_report_data_last_shelter ADD circle text;
ALTER TABLE publiccommons_report_data_last_shelter ADD type text;
ALTER TABLE publiccommons_report_data_last_shelter ADD typedetail text;
ALTER TABLE publiccommons_report_data_last_shelter ADD headcount text;
ALTER TABLE publiccommons_report_data_last_shelter ADD headcountvoluntary text;
ALTER TABLE publiccommons_report_data_last_shelter ADD households text;
ALTER TABLE publiccommons_report_data_last_shelter ADD householdsvoluntary text;
COMMENT ON COLUMN publiccommons_report_data_last_shelter.circle IS '座標';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.type IS '種別';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.typedetail IS '種別詳細';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.headcount IS '避難人数';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.headcountvoluntary IS '避難人数(うち自主避難)';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.households IS '避難世帯数';
COMMENT ON COLUMN publiccommons_report_data_last_shelter.householdsvoluntary IS '避難世帯数(うち自主避難)';


-- ■publiccommons_report_data_last_event
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN personresponsible;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN phone;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN fax;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN email;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN officename;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN officenamekana;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN officelocationarea;
ALTER TABLE publiccommons_report_data_last_event DROP COLUMN chikuname;
ALTER TABLE publiccommons_report_data_last_event ADD mimetype text;
COMMENT ON COLUMN publiccommons_report_data_last_event.mimetype IS 'MIMEタイプ';



-- ■publiccommons_report_data_last_general
ALTER TABLE publiccommons_report_data_last_general DROP COLUMN chikuname;
ALTER TABLE publiccommons_report_data_last_general ADD mimetype text;
COMMENT ON COLUMN publiccommons_report_data_last_general.mimetype IS 'MIMEタイプ';



-- ■publiccommons_report_data_last_refuge
ALTER TABLE publiccommons_report_data_last_refuge DROP COLUMN hatureiKbn_last;
ALTER TABLE publiccommons_report_data_last_refuge ADD hatureiKbn text;
COMMENT ON COLUMN publiccommons_report_data_last_refuge.hatureiKbn IS '発令状況';



-- ■publiccommons_report_data
ALTER TABLE publiccommons_report_data ADD complementaryinfo text;
ALTER TABLE publiccommons_report_data ADD personresponsible text;
ALTER TABLE publiccommons_report_data ADD organizationname text;
ALTER TABLE publiccommons_report_data ADD organizationcode text;
ALTER TABLE publiccommons_report_data ADD organizationdomainname text;
ALTER TABLE publiccommons_report_data ADD officename text;
ALTER TABLE publiccommons_report_data ADD officenamekana text;
ALTER TABLE publiccommons_report_data ADD officelocationarea text;
ALTER TABLE publiccommons_report_data ADD phone text;
ALTER TABLE publiccommons_report_data ADD fax text;
ALTER TABLE publiccommons_report_data ADD email text;
ALTER TABLE publiccommons_report_data ADD officedomainname text;
ALTER TABLE publiccommons_report_data ADD organizationnameeditorial text;
ALTER TABLE publiccommons_report_data ADD organizationcodeeditorial text;
ALTER TABLE publiccommons_report_data ADD organizationdomainnameeditorial text;
ALTER TABLE publiccommons_report_data ADD officenameeditorial text;
ALTER TABLE publiccommons_report_data ADD officenamekanaeditorial text;
ALTER TABLE publiccommons_report_data ADD officelocationareaeditorial text;
ALTER TABLE publiccommons_report_data ADD phoneeditorial text;
ALTER TABLE publiccommons_report_data ADD faxeditorial text;
ALTER TABLE publiccommons_report_data ADD emaileditorial text;
ALTER TABLE publiccommons_report_data ADD officedomainnameeditorial text;
ALTER TABLE publiccommons_report_data ADD targetdatetime timestamp without time zone;
COMMENT ON COLUMN publiccommons_report_data.complementaryinfo IS '補足情報';
COMMENT ON COLUMN publiccommons_report_data.personresponsible IS '発表組織 担当者';
COMMENT ON COLUMN publiccommons_report_data.organizationname IS '発表組織 組織名';
COMMENT ON COLUMN publiccommons_report_data.organizationcode IS '発表組織 地方公共団体コード';
COMMENT ON COLUMN publiccommons_report_data.organizationdomainname IS '発表組織 組織ドメイン';
COMMENT ON COLUMN publiccommons_report_data.officename IS '発表組織 部署名';
COMMENT ON COLUMN publiccommons_report_data.officenamekana IS '発表組織 部署名(カナ)';
COMMENT ON COLUMN publiccommons_report_data.officelocationarea IS '発表組織 部署住所';
COMMENT ON COLUMN publiccommons_report_data.phone IS '発表組織 部署電話番号';
COMMENT ON COLUMN publiccommons_report_data.fax IS '発表組織 部署FAX番号';
COMMENT ON COLUMN publiccommons_report_data.email IS '発表組織 部署メールアドレス';
COMMENT ON COLUMN publiccommons_report_data.officedomainname IS '発表組織 部署ドメイン';
COMMENT ON COLUMN publiccommons_report_data.organizationnameeditorial IS '作成組織 組織名';
COMMENT ON COLUMN publiccommons_report_data.organizationcodeeditorial IS '作成組織 地方公共団体コード';
COMMENT ON COLUMN publiccommons_report_data.organizationdomainnameeditorial IS '作成組織 組織ドメイン';
COMMENT ON COLUMN publiccommons_report_data.officenameeditorial IS '作成組織 部署名';
COMMENT ON COLUMN publiccommons_report_data.officenamekanaeditorial IS '作成組織 部署名(カナ)';
COMMENT ON COLUMN publiccommons_report_data.officelocationareaeditorial IS '作成組織 部署住所';
COMMENT ON COLUMN publiccommons_report_data.phoneeditorial IS '作成組織 部署電話番号';
COMMENT ON COLUMN publiccommons_report_data.faxeditorial IS '作成組織 部署FAX番号';
COMMENT ON COLUMN publiccommons_report_data.emaileditorial IS '作成組織 部署メールアドレス';
COMMENT ON COLUMN publiccommons_report_data.officedomainnameeditorial IS '作成組織 部署ドメイン';
COMMENT ON COLUMN publiccommons_report_data.targetdatetime IS '希望公開開始日時';


-- rev.2744
ALTER TABLE publiccommons_send_to_info DROP COLUMN document_valid_hour;
