/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- ログイン

COMMENT ON TABLE disaster_master IS '災害種別マスタ';
COMMENT ON COLUMN disaster_master.id IS 'ID';
COMMENT ON COLUMN disaster_master.name IS '名称';
COMMENT ON COLUMN disaster_master.disporder IS '表示順';

COMMENT ON TABLE group_info IS '班情報';
COMMENT ON COLUMN group_info.id IS 'ID';
COMMENT ON COLUMN group_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN group_info.name IS '班名';
COMMENT ON COLUMN group_info.password IS 'ログインパスワード';
COMMENT ON COLUMN group_info.ecomuser IS 'eコミマップのアカウント';
COMMENT ON COLUMN group_info.ecompass IS 'eコミマップのアカウントパスワード';
COMMENT ON COLUMN group_info.admin IS '管理権限';
COMMENT ON COLUMN group_info.headoffice IS '本部権限';
COMMENT ON COLUMN group_info.note IS '備考';
COMMENT ON COLUMN group_info.disporder IS '表示順';
COMMENT ON COLUMN group_info.valid IS '有効・無効';

COMMENT ON TABLE unit_info IS 'ユニット情報';
COMMENT ON COLUMN unit_info.id IS 'ID';
COMMENT ON COLUMN unit_info.groupid IS '班ID';
COMMENT ON COLUMN unit_info.name IS 'ユニット名';
COMMENT ON COLUMN unit_info.telno IS '代表電話番号';
COMMENT ON COLUMN unit_info.faxno IS 'FAX番号';
COMMENT ON COLUMN unit_info.email IS 'メールアドレス';
COMMENT ON COLUMN unit_info.note IS '備考';
COMMENT ON COLUMN unit_info.disporder IS '表示順';
COMMENT ON COLUMN unit_info.valid IS '有効・無効';

COMMENT ON TABLE user_info IS 'ユーザ情報';
COMMENT ON COLUMN user_info.id IS 'ID';
COMMENT ON COLUMN user_info.unitid IS 'ユニットID';
COMMENT ON COLUMN user_info.staffno IS '職員番号';
COMMENT ON COLUMN user_info.name IS '名前';
COMMENT ON COLUMN user_info.duty IS '役割';
COMMENT ON COLUMN user_info.telno IS '電話番号';
COMMENT ON COLUMN user_info.mobileno IS '携帯電話番号';
COMMENT ON COLUMN user_info.email IS 'メールアドレス';
COMMENT ON COLUMN user_info.mobilemail IS '携帯電話のメールアドレス';
COMMENT ON COLUMN user_info.note IS '備考';
COMMENT ON COLUMN user_info.disporder IS '表示順';
COMMENT ON COLUMN user_info.valid IS '有効・無効';

COMMENT ON TABLE  login_data IS 'ログイン履歴';
COMMENT ON COLUMN login_data.id IS 'ID';
COMMENT ON COLUMN login_data.trackdataid IS '記録ID';
COMMENT ON COLUMN login_data.groupid IS '班ID';
COMMENT ON COLUMN login_data.demoinfoid IS '訓練ID';
COMMENT ON COLUMN login_data.disasterid IS '災害種別マスタID';
COMMENT ON COLUMN login_data.logintime IS 'ログイン時刻';
COMMENT ON COLUMN login_data.logouttime IS 'ログアウト時刻';

COMMENT ON TABLE  track_data IS '記録データ';
COMMENT ON COLUMN track_data.id IS 'ID';
COMMENT ON COLUMN track_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN track_data.demoinfoid IS 'デモID';
COMMENT ON COLUMN track_data.disasterid IS '災害種別ID';
COMMENT ON COLUMN track_data.name IS '災害名';
COMMENT ON COLUMN track_data.note IS '備考';
COMMENT ON COLUMN track_data.starttime IS '記録開始時刻';
COMMENT ON COLUMN track_data.endtime IS '記録終了時刻';
COMMENT ON COLUMN track_data.deleted IS '削除フラグ';

COMMENT ON TABLE  demo_info IS '訓練情報';
COMMENT ON COLUMN demo_info.id IS 'ID';
COMMENT ON COLUMN demo_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN demo_info.disasterid IS '災害種別ID';
COMMENT ON COLUMN demo_info.triggerurl IS 'トリガーとなるファイルURL';
COMMENT ON COLUMN demo_info.meteorequestinfoid IS '気象情報等取得情報ID';
COMMENT ON COLUMN demo_info.name IS '訓練名称';
COMMENT ON COLUMN demo_info.note IS '備考';
COMMENT ON COLUMN demo_info.disporder IS '表示順';

COMMENT ON TABLE  localgovtype_master IS '地方自治体種別';
COMMENT ON COLUMN localgovtype_master.id IS 'ID';
COMMENT ON COLUMN localgovtype_master.name IS '名称';
COMMENT ON COLUMN localgovtype_master.disporder IS '表示順';

COMMENT ON TABLE  localgov_info IS '地方自治体情報';
COMMENT ON COLUMN localgov_info.id IS 'ID';
COMMENT ON COLUMN localgov_info.domain IS '自治体毎のドメイン';
COMMENT ON COLUMN localgov_info.systemname IS 'システム名';
COMMENT ON COLUMN localgov_info.localgovtypeid IS '自治体種別';
COMMENT ON COLUMN localgov_info.pref IS '県名';
COMMENT ON COLUMN localgov_info.prefcode IS '県コード';
COMMENT ON COLUMN localgov_info.city IS '市区町村';
COMMENT ON COLUMN localgov_info.citycode IS '市区町村コード';
COMMENT ON COLUMN localgov_info.section IS '予備（区、自治会）';
COMMENT ON COLUMN localgov_info.autostart IS '自動発報フラグ';
COMMENT ON COLUMN localgov_info.alarminterval IS 'アラームの取得間隔（秒）';
COMMENT ON COLUMN localgov_info.note IS '備考';
COMMENT ON COLUMN localgov_info.valid IS '有効・無効';


-- システム全般

COMMENT ON TABLE  menulogin_info IS 'メニュー設定情報';
COMMENT ON COLUMN menulogin_info.id IS 'ID';
COMMENT ON COLUMN menulogin_info.groupid IS '班ID';
COMMENT ON COLUMN menulogin_info.disasterid IS '災害種別';
COMMENT ON COLUMN menulogin_info.note IS '備考';
COMMENT ON COLUMN menulogin_info.valid IS '有効・無効';

COMMENT ON TABLE  menuprocess_info IS 'メニュープロセス情報';
COMMENT ON COLUMN menuprocess_info.id IS 'ID';
COMMENT ON COLUMN menuprocess_info.menulogininfoid IS 'メニュー設定情報';
COMMENT ON COLUMN menuprocess_info.name IS '名称';
COMMENT ON COLUMN menuprocess_info.visible IS '表示・非表示';
COMMENT ON COLUMN menuprocess_info.disporder IS '表示順';
COMMENT ON COLUMN menuprocess_info.note IS '備考';
COMMENT ON COLUMN menuprocess_info.valid IS '有効・無効';
COMMENT ON COLUMN menuprocess_info.important IS '重要フラグ';

COMMENT ON TABLE  menutask_info IS 'メニュータスク情報';
COMMENT ON COLUMN menutask_info.id IS 'ID';
COMMENT ON COLUMN menutask_info.menuprocessinfoid IS 'プロセスID';
COMMENT ON COLUMN menutask_info.menutasktypeinfoid IS 'タスク種別';
COMMENT ON COLUMN menutask_info.name IS '名称';
COMMENT ON COLUMN menutask_info.visible IS '表示・非表示';
COMMENT ON COLUMN menutask_info.disporder IS '表示順';
COMMENT ON COLUMN menutask_info.note IS '備考';
COMMENT ON COLUMN menutask_info.valid IS '有効・無効';
COMMENT ON COLUMN menutask_info.important IS '重要フラグ';

COMMENT ON TABLE  menutaskmenu_info IS 'タスクメニュー情報';
COMMENT ON COLUMN menutaskmenu_info.id IS 'ID';
COMMENT ON COLUMN menutaskmenu_info.menutaskinfoid IS 'タスクID';
COMMENT ON COLUMN menutaskmenu_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN menutaskmenu_info.disporder IS '表示順';
COMMENT ON COLUMN menutaskmenu_info.important IS '重要フラグ';

COMMENT ON TABLE  menutasktype_info IS 'タスク種別情報';
COMMENT ON COLUMN menutasktype_info.id IS 'ID';
COMMENT ON COLUMN menutasktype_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN menutasktype_info.name IS 'タスク種別名';
COMMENT ON COLUMN menutasktype_info.disporder IS '表示順';
COMMENT ON COLUMN menutasktype_info.note IS '備考';

