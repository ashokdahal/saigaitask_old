/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

update tablemaster_info set name='被災状況土木被害' where name='被災状況土木害データ';
update tablemaster_info set name='被災状況民生被害' where name='被災状況民政被害';
update tablemaster_info set name='被災状況人的被害' where name='被災状況人的被害データ';
update tablemaster_info set name='被災状況住家被害' where name='被災状況住家被害データ';
update tablemaster_info set name='被災状況住家被害（世帯）' where name='被災状況住家被害（世帯）データ';
update tablemaster_info set name='被災状況住家被害（人）' where name='被災状況住家被害（人）データ';

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
