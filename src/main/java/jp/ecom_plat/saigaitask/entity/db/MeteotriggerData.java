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
 * 気象情報トリガーデータ
 * 
 */
@Entity
@Table(name = "meteotrigger_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/09/09 20:38:21")
@lombok.Getter @lombok.Setter
public class MeteotriggerData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** トリガーID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long meteotriggerinfoid;

    /** トリガー発生時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp triggertime;

    /** 災害モード起動済 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean startup;

    /** 通知グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticegroupinfoid;

    /** 体制ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long stationclassinfoid;

    /** 職員参集メール送信済 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean assemblemail;

    /** 避難勧告済 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean issue;

    /** 避難情報文字列 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String issuetext;

    /** 公共コモンズ送信済 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommons;

    /** エリアメール送信済 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean publiccommonsmail;

    /** SNS送信フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean sns;

	@ManyToOne
	@JoinColumn(name = "localgovinfoid")
	public LocalgovInfo localgovInfo;
	
	@ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;
	
	@ManyToOne
	@JoinColumn(name = "meteotriggerinfoid")
	public MeteotriggerInfo meteotriggerInfo;
	
	@ManyToOne
	@JoinColumn(name = "noticegroupinfoid")
	public NoticegroupInfo noticegroupInfo;
	
	@ManyToOne
	@JoinColumn(name = "stationclassinfoid")
	public StationclassInfo stationclassInfo;
}
