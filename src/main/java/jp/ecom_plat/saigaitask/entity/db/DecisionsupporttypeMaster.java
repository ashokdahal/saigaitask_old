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
 * 意思決定支援レイヤ種別マスタ
 * 
 */
@Entity
@Table(name = "decisionsupporttype_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2016/02/26 10:52:28")
@lombok.Getter @lombok.Setter
public class DecisionsupporttypeMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;
}
