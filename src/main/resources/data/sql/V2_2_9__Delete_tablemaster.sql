/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

-- 日本語の方は V2.0.13でコミット済み
update en.menutype_master set name='Disabled(Response history)' where id=13;


update public.menutype_master set name='使用不可（4号様式）' where id=6;
update     en.menutype_master set name='Disabled(No. 4 format)' where id=6;

update public.menutype_master set name='使用不可（4号様式の集計・総括）' where id=18;
update     en.menutype_master set name='Disabled(Summary of No.4 format)' where id=18;