COMMENT ON TABLE  menu_info IS 'メニュー情報';
COMMENT ON COLUMN menu_info.id IS 'ID';
COMMENT ON COLUMN menu_info.menutasktypeinfoid IS 'タスク種別';
COMMENT ON COLUMN menu_info.menutypeid IS 'メニュータイプ';
COMMENT ON COLUMN menu_info.name IS '名称';
COMMENT ON COLUMN menu_info.helphref IS 'オンラインヘルプ';
COMMENT ON COLUMN menu_info.filterid IS 'フィルター';
COMMENT ON COLUMN menu_info.visible IS '表示・非表示';
COMMENT ON COLUMN menu_info.note IS '備考';
COMMENT ON COLUMN menu_info.valid IS '有効・無効';

COMMENT ON TABLE  menutable_info IS 'メニューテーブル情報';
COMMENT ON COLUMN menutable_info.id IS 'ID';
COMMENT ON COLUMN menutable_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN menutable_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN menutable_info.addable IS '追加フラグ';
COMMENT ON COLUMN menutable_info.deletable IS '削除フラグ';
COMMENT ON COLUMN menutable_info.totalable IS '合計フラグ';

COMMENT ON TABLE  menutype_master IS 'メニュータイプマスタ';
COMMENT ON COLUMN menutype_master.id IS 'ID';
COMMENT ON COLUMN menutype_master.name IS '名称';
COMMENT ON COLUMN menutype_master.disporder IS '表示順';

COMMENT ON TABLE  mapmaster_info IS '地図マスター情報';
COMMENT ON COLUMN mapmaster_info.id IS 'ID';
COMMENT ON COLUMN mapmaster_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN mapmaster_info.communityid IS 'コミュニティID';
COMMENT ON COLUMN mapmaster_info.mapgroupid IS 'グループID';
COMMENT ON COLUMN mapmaster_info.mapid IS '地図ID';

COMMENT ON TABLE  tablemaster_info IS 'テーブルマスター情報';
COMMENT ON COLUMN tablemaster_info.id IS 'ID';
COMMENT ON COLUMN tablemaster_info.mapmasterinfoid IS '地図マスター情報ID';
COMMENT ON COLUMN tablemaster_info.layerid IS 'レイヤID';
COMMENT ON COLUMN tablemaster_info.tablename IS 'テーブル名';
COMMENT ON COLUMN tablemaster_info.name IS '名称';
COMMENT ON COLUMN tablemaster_info.geometrytype IS 'ジオメトリのタイプ';
COMMENT ON COLUMN tablemaster_info.copy IS 'コピーフラグ';
COMMENT ON COLUMN tablemaster_info.addresscolumn IS '住所項目名';
COMMENT ON COLUMN tablemaster_info.updatecolumn IS '更新日時項目名';
COMMENT ON COLUMN tablemaster_info.coordinatecolumn IS '座標表示目名';
COMMENT ON COLUMN tablemaster_info.mgrscolumn IS 'MRGSグリッド項目名';
COMMENT ON COLUMN tablemaster_info.mgrsdigit IS 'MRGS桁数';
COMMENT ON COLUMN tablemaster_info.note IS '備考';

COMMENT ON TABLE  tablecolumn_master IS 'テーブル項目マスタ';
COMMENT ON COLUMN tablecolumn_master.id IS 'ID';
COMMENT ON COLUMN tablecolumn_master.tablename IS 'テーブル名';
COMMENT ON COLUMN tablecolumn_master.columnname IS '項目名';
COMMENT ON COLUMN tablecolumn_master.nullable IS '空欄可';

COMMENT ON TABLE  trackmap_info IS '地図情報';
COMMENT ON COLUMN trackmap_info.id IS 'ID';
COMMENT ON COLUMN trackmap_info.trackdataid IS '記録ID';
COMMENT ON COLUMN trackmap_info.communityid IS 'コミュニティID';
COMMENT ON COLUMN trackmap_info.mapgroupid IS 'グループID';
COMMENT ON COLUMN trackmap_info.mapid IS '地図ID';

COMMENT ON TABLE  tracktable_info IS '記録テーブル情報';
COMMENT ON COLUMN tracktable_info.id IS 'ID';
COMMENT ON COLUMN tracktable_info.trackmapinfoid IS '地図情報';
COMMENT ON COLUMN tracktable_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN tracktable_info.layerid IS 'レイヤID';
COMMENT ON COLUMN tracktable_info.tablename IS 'テーブル名';

COMMENT ON TABLE  tablelistcolumn_info IS 'テーブルリスト項目情報';
COMMENT ON COLUMN tablelistcolumn_info.id IS 'ID';
COMMENT ON COLUMN tablelistcolumn_info.menutableinfoid IS 'メニューテーブルID';
COMMENT ON COLUMN tablelistcolumn_info.attrid IS 'テーブル項目名';
COMMENT ON COLUMN tablelistcolumn_info.name IS '項目名';
COMMENT ON COLUMN tablelistcolumn_info.editable IS '編集可';
COMMENT ON COLUMN tablelistcolumn_info.highlight IS '強調表示';
COMMENT ON COLUMN tablelistcolumn_info.grouping IS 'グループ化';
COMMENT ON COLUMN tablelistcolumn_info.sortable IS 'ソート可';
COMMENT ON COLUMN tablelistcolumn_info.defaultsort IS 'デフォルトソート';
COMMENT ON COLUMN tablelistcolumn_info.uploadable IS 'アップロード可';
COMMENT ON COLUMN tablelistcolumn_info.loggable IS 'ログ出力可';
COMMENT ON COLUMN tablelistcolumn_info.disporder IS '表示順';

COMMENT ON TABLE  tablerowstyle_info IS '属性行スタイル情報';
COMMENT ON COLUMN tablerowstyle_info.id IS 'ID';
COMMENT ON COLUMN tablerowstyle_info.tablelistcolumninfoid IS 'テーブルリスト項目ID';
COMMENT ON COLUMN tablerowstyle_info.val IS '値';
COMMENT ON COLUMN tablerowstyle_info.style IS 'スタイル文字列';

COMMENT ON TABLE  tablecolumnsort_info IS '属性ソート情報';
COMMENT ON COLUMN tablecolumnsort_info.id IS 'ID';
COMMENT ON COLUMN tablecolumnsort_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN tablecolumnsort_info.attrid IS 'テーブル項目名';
COMMENT ON COLUMN tablecolumnsort_info.sorttext IS '昇順の値のCSV';

COMMENT ON TABLE  pagebutton_master IS 'ページボタンマスタ';
COMMENT ON COLUMN pagebutton_master.id IS 'ID';
COMMENT ON COLUMN pagebutton_master.name IS '名称';
COMMENT ON COLUMN pagebutton_master.href IS 'リンク';
COMMENT ON COLUMN pagebutton_master.disporder IS '表示順';

COMMENT ON TABLE  pagemenubutton_info IS 'ページボタン表示マスタ';
COMMENT ON COLUMN pagemenubutton_info.id IS 'ID';
COMMENT ON COLUMN pagemenubutton_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN pagemenubutton_info.pagebuttonid IS 'ページボタンID';
COMMENT ON COLUMN pagemenubutton_info.href IS 'リンク';
COMMENT ON COLUMN pagemenubutton_info.target IS 'ターゲット名';
COMMENT ON COLUMN pagemenubutton_info.enable IS '利用可フラグ';
COMMENT ON COLUMN pagemenubutton_info.disporder IS '表示順';

COMMENT ON TABLE  menumap_info IS 'メニュー地図情報';
COMMENT ON COLUMN menumap_info.id IS 'ID';
COMMENT ON COLUMN menumap_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN menumap_info.extent IS '地図範囲（WKT）';
COMMENT ON COLUMN menumap_info.resolution IS '解像度';
COMMENT ON COLUMN menumap_info.note IS '備考';

COMMENT ON TABLE  maplayer_info IS '地図レイヤ情報';
COMMENT ON COLUMN maplayer_info.id IS 'ID';
COMMENT ON COLUMN maplayer_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN maplayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN maplayer_info.visible IS '初期表示フラグ';
COMMENT ON COLUMN maplayer_info.closed IS '凡例折りたたみ';
COMMENT ON COLUMN maplayer_info.editable IS '編集フラグ';
COMMENT ON COLUMN maplayer_info.addable IS '追加フラグ';
COMMENT ON COLUMN maplayer_info.snapable IS 'スナップフラグ';
COMMENT ON COLUMN maplayer_info.intersectionlayerid IS '切り出しレイヤID';
COMMENT ON COLUMN maplayer_info.valid IS '有効・無効';
COMMENT ON COLUMN maplayer_info.disporder IS '表示順';

