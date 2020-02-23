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
 * 河川マスタ
 * 
 */
@Entity
@Table(name = "meteoriver_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 19:16:16")
@lombok.Getter @lombok.Setter
public class MeteoriverMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 10, nullable = false, unique = true)
    public Integer id;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** コード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String code;

    /** WKT */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String area;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;
}
