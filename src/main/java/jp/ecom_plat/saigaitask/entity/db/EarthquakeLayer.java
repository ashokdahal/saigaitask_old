package jp.ecom_plat.saigaitask.entity.db;

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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "earthquake_layer")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class EarthquakeLayer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;
	
	@Column(precision = 19, nullable = true, unique = false)
    public float magnitude;
	
	@Column(precision = 19, nullable = true, unique = false)
    public float depth;
	
	@Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String file_name;
	
	@Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String location;
	
  
	

}