COMMENT ON TABLE  maplayerattr_info IS '地図レイヤ属性情報';
COMMENT ON COLUMN maplayerattr_info.id IS 'ID';
COMMENT ON COLUMN maplayerattr_info.maplayerinfoid IS '地図レイヤID';
COMMENT ON COLUMN maplayerattr_info.attrid IS '属性項目名';
COMMENT ON COLUMN maplayerattr_info.name IS '名称';
COMMENT ON COLUMN maplayerattr_info.editable IS '編集フラグ';
COMMENT ON COLUMN maplayerattr_info.highlight IS 'ハイライト';
COMMENT ON COLUMN maplayerattr_info.grouping IS 'グループ化';
COMMENT ON COLUMN maplayerattr_info.disporder IS '表示順';

COMMENT ON TABLE  mapreferencelayer_info IS '地図参照レイヤ情報';
COMMENT ON COLUMN mapreferencelayer_info.id IS 'ID';
COMMENT ON COLUMN mapreferencelayer_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN mapreferencelayer_info.layerid IS 'レイヤID';
COMMENT ON COLUMN mapreferencelayer_info.visible IS '初期表示フラグ';
COMMENT ON COLUMN mapreferencelayer_info.closed IS '凡例折りたたみ';
COMMENT ON COLUMN mapreferencelayer_info.valid IS '有効・無効';
COMMENT ON COLUMN mapreferencelayer_info.disporder IS '表示順';

COMMENT ON TABLE  mapbaselayer_info IS '地図ベースレイヤ情報';
COMMENT ON COLUMN mapbaselayer_info.id IS 'ID';
COMMENT ON COLUMN mapbaselayer_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN mapbaselayer_info.layerid IS 'レイヤID';
COMMENT ON COLUMN mapbaselayer_info.visible IS '初期表示フラグ';
COMMENT ON COLUMN mapbaselayer_info.valid IS '有効・無効';
COMMENT ON COLUMN mapbaselayer_info.disporder IS '表示順';

COMMENT ON TABLE  tablecalculatecolumn_info IS 'テーブル演算項目情報';
COMMENT ON COLUMN tablecalculatecolumn_info.id IS 'ID';
COMMENT ON COLUMN tablecalculatecolumn_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN tablecalculatecolumn_info.columnname IS '項目名';

COMMENT ON TABLE  tablecalculate_info IS 'テーブル演算情報';
COMMENT ON COLUMN tablecalculate_info.id IS 'ID';
COMMENT ON COLUMN tablecalculate_info.tablecalculatecolumninfoid IS '計算結果の項目';
COMMENT ON COLUMN tablecalculate_info.function IS '計算式';


--外部地図データ、クリアリングハウス

COMMENT ON TABLE  clearinghousemetadata_info IS 'クリアリングハウス事前データ情報';
COMMENT ON COLUMN clearinghousemetadata_info.id IS 'ID';
COMMENT ON COLUMN clearinghousemetadata_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN clearinghousemetadata_info.metadataid IS 'メタデータID';
COMMENT ON COLUMN clearinghousemetadata_info.name IS 'データに付けられた名前';
COMMENT ON COLUMN clearinghousemetadata_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN clearinghousemetadata_info.note IS '備考';

COMMENT ON TABLE  clearinghousesearch_info IS 'クリアリングハウス検索情報';
COMMENT ON COLUMN clearinghousesearch_info.id IS 'ID';
COMMENT ON COLUMN clearinghousesearch_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN clearinghousesearch_info.interval IS '定期的に検索する間隔（秒）';
COMMENT ON COLUMN clearinghousesearch_info.query IS '検索条件';
COMMENT ON COLUMN clearinghousesearch_info.area IS '範囲のWKT';

COMMENT ON TABLE  clearinghousemetadatadefault_info IS 'メタデータデフォルト設定情報';
COMMENT ON COLUMN clearinghousemetadatadefault_info.id IS 'ID';
COMMENT ON COLUMN clearinghousemetadatadefault_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN clearinghousemetadatadefault_info.prefix IS '接頭語';
COMMENT ON COLUMN clearinghousemetadatadefault_info.suffix IS '接尾後';
COMMENT ON COLUMN clearinghousemetadatadefault_info.reference IS '問合せ先';
COMMENT ON COLUMN clearinghousemetadatadefault_info.telno IS '電話番号';

COMMENT ON TABLE  externalmapdata_info IS '外部地図データ情報';
COMMENT ON COLUMN externalmapdata_info.id IS 'ID';
COMMENT ON COLUMN externalmapdata_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN externalmapdata_info.metadataid IS 'メタデータの識別子';
COMMENT ON COLUMN externalmapdata_info.name IS 'データに付けられた名前';
COMMENT ON COLUMN externalmapdata_info.filterid IS 'フィルター';
COMMENT ON COLUMN externalmapdata_info.visible IS '初期表示フラグ';
COMMENT ON COLUMN externalmapdata_info.closed IS '凡例折りたたみ';
COMMENT ON COLUMN externalmapdata_info.disporder IS '表示順';

COMMENT ON TABLE  externaltabledata_info IS '外部リストデータ情報';
COMMENT ON COLUMN externaltabledata_info.id IS 'ID';
COMMENT ON COLUMN externaltabledata_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN externaltabledata_info.metadataid IS 'メタデータの識別子';
COMMENT ON COLUMN externaltabledata_info.filterid IS 'フィルター';


--通知

COMMENT ON TABLE noticegroup_info IS '通知グループ情報';
COMMENT ON COLUMN noticegroup_info.id IS 'ID';
COMMENT ON COLUMN noticegroup_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN noticegroup_info.name IS 'グループ名';
COMMENT ON COLUMN noticegroup_info.note IS '備考';
COMMENT ON COLUMN noticegroup_info.disporder IS '表示順';
COMMENT ON COLUMN noticegroup_info.valid IS '有効・無効';

COMMENT ON TABLE noticegroupuser_info IS '通知グループユーザ情報';
COMMENT ON COLUMN noticegroupuser_info.id IS 'ID';
COMMENT ON COLUMN noticegroupuser_info.noticegroupinfoid IS 'メールグループ情報';
COMMENT ON COLUMN noticegroupuser_info.userid IS 'ユーザID';

COMMENT ON TABLE noticeaddress_info IS '通知連絡先情報';
COMMENT ON COLUMN noticeaddress_info.id IS 'ID';
COMMENT ON COLUMN noticeaddress_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN noticeaddress_info.name IS '名前';
COMMENT ON COLUMN noticeaddress_info.telno IS '電話番号';
COMMENT ON COLUMN noticeaddress_info.mobileno IS '携帯電話番号';
COMMENT ON COLUMN noticeaddress_info.faxno IS 'FAX番号';
COMMENT ON COLUMN noticeaddress_info.email IS 'メールアドレス';
COMMENT ON COLUMN noticeaddress_info.mobilemail IS '携帯のメールアドレス';
COMMENT ON COLUMN noticeaddress_info.note IS '備考';
COMMENT ON COLUMN noticeaddress_info.valid IS '有効・無効';

COMMENT ON TABLE noticegroupaddress_info IS '通知グループ連絡先情報';
COMMENT ON COLUMN noticegroupaddress_info.id IS 'ID';
COMMENT ON COLUMN noticegroupaddress_info.noticegroupinfoid IS 'メールグループ情報';
COMMENT ON COLUMN noticegroupaddress_info.noticeaddressinfoid IS '連絡先ID';

COMMENT ON TABLE noticedefault_info IS '通知デフォルト情報';
COMMENT ON COLUMN noticedefault_info.id IS 'ID';
COMMENT ON COLUMN noticedefault_info.menuinfoid IS 'メニューID';
COMMENT ON COLUMN noticedefault_info.noticetemplatetypeid IS '通知テンプレート種別ID';
COMMENT ON COLUMN noticedefault_info.templateclass IS '区分';

COMMENT ON TABLE noticedefaultgroup_info IS '通知デフォルトグループ情報';
COMMENT ON COLUMN noticedefaultgroup_info.id IS 'ID';
COMMENT ON COLUMN noticedefaultgroup_info.noticedefaultinfoid IS '通知デフォルトID';
COMMENT ON COLUMN noticedefaultgroup_info.noticegroupinfoid IS '通知グループID';
COMMENT ON COLUMN noticedefaultgroup_info.defaulton IS 'デフォルトON';

COMMENT ON TABLE noticetemplatetype_master IS '通知テンプレート種別マスタ';
COMMENT ON COLUMN noticetemplatetype_master.id IS 'ID';
COMMENT ON COLUMN noticetemplatetype_master.name IS '名称';
COMMENT ON COLUMN noticetemplatetype_master.disporder IS '表示順';

