package jp.ecom_plat.saigaitask.service;
import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;


@org.springframework.stereotype.Service
public class EarthQuakeLayerService {
	
	@Resource 
	protected JdbcManager jdbcManager;

	public String GetClosestEarthquake(double lat, double lon, double depth, double mag) {
		String query = "SELECT file_name FROM earthquake_layer where magnitude = "+mag+" ORDER BY ST_Distance(location,ST_GeomFromText('POINT("+lon+" "+lat+")', 0)) LIMIT 1;";
		String result = jdbcManager.selectBySql(String.class, query.toString()).getSingleResult();		
		return result;
	}	
}

