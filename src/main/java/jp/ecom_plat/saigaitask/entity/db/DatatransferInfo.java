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
 * DatatransferInfoエンティティクラス
 * 
 */
@Entity
@Table(name = "datatransfer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2017/06/13 11:39:40")
@lombok.Getter @lombok.Setter
public class DatatransferInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** フォーマット */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String format;

    /** 転送プロトコル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String protocol;

    /** ホスト */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String host;

    /** ポート番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String port;

    /** 認証 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String authentication;

    /** ユーザID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String userid;

    /** パスワード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String password;

    /** ディレクトリ */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String directory;

    /** 1:更新時,2:定期的 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer transfertype;

    /** 送信日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String crontext;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;

}