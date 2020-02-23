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
 * FacebookMasterエンティティクラス
 *
 */
@Entity
@Table(name = "facebook_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/08/29 18:38:59")
@lombok.Getter @lombok.Setter
public class FacebookMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** idプロパティ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 10, nullable = false, unique = true)
    public Integer id;

    /** appidプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String appid;

    /** appsecretプロパティ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String appsecret;
}
