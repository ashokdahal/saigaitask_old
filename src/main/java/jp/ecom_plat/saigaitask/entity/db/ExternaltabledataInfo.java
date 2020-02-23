/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 外部リストデータ情報
 *
 */
@Entity
@Table(name = "externaltabledata_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/08/19 12:22:41")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"externalmapdataInfoList"})
public class ExternaltabledataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** メタデータの識別子 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String metadataid;

    /** フィルター */
    @Column(precision = 19, nullable = true, unique = false)
    public Long filterid;

    /** 認証情報テーブルの識別子 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long authorizationinfoid;

    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** 認証情報 */
    @ManyToOne
    @JoinColumn(name="authorizationinfoid")
    public AuthorizationInfo authorizationInfo;

    @OneToMany(mappedBy = "externaltabledataInfo")
    public List<ExternalmapdataInfo> externalmapdataInfoList;
}
