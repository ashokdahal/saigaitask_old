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
 * インポート記録テーブル情報
 *
 */
@Entity
@Table(name = "importtracktable_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/02/20 18:45:14")
@lombok.Getter @lombok.Setter
public class ImporttracktableInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録テーブル情報ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long tracktableinfoid;

    /** 旧レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String layerid;

    /** 旧レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String oldlayerid;

    /** importtrackinfoidプロパティ */
    @Column(precision = 19, nullable = false, unique = false)
    public Long importtrackinfoid;

    /** インポート記録情報 */
    @ManyToOne
    @JoinColumn(name="importtrackinfoid")
    public ImporttrackInfo importtrackInfo;
}
