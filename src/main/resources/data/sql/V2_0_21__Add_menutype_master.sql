/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
INSERT INTO menutype_master(id, name, disporder)
SELECT 25, '投稿写真振り分け', 25
WHERE NOT EXISTS (
    SELECT id, name, disporder
    FROM menutype_master
    WHERE id=25
    )
returning *;
SELECT pg_catalog.setval('menutype_master_id_seq', 25, true);

INSERT INTO en.menutype_master VALUES (25, 'Posted photo sorting', 25);
SELECT pg_catalog.setval('en.menutype_master_id_seq', 25, true);

INSERT INTO menutype_master VALUES (26, 'エクセル帳票', 26);
SELECT pg_catalog.setval('menutype_master_id_seq', 26, true);

INSERT INTO en.menutype_master VALUES (26, 'Excel file', 26);
SELECT pg_catalog.setval('en.menutype_master_id_seq', 26, true);
