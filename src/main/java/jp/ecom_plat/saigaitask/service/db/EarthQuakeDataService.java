package jp.ecom_plat.saigaitask.service.db;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;

import jp.ecom_plat.saigaitask.entity.db.EarthquakeLayer;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.TableService;

@org.springframework.stereotype.Repository
public class EarthQuakeDataService extends AbstractService<EarthquakeLayer>{

	@Resource
	protected ServletContext application;
	@Resource
	protected UserTransaction userTransaction;
	@Resource
	protected TableService tableService;
	
	public List<EarthquakeLayer> getAll(){
		List<EarthquakeLayer> result = select().getResultList();
		return result;
	}
}
