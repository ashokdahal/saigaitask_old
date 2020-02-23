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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 気象情報等取得種別マスタ
 *
 */
@Entity
@Table(name = "meteotype_master")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 18:55:54")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"meteorequestInfoList","meteoxsltInfoList","meteolayerInfoList"})
public class MeteotypeMaster implements Serializable {

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

    /** 取得種別名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String type;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 気象情報等取得情報リスト */
    @OneToMany(mappedBy = "meteotypeMaster")
    public List<MeteorequestInfo> meteorequestInfoList;

    /** 気象情報XSLT情報リスト */
    @OneToMany(mappedBy = "meteotypeMaster")
    public List<MeteoxsltInfo> meteoxsltInfoList;

    /** 気象情報レイヤ情報リスト */
    @OneToMany(mappedBy = "meteotypeMaster")
    public List<MeteolayerInfo> meteolayerInfoList;
}
