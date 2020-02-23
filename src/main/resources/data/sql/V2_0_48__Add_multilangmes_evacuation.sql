
-- @see http://www.bousai.go.jp/oukyu/hinankankoku/hinanjumbijoho/index.html
/*
  平成28年台風第10号による水害では、死者・行方不明者27人が発生する等、東北・北海道の各地で甚大な被害が発生しました。
  とりわけ、岩手県岩泉町では、グループホームが被災し、入所者9名が全員亡くなる等、高齢者の被災が相次ぎました。
  「避難準備情報」の名称については、本水害では、高齢者施設において、適切な避難行動がとられなかったことを重く受けとめ、
  高齢者等が避難を開始する段階であるということを明確にするため、
  「避難準備情報」を「避難準備・高齢者等避難開始」に名称変更することといたしました。
*/
update multilangmes_info set message='避難準備・高齢者等避難開始' where messageid='Evacuation preparation' and multilanginfoid=2;
update multilangmes_info set message='避難指示（緊急）' where messageid='Evacuation order' and multilanginfoid=2;
