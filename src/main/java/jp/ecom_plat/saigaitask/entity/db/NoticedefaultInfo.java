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
 * 通知デフォルト情報
 * 
 */
@Entity
@Table(name = "noticedefault_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:25:41")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"alarmdefaultgroupInfoList","noticedefaultgroupInfoList"})
public class NoticedefaultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long menuinfoid;

    /** 通知テンプレート種別ID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer noticetemplatetypeid;

    /** 区分 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String templateclass;
    
    /** アラームデフォルトグループ情報リスト */
    @OneToMany(mappedBy = "noticedefaultInfo")
    public List<AlarmdefaultgroupInfo> alarmdefaultgroupInfoList;

    /** メニュープロセス情報 */
    @ManyToOne
    @JoinColumn(name="menuinfoid")
    public MenuInfo menuInfo;

    /** 通知デフォルトグループ情報リスト */
    @OneToMany(mappedBy = "noticedefaultInfo")
    public List<NoticedefaultgroupInfo> noticedefaultgroupInfoList;

    /** 通知テンプレート種別マスタ */
    @ManyToOne
    @JoinColumn(name="noticetemplatetypeid")
    public NoticetemplatetypeMaster noticetemplatetypeMaster;
}
