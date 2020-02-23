
update menu_info set deleted=true where id=1;

update menu_info set deleted=true where id=2;

update menu_info set deleted=true where id=4;

delete from pagemenubutton_info where id=15;
delete from pagemenubutton_info where id=12;
delete from pagemenubutton_info where id=13;

update pagemenubutton_info set enable=true where id=11;

update tablelistcolumn_info set sortable =false where id=9;
update tablelistcolumn_info set disporder=2 where id=10;
update tablelistcolumn_info set sortable=false, disporder=3 where id=11;
update tablelistcolumn_info set sortable=false, disporder=4 where id=12;

update tablelistcolumn_info set defaultsort=1 where id=10;

