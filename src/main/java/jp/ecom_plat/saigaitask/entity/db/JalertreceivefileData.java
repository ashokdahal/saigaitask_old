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
 * JAlert受信データ情報
 *
 */
@Entity
@Table(name = "jalertreceivefile_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/03/13 16:24:12")
@lombok.Getter @lombok.Setter
public class JalertreceivefileData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** JAlert取得ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long jalertrequestinfoid;

    /** ファイル種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer jalerttypeid;

    /** 受信日時 */
    @Column(nullable = true, unique = false)
    public Timestamp receivetime;

    /** テキストファイル名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String orgtextfilename;

    /** データファイル名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String orgdatafilename;

    /** テキストファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String textfilepath;

    /** ファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String filepath;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
