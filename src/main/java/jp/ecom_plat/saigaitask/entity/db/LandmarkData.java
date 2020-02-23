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
 * ランドマークデータ
 *
 */
@Entity
@Table(name = "landmark_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/04/10 17:43:12")
@lombok.Getter @lombok.Setter
public class LandmarkData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** ランドマーク情報ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long landmarkinfoid;

    /** ランドマークデータ登録者のグループID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long groupid;

    /** ランドマークデータ文字列 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String landmark;

    /** ランドマークデータ緯度 */
    @Column(precision = 17, scale = 17, nullable = false, unique = false)
    public Double latitude;

    /** ランドマークデータ経度 */
    @Column(precision = 17, scale = 17, nullable = false, unique = false)
    public Double longitude;

    /** landmarkinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "landmarkinfoid", referencedColumnName = "id")
    public LandmarkInfo landmarkinfo;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;
}
