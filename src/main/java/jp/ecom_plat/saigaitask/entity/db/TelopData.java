/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;

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
 * テロップデータ
 * 
 */
@Entity
@Table(name = "telop_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:54:48")
@lombok.Getter @lombok.Setter
public class TelopData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 種別ID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer teloptypeid;

    /** メッセージ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String message;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 掲載期限 */
    @Column(nullable = true, unique = false)
    public Timestamp viewlimit;

	@ManyToOne
	@JoinColumn(name = "localgovinfoid")
	public LocalgovInfo localgovInfo;
	
	@ManyToOne
	@JoinColumn(name = "teloptypeid")
	public TeloptypeMaster teloptypeMaster;
}
