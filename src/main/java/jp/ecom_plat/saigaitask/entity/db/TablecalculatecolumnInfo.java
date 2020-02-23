/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
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
 * テーブル演算項目情報
 *
 */
@Entity
@Table(name = "tablecalculatecolumn_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/12/05 16:21:02")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"tablecalculateList"})
public class TablecalculatecolumnInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 項目名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String columnname;

	@OneToMany(mappedBy = "tablecalculatecolumnInfo")
	public List<TablecalculateInfo> tablecalculateList;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}
