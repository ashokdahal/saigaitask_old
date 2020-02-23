/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */

ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN postcode text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN adminarea text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN city text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN adminareacode text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN citycode text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN deliverypoint text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN email text;
ALTER TABLE clearinghousemetadatadefault_info ADD COLUMN linkage text;

COMMENT ON COLUMN clearinghousemetadatadefault_info.postcode IS '郵便番号';
COMMENT ON COLUMN clearinghousemetadatadefault_info.adminarea IS '都道府県名';
COMMENT ON COLUMN clearinghousemetadatadefault_info.city IS '市区町村名';
COMMENT ON COLUMN clearinghousemetadatadefault_info.adminareacode IS '都道府県コード';
COMMENT ON COLUMN clearinghousemetadatadefault_info.citycode IS '市区町村コード';
COMMENT ON COLUMN clearinghousemetadatadefault_info.deliverypoint IS '町名、番地、ビル名等';
COMMENT ON COLUMN clearinghousemetadatadefault_info.email IS '電子メールアドレス';
COMMENT ON COLUMN clearinghousemetadatadefault_info.linkage IS '問い合わせ先のHP等のURL';

