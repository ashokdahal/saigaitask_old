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
 * アラームデフォルトグループ情報
 *
 */
@Entity
@Table(name = "alarmdefaultgroup_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:49:25")
@lombok.Getter @lombok.Setter
public class AlarmdefaultgroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 通知デフォルトID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticedefaultinfoid;

    /** 通知グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** デフォルトON/OFF */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean defaulton;

    /** "success", "error", "warning", "information" */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String messagetype;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;

    /** 通知デフォルト情報 */
    @ManyToOne
    @JoinColumn(name="noticedefaultinfoid")
    public NoticedefaultInfo noticedefaultInfo;
}
