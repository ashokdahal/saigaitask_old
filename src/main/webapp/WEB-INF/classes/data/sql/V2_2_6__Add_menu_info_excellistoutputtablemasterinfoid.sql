/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
alter table menu_info add excellistoutputtablemasterinfoid bigint;
comment on column menu_info.excellistoutputtablemasterinfoid IS 'エクセル帳票出力テーブルID';

alter table menu_info add excellistoutputtableregisttimeattrid text;
comment on column menu_info.excellistoutputtableregisttimeattrid IS 'エクセル帳票出力テーブル 登録日時の属性ID';

alter table menu_info add excellistoutputtabledownloadlinkattrid text;
comment on column menu_info.excellistoutputtabledownloadlinkattrid IS 'エクセル帳票出力テーブル ダウンロードリンクの属性ID';

-- すでにエクセル帳票テーブルを設定済みの場合は、report_data に出力するようにする。
UPDATE menu_info SET excellistoutputtablemasterinfoid=(select id from tablemaster_info where tablename='report_data'), excellistoutputtableregisttimeattrid='registtime', excellistoutputtabledownloadlinkattrid='filepath' where menutypeid=26;