COMMENT ON TABLE notice_template IS '通知テンプレート';
COMMENT ON COLUMN notice_template.id IS 'ID';
COMMENT ON COLUMN notice_template.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN notice_template.noticetemplatetypeid IS 'テンプレート種別';
COMMENT ON COLUMN notice_template.templateclass IS '区分';
COMMENT ON COLUMN notice_template.title IS 'タイトル';
COMMENT ON COLUMN notice_template.content IS '内容';
COMMENT ON COLUMN notice_template.note IS '備考';
COMMENT ON COLUMN notice_template.disporder IS '表示順';

COMMENT ON TABLE noticemail_data IS '通知データ';
COMMENT ON COLUMN noticemail_data.id IS 'ID';
COMMENT ON COLUMN noticemail_data.trackdataid IS '記録ID';
COMMENT ON COLUMN noticemail_data.noticetypeid IS '通知種別';
COMMENT ON COLUMN noticemail_data.mailto IS '宛先';
COMMENT ON COLUMN noticemail_data.title IS 'タイトル';
COMMENT ON COLUMN noticemail_data.content IS '内容';
COMMENT ON COLUMN noticemail_data.sendtime IS '送信時間';
COMMENT ON COLUMN noticemail_data.send IS '送信フラグ';
COMMENT ON COLUMN noticemail_data.trackdataname IS '災害名称';

COMMENT ON TABLE noticemailsend_data IS '通知先データ';
COMMENT ON COLUMN noticemailsend_data.id IS 'ID';
COMMENT ON COLUMN noticemailsend_data.noticemaildataid IS '通知データID';
COMMENT ON COLUMN noticemailsend_data.noticegroupinfoid IS 'メールグループ情報';

COMMENT ON TABLE noticetype_master IS '通知種別マスタ';
COMMENT ON COLUMN noticetype_master.id IS 'ID';
COMMENT ON COLUMN noticetype_master.name IS '名称';
COMMENT ON COLUMN noticetype_master.disporder IS '表示順';


--アラーム

COMMENT ON TABLE  alarmtype_master IS 'アラームタイプマスタ';
COMMENT ON COLUMN alarmtype_master.id IS 'ID';
COMMENT ON COLUMN alarmtype_master.name IS '名称';
COMMENT ON COLUMN alarmtype_master.disporder IS '表示順';

COMMENT ON TABLE  alarmmessage_info IS 'アラームメッセージ設定';
COMMENT ON COLUMN alarmmessage_info.id IS 'ID';
COMMENT ON COLUMN alarmmessage_info.localgovinfoid IS '自治体ID（０）';
COMMENT ON COLUMN alarmmessage_info.groupid IS 'グループID';
COMMENT ON COLUMN alarmmessage_info.alarmtypeid IS 'アラームタイプID';
COMMENT ON COLUMN alarmmessage_info.name IS 'アラームタイプ名';
COMMENT ON COLUMN alarmmessage_info.message IS '表示メッセージ （nameを置換する）';
COMMENT ON COLUMN alarmmessage_info.showmessage IS 'メッセージ表示フラグ';
COMMENT ON COLUMN alarmmessage_info.messagetype IS '"success", "error", "warning", "information"';
COMMENT ON COLUMN alarmmessage_info.duration IS '表示時間、0で手動非表示';
COMMENT ON COLUMN alarmmessage_info.disporder IS '表示順';
COMMENT ON COLUMN alarmmessage_info.valid IS '有効・無効';

COMMENT ON TABLE  alarmmessage_data IS 'アラームメッセージデータ';
COMMENT ON COLUMN alarmmessage_data.id IS 'ID';
COMMENT ON COLUMN alarmmessage_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN alarmmessage_data.trackdataid IS 'トラックデータID';
COMMENT ON COLUMN alarmmessage_data.alarmmessageinfoid IS 'アラームメッセージID';
COMMENT ON COLUMN alarmmessage_data.sendgroupid IS '送信班ID';
COMMENT ON COLUMN alarmmessage_data.groupid IS '閲覧班ID';
COMMENT ON COLUMN alarmmessage_data.noticeto IS '通知先';
COMMENT ON COLUMN alarmmessage_data.message IS '成形されたメッセージ';
COMMENT ON COLUMN alarmmessage_data.noticeurl IS '再現URL';
COMMENT ON COLUMN alarmmessage_data.showmessage IS 'メッセージ表示フラグ';
COMMENT ON COLUMN alarmmessage_data.messagetype IS '"success", "error", "warning", "information"';
COMMENT ON COLUMN alarmmessage_data.duration IS '表示時間、0で手動非表示';
COMMENT ON COLUMN alarmmessage_data.registtime IS '登録日時';
COMMENT ON COLUMN alarmmessage_data.deleted IS '削除フラグ';

COMMENT ON TABLE  alarmshow_data IS 'アラーム表示データ';
COMMENT ON COLUMN alarmshow_data.id IS 'ID';
COMMENT ON COLUMN alarmshow_data.alarmmessagedataid IS 'アラームメッセージデータID';
COMMENT ON COLUMN alarmshow_data.sessionid IS 'セッションID？IP？';
COMMENT ON COLUMN alarmshow_data.stop IS '終了フラグ';

COMMENT ON TABLE  alarmdefaultgroup_info IS 'アラームデフォルトグループ情報';
COMMENT ON COLUMN alarmdefaultgroup_info.id IS 'ID';
COMMENT ON COLUMN alarmdefaultgroup_info.noticedefaultinfoid IS '通知デフォルトID';
COMMENT ON COLUMN alarmdefaultgroup_info.groupid IS '通知グループID';
COMMENT ON COLUMN alarmdefaultgroup_info.defaulton IS 'デフォルトON/OFF';


-- テロップ

COMMENT ON TABLE  teloptype_master IS 'テロップ種別マスタ';
COMMENT ON COLUMN teloptype_master.id IS 'ID';
COMMENT ON COLUMN teloptype_master.name IS '名称';
COMMENT ON COLUMN teloptype_master.disporder IS '表示順';

COMMENT ON TABLE  telop_data IS 'テロップデータ';
COMMENT ON COLUMN telop_data.id IS 'ID';
COMMENT ON COLUMN telop_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN telop_data.teloptypeid IS '種別ID';
COMMENT ON COLUMN telop_data.message IS 'メッセージ';
COMMENT ON COLUMN telop_data.registtime IS '登録日時';
COMMENT ON COLUMN telop_data.viewlimit IS '掲載期限';


--監視観測

--気象情報

COMMENT ON TABLE  meteotype_master IS '気象情報等取得種別マスタ';
COMMENT ON COLUMN meteotype_master.id IS 'ID';
COMMENT ON COLUMN meteotype_master.name IS '名称';
COMMENT ON COLUMN meteotype_master.type IS '取得種別名';
COMMENT ON COLUMN meteotype_master.note IS '備考';
COMMENT ON COLUMN meteotype_master.disporder IS '表示順';

COMMENT ON TABLE  meteorequest_info IS '気象情報等取得情報';
COMMENT ON COLUMN meteorequest_info.id IS 'ID';
COMMENT ON COLUMN meteorequest_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN meteorequest_info.meteotypeid IS '気象情報種別';
COMMENT ON COLUMN meteorequest_info.meteoareaid IS 'エリアID';
COMMENT ON COLUMN meteorequest_info.meteoareaid2 IS 'エリアID予備';
COMMENT ON COLUMN meteorequest_info.alarm IS 'アラームフラグ';
COMMENT ON COLUMN meteorequest_info.view IS '表示フラグ';
COMMENT ON COLUMN meteorequest_info.map IS '地図表示フラグ';
COMMENT ON COLUMN meteorequest_info.note IS '備考';
COMMENT ON COLUMN meteorequest_info.valid IS '有効・無効';

COMMENT ON TABLE  meteo_data IS '気象情報取得データ';
COMMENT ON COLUMN meteo_data.id IS 'ID';
COMMENT ON COLUMN meteo_data.meteorequestinfoid IS '気象情報取得ID';
COMMENT ON COLUMN meteo_data.meteoid IS 'XMLファイルID';
COMMENT ON COLUMN meteo_data.reporttime IS '報告日時';
COMMENT ON COLUMN meteo_data.filepath IS 'ファイルパス';

COMMENT ON TABLE  meteoxslt_info IS '気象情報XSLT情報';
COMMENT ON COLUMN meteoxslt_info.id IS 'ID';
COMMENT ON COLUMN meteoxslt_info.localgovinfoid IS '自治体ID（０）';
COMMENT ON COLUMN meteoxslt_info.meteotypeid IS '気象情報種別';
COMMENT ON COLUMN meteoxslt_info.noticetypeid IS '通知種別';
COMMENT ON COLUMN meteoxslt_info.filepath IS 'xsltファイルパス';

