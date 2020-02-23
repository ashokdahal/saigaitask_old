/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 言語情報
 *
 */
@Entity
@Table(name = "multilang_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/08/13 9:00:10")
@lombok.Getter @lombok.Setter
public class MultilangInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 言語コード */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String code;

    /** 言語名称 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String name;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 最終更新日時 */
    @Column(nullable = true, unique = false)
    public Timestamp updatetime;

    /** 言語メッセージ情報 */
    @OneToMany(mappedBy = "multilangInfo")
    public List<MultilangmesInfo> multilangmesInfoList;
}
