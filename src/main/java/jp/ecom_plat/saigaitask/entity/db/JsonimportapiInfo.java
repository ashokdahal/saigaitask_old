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
 * JSON連携API設定テーブル
 * 
 */
@Entity
@Table(name = "jsonimportapi_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2018/09/20 11:57:34")
@lombok.Getter @lombok.Setter
public class JsonimportapiInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** URL */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String url;

    /** 認証キー */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String authkey;

    /** 有効/無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 間隔 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer interval;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}