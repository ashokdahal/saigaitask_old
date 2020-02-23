/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
-- メニュー情報のfilteridをフィルター情報に移動
insert into filter_info ( menuinfoid , name , filterid, valid, disporder )
select id, '移行データ',filterid,true,1 from menu_info where filterid is not null order by id,filterid;
