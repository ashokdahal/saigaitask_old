package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 自治体グループメンバー情報
 * 
 */
@Entity
@Table(name = "localgovgroupmember_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2018/10/09 12:13:37")
@lombok.Getter @lombok.Setter
public class LocalgovgroupmemberInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovgroupinfoid;

    /** 子自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** localgovinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "localgovinfoid", referencedColumnName = "id")
    public LocalgovInfo localgovinfo;

    /** localgovgroupinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "localgovgroupinfoid", referencedColumnName = "id")
    public LocalgovgroupInfo localgovgroupinfo;
}