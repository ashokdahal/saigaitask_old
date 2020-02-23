/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
INSERT INTO disaster_master
       SELECT 6, '地震', 4
       WHERE NOT EXISTS (SELECT 1 FROM disaster_master WHERE id=6);
SELECT pg_catalog.setval('disaster_master_id_seq', (select max(id) from disaster_master), true);

INSERT INTO en.disaster_master
       SELECT 6, 'QUAKE', 4
       WHERE NOT EXISTS (SELECT 1 FROM en.disaster_master WHERE id=6);
SELECT pg_catalog.setval('en.disaster_master_id_seq', (select max(id) from en.disaster_master), true);
