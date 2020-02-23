/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
INSERT INTO pagebutton_master VALUES (14, '避難者推計', 'javascript:decisionSupport()', 14);
SELECT pg_catalog.setval('pagebutton_master_id_seq', 14, true);

INSERT INTO en.pagebutton_master VALUES (14, 'Evacuees estimate', 'javascript:decisionSupport()', 14);
SELECT pg_catalog.setval('en.pagebutton_master_id_seq', 14, true);
