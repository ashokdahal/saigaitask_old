/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

--
-- fix {0] to {0}
--

-- 'Insert {0] into {1} by history data import of history map. gid={2}';
-- 'Add {0] into {1} by history data import of history map. gid={2}';

-- fix message
UPDATE multilangmes_info SET message = replace(message, '{0]', '{0}') where message like '%{0]%';

-- fix messageid
UPDATE multilangmes_info SET messageid = replace(messageid, '{0]', '{0}') where messageid like '%{0]%';

