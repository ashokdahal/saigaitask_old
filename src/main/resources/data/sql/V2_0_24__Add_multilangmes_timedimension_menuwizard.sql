/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
DELETE FROM multilangmes_info WHERE messageid = 'Ordinary times';
DELETE FROM multilangmes_info WHERE messageid = 'While disaster happens';
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Normal', '平常時');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Disaster', '災害時');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Normal', 'Normal');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Disaster', 'Disaster');

INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected tab is highlighted.', '選択するとタブが強調表示されます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'It shows a window type displayed by a menu clicked.', 'メニューをクリックした際に表示される画面の種類を表します。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, an action depending on page button ID, will be performed by a button clicked.', '選択するとボタン押下時にページボタンIDに応じた動作が行われます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, an item will be added.', '選択すると項目を追加することができます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, an item will be deleted.', '選択すると項目を削除することができます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, a total amount will be displayed.', '選択すると合計値を計算して表示します。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected attribution is to be edited.', '選択すると属性を編集することができます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected attribution is highlighted.', '選択すると属性が強調表示されます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, the butch group operation will be valid.', '選択するとグループ化による一括変更が可能となります。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, sort operation will be valid.', '選択するとリスト表示時のソートが可能となります。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Sort setting. "-1": sort invalid "0": ascending "1": descending', 'ソート設定を行います。"-1":ソートしない　"0":昇順　"1":降順');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Data, such as photos can be uploaded.', '写真等の電子データをアップロードすることができます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Log will be displayed in response history when an attribution changes.', '属性が変化するタイミングでログが対応履歴に表示されます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected attribution is displayed as default.', '選択すると属性を初期表示します。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, the legend will be folded as default.', '選択すると初期表示として凡例を折りたたんで表示します。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected item is to be edited.', '選択すると項目を編集可能となります。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected item is to be added.', '選択すると項目を追加可能となります。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'If selected, search function will be valid.', '選択すると検索機能が有効となります。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Cursor performs with snap when features are registered.', '地物の登録時にマウスカーソルがスナップするようにできます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'Geospatial calculation will perform trimming with a feature on a cutting-out-layerID when points, lines or polygons are registered.', '点、線、面を登録したときに切り出しレイヤIDの地物と空間演算でトリミング処理を行うことができます。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'The selected item will be displayed as default.', '選択すると初期表示します。');
INSERT INTO multilangmes_info VALUES (DEFAULT,2,'It shows registered filter ID.', '登録済みのフィルタIDです。');

INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected tab is highlighted.', 'The selected tab is highlighted.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'It shows a window type displayed by a menu clicked.', 'It shows a window type displayed by a menu clicked.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, an action depending on page button ID, will be performed by a button clicked.', 'If selected, an action depending on page button ID, will be performed by a button clicked.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, an item will be added.', 'If selected, an item will be added.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, an item will be deleted.', 'If selected, an item will be deleted.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, a total amount will be displayed.', 'If selected, a total amount will be displayed.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected attribution is to be edited.', 'The selected attribution is to be edited.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected attribution is highlighted.', 'The selected attribution is highlighted.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, the butch group operation will be valid.', 'If selected, the butch group operation will be valid.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, sort operation will be valid.', 'If selected, sort operation will be valid.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Sort setting. "-1": sort invalid "0": ascending "1": descending', 'Sort setting. "-1": sort invalid "0": ascending "1": descending');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Data, such as photos can be uploaded.', 'Data, such as photos can be uploaded.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Log will be displayed in response history when an attribution changes.', 'Log will be displayed in response history when an attribution changes.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected attribution is displayed as default.', 'The selected attribution is displayed as default.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, the legend will be folded as default.', 'If selected, the legend will be folded as default.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected item is to be edited.', 'The selected item is to be edited.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected item is to be added.', 'The selected item is to be added.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'If selected, search function will be valid.', 'If selected, search function will be valid.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Cursor performs with snap when features are registered.', 'Cursor performs with snap when features are registered.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'Geospatial calculation will perform trimming with a feature on a cutting-out-layerID when points, lines or polygons are registered.', 'Geospatial calculation will perform trimming with a feature on a cutting-out-layerID when points, lines or polygons are registered.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'The selected item will be displayed as default.', 'The selected item will be displayed as default.');
INSERT INTO multilangmes_info VALUES (DEFAULT,1,'It shows registered filter ID.', 'It shows registered filter ID.');
