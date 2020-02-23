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
 * テーブル項目マスタ
 * 
 */
@Entity
@Table(name = "tablecolumn_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/08/08 17:17:33")
@lombok.Getter @lombok.Setter
public class TablecolumnMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 10, nullable = false, unique = true)
    public Integer id;

    /** テーブル名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String tablename;

    /** 項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String columnname;

    /** 空欄可 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean nullable;
}
