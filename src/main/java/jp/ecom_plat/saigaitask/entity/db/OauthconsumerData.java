/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * OAuthコンシューマデータ
 * 
 */
@Entity
@Table(name = "oauthconsumer_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/12/22 10:26:55")
@lombok.Getter @lombok.Setter
public class OauthconsumerData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** applicationnameプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String applicationname;

    /** コンシューマキー */
    @Column(length = 256, nullable = false, unique = false)
    public String consumerkey;

    /** コンシューマー秘密鍵 */
    @Column(length = 256, nullable = false, unique = false)
    public String consumerkeysecret;
}
