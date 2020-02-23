/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
ALTER TABLE reportcontent2_data RENAME house21    TO houseall1;   -- 全壊（棟）
ALTER TABLE reportcontent2_data RENAME household1 TO houseall2;   -- 全壊（世帯）
ALTER TABLE reportcontent2_data RENAME numpeople1 TO houseall3;   -- 全壊（人）
ALTER TABLE reportcontent2_data RENAME house22    TO househalf1;  -- 半壊（棟）
ALTER TABLE reportcontent2_data RENAME household2 TO househalf2;  -- 半壊（世帯）
ALTER TABLE reportcontent2_data RENAME numpeople2 TO househalf3;  -- 半壊（人）
ALTER TABLE reportcontent2_data RENAME house23    TO housepart1;  -- 一部破損（棟）
ALTER TABLE reportcontent2_data RENAME household3 TO housepart2;  -- 一部破損（世帯）
ALTER TABLE reportcontent2_data RENAME numpeople3 TO housepart3;  -- 一部破損（人）
ALTER TABLE reportcontent2_data RENAME house24    TO houseupper1; -- 床上浸水（棟）
ALTER TABLE reportcontent2_data RENAME household4 TO houseupper2; -- 床上浸水（世帯）
ALTER TABLE reportcontent2_data RENAME numpeople4 TO houseupper3; -- 床上浸水（人）
ALTER TABLE reportcontent2_data RENAME house25    TO houselower1; -- 床下浸水（棟）
ALTER TABLE reportcontent2_data RENAME household5 TO houselower2; -- 床下浸水（世帯）
ALTER TABLE reportcontent2_data RENAME numpeople5 TO houselower3; -- 床下浸水（人）