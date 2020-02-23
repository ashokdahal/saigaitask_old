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
 * 言語メッセージ情報
 *
 */
@Entity
@Table(name = "multilangmes_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/08/13 9:03:20")
@lombok.Getter @lombok.Setter
public class MultilangmesInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 言語情報ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long multilanginfoid;

    /** メッセージID */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String messageid;

    /** メッセージ内容 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String message;

    /** 言語情報 */
    @ManyToOne
    @JoinColumn(name="multilanginfoid")
    public MultilangInfo multilangInfo;
}
