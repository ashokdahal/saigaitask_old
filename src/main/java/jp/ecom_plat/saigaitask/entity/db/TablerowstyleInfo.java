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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 属性行スタイル情報
 * 
 */
@Entity
@Table(name = "tablerowstyle_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/23 11:43:39")
@lombok.Getter @lombok.Setter
public class TablerowstyleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** テーブルリスト項目ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablelistcolumninfoid;

    /** 値 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String val;

    /** スタイル文字列 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String style;

    /** テーブルリスト項目情報 */
    @ManyToOne
    @JoinColumn(name="tablelistcolumninfoid")
    public TablelistcolumnInfo tablelistcolumnInfo;
}
