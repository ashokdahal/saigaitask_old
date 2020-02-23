/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
INSERT INTO pagebutton_master VALUES (17, '検索', 'javascript:openSearchDialog()', 17);
SELECT pg_catalog.setval('pagebutton_master_id_seq', 17, true);

INSERT INTO en.pagebutton_master VALUES (17, 'Search', 'javascript:openSearchDialog()', 17);
SELECT pg_catalog.setval('en.pagebutton_master_id_seq', 17, true);