COMMENT ON TABLE  meteotsunamiarea_master IS '津波エリア情報';
COMMENT ON COLUMN meteotsunamiarea_master.id IS 'ID';
COMMENT ON COLUMN meteotsunamiarea_master.name IS '名称';
COMMENT ON COLUMN meteotsunamiarea_master.code IS 'コード';
COMMENT ON COLUMN meteotsunamiarea_master.line IS 'WKT';
COMMENT ON COLUMN meteotsunamiarea_master.disporder IS '表示順';

COMMENT ON TABLE  meteowarnarea_master IS '警報・注意報エリア情報';
COMMENT ON COLUMN meteowarnarea_master.id IS 'ID';
COMMENT ON COLUMN meteowarnarea_master.name IS '名称';
COMMENT ON COLUMN meteowarnarea_master.code IS 'コード';
COMMENT ON COLUMN meteowarnarea_master.area IS 'WKT';
COMMENT ON COLUMN meteowarnarea_master.disporder IS '表示順';

COMMENT ON TABLE  meteoseismicarea1_master IS '震度速報エリア情報';
COMMENT ON COLUMN meteoseismicarea1_master.id IS 'ID';
COMMENT ON COLUMN meteoseismicarea1_master.name IS '名称';
COMMENT ON COLUMN meteoseismicarea1_master.code IS 'コード';
COMMENT ON COLUMN meteoseismicarea1_master.area IS 'WKT';
COMMENT ON COLUMN meteoseismicarea1_master.disporder IS '表示順';

COMMENT ON TABLE  meteovolcano_master IS '火山名マスタ';
COMMENT ON COLUMN meteovolcano_master.id IS 'ID';
COMMENT ON COLUMN meteovolcano_master.name IS '名称';
COMMENT ON COLUMN meteovolcano_master.code IS 'コード';
COMMENT ON COLUMN meteovolcano_master.area IS 'WKT';
COMMENT ON COLUMN meteovolcano_master.disporder IS '表示順';

COMMENT ON TABLE  meteoriver_master IS '河川マスタ';
COMMENT ON COLUMN meteoriver_master.id IS 'ID';
COMMENT ON COLUMN meteoriver_master.name IS '名称';
COMMENT ON COLUMN meteoriver_master.code IS 'コード';
COMMENT ON COLUMN meteoriver_master.area IS 'WKT';
COMMENT ON COLUMN meteoriver_master.disporder IS '表示順';

COMMENT ON TABLE  meteoriverarea_master IS '河川流域マスタ';
COMMENT ON COLUMN meteoriverarea_master.id IS 'ID';
COMMENT ON COLUMN meteoriverarea_master.name IS '名称';
COMMENT ON COLUMN meteoriverarea_master.code IS 'コード';
COMMENT ON COLUMN meteoriverarea_master.area IS 'WKT';
COMMENT ON COLUMN meteoriverarea_master.disporder IS '表示順';

COMMENT ON TABLE  meteolandslidearea_master IS '土砂災害エリアマスタ';
COMMENT ON COLUMN meteolandslidearea_master.id IS 'ID';
COMMENT ON COLUMN meteolandslidearea_master.name IS '名称';
COMMENT ON COLUMN meteolandslidearea_master.code IS 'コード';
COMMENT ON COLUMN meteolandslidearea_master.area IS 'WKT';
COMMENT ON COLUMN meteolandslidearea_master.disporder IS '表示順';

COMMENT ON TABLE  meteorainarea_master IS '雨量エリアマスタ';
COMMENT ON COLUMN meteorainarea_master.id IS 'ID';
COMMENT ON COLUMN meteorainarea_master.name IS '名称';
COMMENT ON COLUMN meteorainarea_master.code IS 'コード';
COMMENT ON COLUMN meteorainarea_master.area IS 'WKT';
COMMENT ON COLUMN meteorainarea_master.disporder IS '表示順';

COMMENT ON TABLE  meteotatsumakiarea_master IS '竜巻エリアマスタ';
COMMENT ON COLUMN meteotatsumakiarea_master.id IS 'ID';
COMMENT ON COLUMN meteotatsumakiarea_master.name IS '名称';
COMMENT ON COLUMN meteotatsumakiarea_master.code IS 'コード';
COMMENT ON COLUMN meteotatsumakiarea_master.area IS 'WKT';
COMMENT ON COLUMN meteotatsumakiarea_master.disporder IS '表示順';

COMMENT ON TABLE  meteotrigger_info IS '気象情報トリガー情報';
COMMENT ON COLUMN meteotrigger_info.id IS 'ID';
COMMENT ON COLUMN meteotrigger_info.meteorequestinfoid IS '気象情報取得ID';
COMMENT ON COLUMN meteotrigger_info.trigger IS 'トリガー';
COMMENT ON COLUMN meteotrigger_info.noticegroupinfoid IS '通知グループID';
COMMENT ON COLUMN meteotrigger_info.stationclassinfoid IS '体制ID';
COMMENT ON COLUMN meteotrigger_info.assemblemail IS '職員参集メール送信フラグ';
COMMENT ON COLUMN meteotrigger_info.issuetablemasterinfoid IS '避難勧告テーブルID';
COMMENT ON COLUMN meteotrigger_info.issueattrid IS '避難勧告属性項目';
COMMENT ON COLUMN meteotrigger_info.issuetext IS '避難情報文字列';
COMMENT ON COLUMN meteotrigger_info.publiccommons IS '公共コモンズ送信フラグ';
COMMENT ON COLUMN meteotrigger_info.publiccommonsmail IS 'エリアメール送信フラグ';
COMMENT ON COLUMN meteotrigger_info.sns IS 'SNS送信フラグ';
COMMENT ON COLUMN meteotrigger_info.startup IS '災害モード起動フラグ';
COMMENT ON COLUMN meteotrigger_info.note IS '備考';
COMMENT ON COLUMN meteotrigger_info.valid IS '有効・無効';

COMMENT ON TABLE  meteotrigger_data IS '気象情報トリガーデータ';
COMMENT ON COLUMN meteotrigger_data.id IS 'ID';
COMMENT ON COLUMN meteotrigger_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN meteotrigger_data.trackdataid IS '記録ID';
COMMENT ON COLUMN meteotrigger_data.meteotriggerinfoid IS 'トリガーID';
COMMENT ON COLUMN meteotrigger_data.triggertime IS 'トリガー発生時刻';
COMMENT ON COLUMN meteotrigger_data.startup IS '災害モード起動済';
COMMENT ON COLUMN meteotrigger_data.noticegroupinfoid IS '通知グループID';
COMMENT ON COLUMN meteotrigger_data.stationclassinfoid IS '体制ID';
COMMENT ON COLUMN meteotrigger_data.assemblemail IS '職員参集メール送信済';
COMMENT ON COLUMN meteotrigger_data.issue IS '避難勧告済';
COMMENT ON COLUMN meteotrigger_data.issuetext IS '避難情報文字列';
COMMENT ON COLUMN meteotrigger_data.publiccommons IS '公共コモンズ送信済';
COMMENT ON COLUMN meteotrigger_data.publiccommonsmail IS 'エリアメール送信済';
COMMENT ON COLUMN meteotrigger_data.sns IS 'SNS送信フラグ';


--避難勧告・指示

COMMENT ON TABLE  issuelayer_info IS '避難勧告レイヤ情報';
COMMENT ON COLUMN issuelayer_info.id IS 'ID';
COMMENT ON COLUMN issuelayer_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN issuelayer_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN issuelayer_info.attrid IS '属性ID';


--被災状況の集約

