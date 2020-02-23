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
 * データ判定結果スタイル情報
 *
 */
@Entity
@Table(name = "judgeresultstyle_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/03/08 10:30:48")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"judgeresultstyleDataList"})
public class JudgeresultstyleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** データ判定管理ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long judgemaninfoid;

    /** スタイル文字列 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String style;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    @OneToMany(mappedBy = "judgeresultstyleInfo")
    public List<JudgeresultstyleData> judgeresultstyleDataList;

    /** データ判定管理情報 */
    @ManyToOne
    @JoinColumn(name="judgemaninfoid")
    public JudgemanInfo judgemanInfo;
}
