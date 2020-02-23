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
 * テーブル演算情報
 *
 */
@Entity
@Table(name = "tablecalculate_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/12/05 16:24:02")
@lombok.Getter @lombok.Setter
public class TablecalculateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 計算結果の項目 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablecalculatecolumninfoid;

    /** 計算式 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String function;

	@ManyToOne
	@JoinColumn(name = "tablecalculatecolumninfoid")
	public TablecalculatecolumnInfo tablecalculatecolumnInfo;
}