COMMENT ON TABLE  disastercasualties_data IS '被災状況人的被害データ';
COMMENT ON COLUMN disastercasualties_data.id IS 'ID';
COMMENT ON COLUMN disastercasualties_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disastercasualties_data.casualties1 IS '死者';
COMMENT ON COLUMN disastercasualties_data.casualties2 IS '行方不明者';
COMMENT ON COLUMN disastercasualties_data.casualties3 IS '負傷者（重傷）';
COMMENT ON COLUMN disastercasualties_data.casualties4 IS '負傷者（軽傷）';
COMMENT ON COLUMN disastercasualties_data.note IS '備考';
COMMENT ON COLUMN disastercasualties_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterhouse_data IS '被災状況住家被害（棟）データ';
COMMENT ON COLUMN disasterhouse_data.id IS 'ID';
COMMENT ON COLUMN disasterhouse_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterhouse_data.houseall IS '全壊';
COMMENT ON COLUMN disasterhouse_data.househalf IS '半壊';
COMMENT ON COLUMN disasterhouse_data.housepart IS '一部破損';
COMMENT ON COLUMN disasterhouse_data.houseupper IS '床上浸水';
COMMENT ON COLUMN disasterhouse_data.houselower IS '床下浸水';
COMMENT ON COLUMN disasterhouse_data.note IS '備考';
COMMENT ON COLUMN disasterhouse_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterhousehold_data IS '被災状況住家被害（世帯）データ';
COMMENT ON COLUMN disasterhousehold_data.id IS 'ID';
COMMENT ON COLUMN disasterhousehold_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterhousehold_data.houseall IS '全壊';
COMMENT ON COLUMN disasterhousehold_data.househalf IS '半壊';
COMMENT ON COLUMN disasterhousehold_data.housepart IS '一部破損';
COMMENT ON COLUMN disasterhousehold_data.houseupper IS '床上浸水';
COMMENT ON COLUMN disasterhousehold_data.houselower IS '床下浸水';
COMMENT ON COLUMN disasterhousehold_data.note IS '備考';
COMMENT ON COLUMN disasterhousehold_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterhouseregident_data IS '被災状況住家被害（人）データ';
COMMENT ON COLUMN disasterhouseregident_data.id IS 'ID';
COMMENT ON COLUMN disasterhouseregident_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterhouseregident_data.houseall IS '全壊';
COMMENT ON COLUMN disasterhouseregident_data.househalf IS '半壊';
COMMENT ON COLUMN disasterhouseregident_data.housepart IS '一部破損';
COMMENT ON COLUMN disasterhouseregident_data.houseupper IS '床上浸水';
COMMENT ON COLUMN disasterhouseregident_data.houselower IS '床下浸水';
COMMENT ON COLUMN disasterhouseregident_data.note IS '備考';
COMMENT ON COLUMN disasterhouseregident_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterroad_data IS '被災状況土木被害';
COMMENT ON COLUMN disasterroad_data.id IS 'ID';
COMMENT ON COLUMN disasterroad_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterroad_data.road IS '道路';
COMMENT ON COLUMN disasterroad_data.bridge IS '橋梁';
COMMENT ON COLUMN disasterroad_data.river IS '河川';
COMMENT ON COLUMN disasterroad_data.harbor IS '港湾';
COMMENT ON COLUMN disasterroad_data.landslide IS '砂防';
COMMENT ON COLUMN disasterroad_data.cliff IS 'かけ崩れ';
COMMENT ON COLUMN disasterroad_data.railway IS '鉄道不通';
COMMENT ON COLUMN disasterroad_data.ship IS '被害船舶';
COMMENT ON COLUMN disasterroad_data.water IS '水道';
COMMENT ON COLUMN disasterroad_data.roadmount IS '被害額';
COMMENT ON COLUMN disasterroad_data.note IS '備考';
COMMENT ON COLUMN disasterroad_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterlifeline_data IS '被災状況ライフライン被害';
COMMENT ON COLUMN disasterlifeline_data.id IS 'ID';
COMMENT ON COLUMN disasterlifeline_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterlifeline_data.telephone IS '電話';
COMMENT ON COLUMN disasterlifeline_data.electricity IS '電気';
COMMENT ON COLUMN disasterlifeline_data.gas IS 'ガス';
COMMENT ON COLUMN disasterlifeline_data.lifelinemount IS '被害額';
COMMENT ON COLUMN disasterlifeline_data.note IS '備考';
COMMENT ON COLUMN disasterlifeline_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterhospital_data IS '被災状況病院被害';
COMMENT ON COLUMN disasterhospital_data.id IS 'ID';
COMMENT ON COLUMN disasterhospital_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterhospital_data.hospital IS '病院';
COMMENT ON COLUMN disasterhospital_data.hospitalmount IS '被害額';
COMMENT ON COLUMN disasterhospital_data.note IS '備考';
COMMENT ON COLUMN disasterhospital_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterfarm_data IS '被災状況農林被害';
COMMENT ON COLUMN disasterfarm_data.id IS 'ID';
COMMENT ON COLUMN disasterfarm_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterfarm_data.field1 IS '田・流出・埋没';
COMMENT ON COLUMN disasterfarm_data.field2 IS '田・冠水';
COMMENT ON COLUMN disasterfarm_data.farm1 IS '畑・流出・埋没';
COMMENT ON COLUMN disasterfarm_data.farm2 IS '畑・冠水';
COMMENT ON COLUMN disasterfarm_data.farmmount IS '被害額';
COMMENT ON COLUMN disasterfarm_data.note IS '備考';
COMMENT ON COLUMN disasterfarm_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterwelfare_data IS '被災状況民政被害';
COMMENT ON COLUMN disasterwelfare_data.id IS 'ID';
COMMENT ON COLUMN disasterwelfare_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterwelfare_data.gabage IS '清掃施設';
COMMENT ON COLUMN disasterwelfare_data.block IS 'ブロック塀等';
COMMENT ON COLUMN disasterwelfare_data.welfaremount IS '被害額';
COMMENT ON COLUMN disasterwelfare_data.note IS '備考';
COMMENT ON COLUMN disasterwelfare_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterschool_data IS '被災状況文教被害';
COMMENT ON COLUMN disasterschool_data.id IS 'ID';
COMMENT ON COLUMN disasterschool_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterschool_data.school IS '文教施設';
COMMENT ON COLUMN disasterschool_data.schoolmount IS '被害額';
COMMENT ON COLUMN disasterschool_data.note IS '備考';
COMMENT ON COLUMN disasterschool_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterbuild_data IS '被災状況非住家被害';
COMMENT ON COLUMN disasterbuild_data.id IS 'ID';
COMMENT ON COLUMN disasterbuild_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterbuild_data.build1 IS '公共の建物（非住家）';
COMMENT ON COLUMN disasterbuild_data.build2 IS 'その他（非住家）';
COMMENT ON COLUMN disasterbuild_data.buildmount IS '被害額';
COMMENT ON COLUMN disasterbuild_data.note IS '備考';
COMMENT ON COLUMN disasterbuild_data.registtime IS '登録日時';

COMMENT ON TABLE  disasterfire_data IS '被災状況火災発生';
COMMENT ON COLUMN disasterfire_data.id IS 'ID';
COMMENT ON COLUMN disasterfire_data.trackdataid IS '記録ID';
COMMENT ON COLUMN disasterfire_data.fire1 IS '建物';
COMMENT ON COLUMN disasterfire_data.fire2 IS '危険物';
COMMENT ON COLUMN disasterfire_data.fire3 IS 'その他';
COMMENT ON COLUMN disasterfire_data.note IS '備考';
COMMENT ON COLUMN disasterfire_data.registtime IS '登録日時';

COMMENT ON TABLE  houseunit_master IS '住宅単位マスタ';
COMMENT ON COLUMN houseunit_master.id IS 'ID';
COMMENT ON COLUMN houseunit_master.name IS '名称';
COMMENT ON COLUMN houseunit_master.disporder IS '表示順';

COMMENT ON TABLE  report_data IS '報告データ';
COMMENT ON COLUMN report_data.id IS 'ID';
COMMENT ON COLUMN report_data.trackdataid IS '記録ID';
COMMENT ON COLUMN report_data.filepath IS 'ファイルパス';
COMMENT ON COLUMN report_data.note IS '備考';
COMMENT ON COLUMN report_data.registtime IS '登録時刻';

COMMENT ON TABLE  reportcontent_data IS '報告内容データ';
COMMENT ON COLUMN reportcontent_data.id IS 'ID';
COMMENT ON COLUMN reportcontent_data.reportdataid IS 'ID';
COMMENT ON COLUMN reportcontent_data.receiver IS '消防庁受信者氏名';
COMMENT ON COLUMN reportcontent_data.reporttime IS '報告日時';
COMMENT ON COLUMN reportcontent_data.pref IS '都道府県';
COMMENT ON COLUMN reportcontent_data.city IS '市町村';
COMMENT ON COLUMN reportcontent_data.reporter IS '報告者氏名';
COMMENT ON COLUMN reportcontent_data.disastername IS '災害名';
COMMENT ON COLUMN reportcontent_data.reportno IS '報告数';
COMMENT ON COLUMN reportcontent_data.place IS '発生場所';
COMMENT ON COLUMN reportcontent_data.occurtime IS '発生時刻';
COMMENT ON COLUMN reportcontent_data.summary IS '概況';
COMMENT ON COLUMN reportcontent_data.casualties1 IS '死者';
COMMENT ON COLUMN reportcontent_data.casualties2 IS '不明';
COMMENT ON COLUMN reportcontent_data.casualties3 IS '負傷者';
COMMENT ON COLUMN reportcontent_data.total IS '合計';
COMMENT ON COLUMN reportcontent_data.house1 IS '住家全壊';
COMMENT ON COLUMN reportcontent_data.house2 IS '住家半壊';
COMMENT ON COLUMN reportcontent_data.house3 IS '一部破損';
COMMENT ON COLUMN reportcontent_data.house4 IS '床上浸水';
COMMENT ON COLUMN reportcontent_data.headoffice1 IS '災害対策本部等の設置状況（都道府県）';
COMMENT ON COLUMN reportcontent_data.headoffice2 IS '災害対策本部等の設置状況（市町村）';
COMMENT ON COLUMN reportcontent_data.status1 IS '被害の状況';
COMMENT ON COLUMN reportcontent_data.status2 IS '応急対策の状況';

