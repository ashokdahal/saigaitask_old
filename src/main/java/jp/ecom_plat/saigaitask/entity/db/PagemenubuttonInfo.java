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
 * ページボタン表示マスタ
 *
 */
@Entity
@Table(name = "pagemenubutton_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/16 16:16:56")
@lombok.Getter @lombok.Setter
public class PagemenubuttonInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** ページボタンID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer pagebuttonid;

    /** リンク */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String href;

    /** ターゲット名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String target;

    /** 利用可フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean enable;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** メニュー情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** ページボタンマスタ */
    @ManyToOne
    @JoinColumn(name="pagebuttonid")
    public PagebuttonMaster pagebuttonMaster;
}
