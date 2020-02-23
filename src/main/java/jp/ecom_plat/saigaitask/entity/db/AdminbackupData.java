/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name="adminbackup_data")
@lombok.Getter @lombok.Setter
public class AdminbackupData {

    /** ID */
    @Id
    @GeneratedValue
    public Long id;

    /** 自冶体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 班ID */
    public Long groupid;

    /** 名称 */
    public String name;

    /** 管理者 */
    public Boolean admin;

    /** ファイルパス */
    public String path;

    /** 登録日時 */
    @Temporal(TemporalType.TIMESTAMP)
    public Timestamp registtime;


    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;

}