COMMENT ON TABLE  reportcontent2_data IS '報告内容データ２';
COMMENT ON COLUMN reportcontent2_data.id IS 'ID';
COMMENT ON COLUMN reportcontent2_data.reportdataid IS 'ID';
COMMENT ON COLUMN reportcontent2_data.reporttime IS '報告日時';
COMMENT ON COLUMN reportcontent2_data.pref IS '都道府県';
COMMENT ON COLUMN reportcontent2_data.city IS '市町村';
COMMENT ON COLUMN reportcontent2_data.disastername IS '災害名';
COMMENT ON COLUMN reportcontent2_data.reportno IS '報告数';
COMMENT ON COLUMN reportcontent2_data.reporter IS '報告者氏名';
COMMENT ON COLUMN reportcontent2_data.casualties21 IS '死者';
COMMENT ON COLUMN reportcontent2_data.casualties22 IS '行方不明者';
COMMENT ON COLUMN reportcontent2_data.casualties23 IS '負傷者（重傷）';
COMMENT ON COLUMN reportcontent2_data.casualties24 IS '負傷者（軽傷）';
COMMENT ON COLUMN reportcontent2_data.house21 IS '全壊（棟）';
COMMENT ON COLUMN reportcontent2_data.household1 IS '全壊（世帯）';
COMMENT ON COLUMN reportcontent2_data.numpeople1 IS '全壊（人）';
COMMENT ON COLUMN reportcontent2_data.house22 IS '半壊（棟）';
COMMENT ON COLUMN reportcontent2_data.household2 IS '半壊（世帯）';
COMMENT ON COLUMN reportcontent2_data.numpeople2 IS '半壊（人）';
COMMENT ON COLUMN reportcontent2_data.house23 IS '一部破損（棟）';
COMMENT ON COLUMN reportcontent2_data.household3 IS '一部破損（世帯）';
COMMENT ON COLUMN reportcontent2_data.numpeople3 IS '一部破損（人）';
COMMENT ON COLUMN reportcontent2_data.house24 IS '床上浸水（棟）';
COMMENT ON COLUMN reportcontent2_data.household4 IS '床上浸水（世帯）';
COMMENT ON COLUMN reportcontent2_data.numpeople4 IS '床上浸水（人）';
COMMENT ON COLUMN reportcontent2_data.house25 IS '床下浸水（棟）';
COMMENT ON COLUMN reportcontent2_data.household5 IS '床下浸水（世帯）';
COMMENT ON COLUMN reportcontent2_data.numpeople5 IS '床下浸水（人）';
COMMENT ON COLUMN reportcontent2_data.build1 IS '公共の建物（非住家）';
COMMENT ON COLUMN reportcontent2_data.build2 IS 'その他（非住家）';
COMMENT ON COLUMN reportcontent2_data.field1 IS '田・流出・埋没';
COMMENT ON COLUMN reportcontent2_data.field2 IS '田・冠水';
COMMENT ON COLUMN reportcontent2_data.farm1 IS '畑・流出・埋没';
COMMENT ON COLUMN reportcontent2_data.farm2 IS '畑・冠水';
COMMENT ON COLUMN reportcontent2_data.school IS '文教施設';
COMMENT ON COLUMN reportcontent2_data.hospital IS '病院';
COMMENT ON COLUMN reportcontent2_data.road IS '道路';
COMMENT ON COLUMN reportcontent2_data.bridge IS '橋梁';
COMMENT ON COLUMN reportcontent2_data.river IS '河川';
COMMENT ON COLUMN reportcontent2_data.harbor IS '港湾';
COMMENT ON COLUMN reportcontent2_data.landslide IS '砂防';
COMMENT ON COLUMN reportcontent2_data.gabage IS '清掃施設';
COMMENT ON COLUMN reportcontent2_data.cliff IS 'かけ崩れ';
COMMENT ON COLUMN reportcontent2_data.railway IS '鉄道不通';
COMMENT ON COLUMN reportcontent2_data.ship IS '被害船舶';
COMMENT ON COLUMN reportcontent2_data.water IS '水道';
COMMENT ON COLUMN reportcontent2_data.telephone IS '電話';
COMMENT ON COLUMN reportcontent2_data.electricity IS '電気';
COMMENT ON COLUMN reportcontent2_data.gas IS 'ガス';
COMMENT ON COLUMN reportcontent2_data.block IS 'ブロック塀等';
COMMENT ON COLUMN reportcontent2_data.suffer1 IS '罹災世帯数';
COMMENT ON COLUMN reportcontent2_data.suffer2 IS '罹災者数';
COMMENT ON COLUMN reportcontent2_data.fire1 IS '建物';
COMMENT ON COLUMN reportcontent2_data.fire2 IS '危険物';
COMMENT ON COLUMN reportcontent2_data.fire3 IS 'その他';
COMMENT ON COLUMN reportcontent2_data.amount1 IS '公共文教施設';
COMMENT ON COLUMN reportcontent2_data.amount2 IS '農林水産業施設';
COMMENT ON COLUMN reportcontent2_data.amount3 IS '公共土木施設';
COMMENT ON COLUMN reportcontent2_data.amount4 IS 'その他公共施設';
COMMENT ON COLUMN reportcontent2_data.subtotal IS '小計';
COMMENT ON COLUMN reportcontent2_data.amount5 IS '農業被害';
COMMENT ON COLUMN reportcontent2_data.amount6 IS '林業被害';
COMMENT ON COLUMN reportcontent2_data.amount7 IS '畜産被害';
COMMENT ON COLUMN reportcontent2_data.amount8 IS '水産被害';
COMMENT ON COLUMN reportcontent2_data.amount9 IS '商工被害';
COMMENT ON COLUMN reportcontent2_data.amount10 IS 'その他被害';
COMMENT ON COLUMN reportcontent2_data.atotal IS '被害総額';
COMMENT ON COLUMN reportcontent2_data.headoffice21 IS '本部名 もしくは 都道府県';
COMMENT ON COLUMN reportcontent2_data.headoffice22 IS '本部設置 もしくは 市町村';
COMMENT ON COLUMN reportcontent2_data.headoffice23 IS '解散';
COMMENT ON COLUMN reportcontent2_data.fireman1 IS '消防職員出動延人数';
COMMENT ON COLUMN reportcontent2_data.fireman2 IS '消防団員出動延人数';
COMMENT ON COLUMN reportcontent2_data.status IS '住民避難の状況';
COMMENT ON COLUMN reportcontent2_data.note2 IS '備考';


--本部設置

COMMENT ON TABLE  assemble_info IS '職員参集情報';
COMMENT ON COLUMN assemble_info.id IS 'ID';
COMMENT ON COLUMN assemble_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN assemble_info.stationclassinfoid IS '体制ID';
COMMENT ON COLUMN assemble_info.noticetemplateid IS 'テンプレートID';
COMMENT ON COLUMN assemble_info.noticegroupinfoid IS '通知グループID';
COMMENT ON COLUMN assemble_info.name IS '参集名';
COMMENT ON COLUMN assemble_info.note IS '備考';
COMMENT ON COLUMN assemble_info.valid IS '有効・無効';

COMMENT ON TABLE  assemblestate_data IS '職員参集状況';
COMMENT ON COLUMN assemblestate_data.id IS 'ID';
COMMENT ON COLUMN assemblestate_data.trackdataid IS '記録ID';
COMMENT ON COLUMN assemblestate_data.userid IS 'ユーザID';
COMMENT ON COLUMN assemblestate_data.safetystateid IS '安否確認状況';
COMMENT ON COLUMN assemblestate_data.groupname IS '担当班名';
COMMENT ON COLUMN assemblestate_data.unitname IS '部課名';
COMMENT ON COLUMN assemblestate_data.username IS '職員名';
COMMENT ON COLUMN assemblestate_data.staffno IS '職員NO';
COMMENT ON COLUMN assemblestate_data.note IS '備考';
COMMENT ON COLUMN assemblestate_data.registtime IS '登録時間';
COMMENT ON COLUMN assemblestate_data.updatetime IS '更新時間';

