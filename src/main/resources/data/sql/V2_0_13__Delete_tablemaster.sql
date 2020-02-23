/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

--体制テーブルの削除
UPDATE stationlayer_info                 SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE tablecalculatecolumn_info         SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE tablecolumnsort_info              SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE historytable_info                 SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE importtablemaster_data            SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE issuelayer_info                   SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE maplayer_info                     SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE menutable_info                    SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE observatorydamlayer_info          SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE observatoryrainlayer_info         SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE observatoryriverlayer_info        SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE postingphotolayer_info            SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
--UPDATE publiccommons_report_refuge_info  SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
--UPDATE publiccommons_report_shelter_info SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE tableresetcolumn_data             SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE timelinetable_info                SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE tracktable_info                   SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE decisionsupport_info              SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE stationalarm_info                 SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE toolbox_data                      SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE autocomplete_info                 SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE clearinghousemetadata_info        SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE observatorydam_info               SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE observatoryrain_info              SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
UPDATE observatoryriver_info             SET tablemasterinfoid=null WHERE tablemasterinfoid IN(SELECT id FROM tablemaster_info WHERE tablename='stationorder_data');
delete from tablemaster_info where tablename='stationorder_data';

--対応履歴メニュー種別削除
--delete from menutype_master where id=13;
update menutype_master set name='使用不可（対応履歴）' where id=13;


