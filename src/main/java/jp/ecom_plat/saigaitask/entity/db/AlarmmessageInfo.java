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
 * アラームメッセージ設定
 * 
 */
@Entity
@Table(name = "alarmmessage_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:48:48")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"stationalarmInfoList"})
public class AlarmmessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID（０） */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** アラームタイプID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer alarmtypeid;

    /** アラームタイプ名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 表示メッセージ （nameを置換する） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String message;

    /** メッセージ表示フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean showmessage;

    /** "success", "error", "warning", "information" */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String messagetype;

    /** 表示時間、0で手動非表示 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer duration;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** アラームタイプマスタ */
    @ManyToOne
    @JoinColumn(name="alarmtypeid")
    public AlarmtypeMaster alarmtypeMaster;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
    
    
    /** 体制アラート情報 */
    @OneToMany(mappedBy = "alarmmessageInfo")
    public List<StationalarmInfo> stationalarmInfoList;    
}