COMMENT ON TABLE  safetystate_master IS '安否確認状況マスタ';
COMMENT ON COLUMN safetystate_master.id IS 'ID';
COMMENT ON COLUMN safetystate_master.name IS '名称';
COMMENT ON COLUMN safetystate_master.disporder IS '表示順';

COMMENT ON TABLE  station_master IS '体制マスタ';
COMMENT ON COLUMN station_master.id IS 'ID';
COMMENT ON COLUMN station_master.name IS '名称';
COMMENT ON COLUMN station_master.disporder IS '表示順';

COMMENT ON TABLE  stationclass_info IS '体制区分';
COMMENT ON COLUMN stationclass_info.id IS 'ID';
COMMENT ON COLUMN stationclass_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN stationclass_info.disasterid IS '災害種別ID';
COMMENT ON COLUMN stationclass_info.stationid IS '体制ID';
COMMENT ON COLUMN stationclass_info.name IS '名称';
COMMENT ON COLUMN stationclass_info.staff IS '出動職員';
COMMENT ON COLUMN stationclass_info.note IS '備考';
COMMENT ON COLUMN stationclass_info.disporder IS '表示順';
COMMENT ON COLUMN stationclass_info.valid IS '有効・無効';

COMMENT ON TABLE  stationorder_data IS '体制発令';
COMMENT ON COLUMN stationorder_data.id IS 'ID';
COMMENT ON COLUMN stationorder_data.trackdataid IS '記録ID';
COMMENT ON COLUMN stationorder_data.stationclassinfoid IS '体制区分ID';
COMMENT ON COLUMN stationorder_data.note IS '備考';
COMMENT ON COLUMN stationorder_data.shifttime IS '移行時間（手入力）';
COMMENT ON COLUMN stationorder_data.registtime IS '登録時間';
COMMENT ON COLUMN stationorder_data.closetime IS '終了時間';

COMMENT ON TABLE  stationalarm_info IS '体制アラート情報';
COMMENT ON COLUMN stationalarm_info.id IS 'ID';
COMMENT ON COLUMN stationalarm_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN stationalarm_info.alarmmessageinfoid IS 'アラームメッセージID';
COMMENT ON COLUMN stationalarm_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN stationalarm_info.stationclassinfoid IS '体制ID';
COMMENT ON COLUMN stationalarm_info.stationclassorgid IS '元体制ID';


--避難所関連


-- 本部会議

COMMENT ON TABLE  headoffice_data IS '本部会議データ';
COMMENT ON COLUMN headoffice_data.id IS 'ID';
COMMENT ON COLUMN headoffice_data.trackdataid IS '記録ID';
COMMENT ON COLUMN headoffice_data.name IS '会議名';
COMMENT ON COLUMN headoffice_data.filepath IS 'ファイルパス';
COMMENT ON COLUMN headoffice_data.meetingtime IS '開催日時';
COMMENT ON COLUMN headoffice_data.registtime IS '登録日時';
COMMENT ON COLUMN headoffice_data.note IS '備考';


-- 広報

COMMENT ON TABLE  twitter_master IS 'Twitterマスタ';
COMMENT ON COLUMN twitter_master.id IS 'ID';
COMMENT ON COLUMN twitter_master.consumerkey IS 'Consumer key';
COMMENT ON COLUMN twitter_master.consumersecret IS 'Consumer secret';

COMMENT ON TABLE  twitter_info IS 'Twitter設定';
COMMENT ON COLUMN twitter_info.id IS 'ID';
COMMENT ON COLUMN twitter_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN twitter_info.accesstoken IS 'Access token';
COMMENT ON COLUMN twitter_info.accesstokensecret IS 'Access token secret';

COMMENT ON TABLE publiccommons_report_data IS '公共情報コモンズ発信データ';
COMMENT ON COLUMN publiccommons_report_data.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_data.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_data.trackdataid IS '記録データID';
COMMENT ON COLUMN publiccommons_report_data.category IS '情報種別';
COMMENT ON COLUMN publiccommons_report_data.distribution_id IS 'メッセージID';
COMMENT ON COLUMN publiccommons_report_data.document_id IS 'ドキュメントID';
COMMENT ON COLUMN publiccommons_report_data.document_id_serial IS 'ドキュメントID連番';
COMMENT ON COLUMN publiccommons_report_data.document_revision IS 'ドキュメント版番号';
COMMENT ON COLUMN publiccommons_report_data.filename IS 'ファイル名';
COMMENT ON COLUMN publiccommons_report_data.status IS 'ステータス';
COMMENT ON COLUMN publiccommons_report_data.createtime IS '作成日時';
COMMENT ON COLUMN publiccommons_report_data.reporttime IS '発表日時';
COMMENT ON COLUMN publiccommons_report_data.startsendtime IS '発信開始日時';
COMMENT ON COLUMN publiccommons_report_data.sendtime IS '発信成功日時';
COMMENT ON COLUMN publiccommons_report_data.success IS '発信成功フラグ';
COMMENT ON COLUMN publiccommons_report_data.registtime IS '登録日時';

COMMENT ON TABLE publiccommons_send_history_data IS '公共情報コモンズ発信履歴データ';
COMMENT ON COLUMN publiccommons_send_history_data.id IS 'ID';
COMMENT ON COLUMN publiccommons_send_history_data.publiccommons_report_data_id IS '公共情報コモンズ発信データID';
COMMENT ON COLUMN publiccommons_send_history_data.publiccommons_send_to_info_id IS '公共情報コモンズ発信先データID';
COMMENT ON COLUMN publiccommons_send_history_data.sendtime IS '発信日時';
COMMENT ON COLUMN publiccommons_send_history_data.success IS '発信成功フラグ';

COMMENT ON TABLE publiccommons_send_to_info IS '公共情報コモンズ発信先データ';
COMMENT ON COLUMN publiccommons_send_to_info.id IS 'ID';
COMMENT ON COLUMN publiccommons_send_to_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_send_to_info.endpoint_url IS 'エンドポイントURL';
COMMENT ON COLUMN publiccommons_send_to_info.username IS 'ユーザ名';
COMMENT ON COLUMN publiccommons_send_to_info.password IS 'パスワード';
COMMENT ON COLUMN publiccommons_send_to_info.send_order IS '送信順';

COMMENT ON TABLE publiccommons_report_refuge_info IS '公共情報コモンズ避難勧告レイヤ情報';
COMMENT ON COLUMN publiccommons_report_refuge_info.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_refuge_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_refuge_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN publiccommons_report_refuge_info.attrarea IS '地区';
COMMENT ON COLUMN publiccommons_report_refuge_info.attrorderstatus IS '発令状況';
COMMENT ON COLUMN publiccommons_report_refuge_info.attrhouseholds IS '対象世帯数';
COMMENT ON COLUMN publiccommons_report_refuge_info.attrpeople IS '人数';
COMMENT ON COLUMN publiccommons_report_refuge_info.attrordertime IS '発令日時';

COMMENT ON TABLE publiccommons_report_shelter_info IS '公共情報コモンズ避難所レイヤ情報';
COMMENT ON COLUMN publiccommons_report_shelter_info.id IS 'ID';
COMMENT ON COLUMN publiccommons_report_shelter_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN publiccommons_report_shelter_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrshelter IS '避難所名';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrarea IS '地区';
COMMENT ON COLUMN publiccommons_report_shelter_info.attraddress IS '住所';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrphone IS '電話番号';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrfax IS 'FAX';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrstaff IS '代表者氏名';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrstatus IS '開設状況';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrcapacity IS '収容定員数';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrsetuptime IS '開設日時';
COMMENT ON COLUMN publiccommons_report_shelter_info.attrcloseTime IS '閉鎖日時';

-- その他

COMMENT ON TABLE  autocomplete_info IS '入力補完情報';
COMMENT ON COLUMN autocomplete_info.id IS 'ID';
COMMENT ON COLUMN autocomplete_info.localgovinfoid IS '自治体ID';
COMMENT ON COLUMN autocomplete_info.groupid IS '班名';
COMMENT ON COLUMN autocomplete_info.tablemasterinfoid IS 'テーブルID';
COMMENT ON COLUMN autocomplete_info.columnname IS '項目名';

COMMENT ON TABLE  autocomplete_data IS '入力補完データ';
COMMENT ON COLUMN autocomplete_data.id IS 'ID';
COMMENT ON COLUMN autocomplete_data.autocompleteinfoid IS '入力補完情報ID';
COMMENT ON COLUMN autocomplete_data.string IS '文字列';
COMMENT ON COLUMN autocomplete_data.count IS '回数';
COMMENT ON COLUMN autocomplete_data.lasttime IS '最後に入力した時間';
