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
 * JSON連携更新対象レイヤテーブル
 * 
 */
@Entity
@Table(name = "jsonimportlayer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2018/09/20 15:10:14")
@lombok.Getter @lombok.Setter
public class JsonimportlayerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** jsonimportapiinfoidプロパティ */
    @Column(precision = 19, nullable = true, unique = false)
    public Long jsonimportapiinfoid;

    /** 区分 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String category;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 整理番号属性 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String noattr;

    /** Contents属性 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String contentsattr;

    /** Category_text属性 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String categorytextattr;

    /** Subject属性 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String subjectattr;

    /** 受信日時属性 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String receptiondatetimeattr;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** JSON連携API設定 */
    @ManyToOne
    @JoinColumn(name="jsonimportapiinfoid")
    public LocalgovInfo jsonimportapiInfo;

    /** テーブルマスタ情報リスト */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}