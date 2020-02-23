/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.reportcontent2Data;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.Reportcontent2Data;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class Reportcontent2DataService extends AbstractService<Reportcontent2Data> {

    public Reportcontent2Data findById(Long id) {
        return select().id(id).getSingleResult();
    }

    public List<Reportcontent2Data> check() {
    	List<Reportcontent2Data> reslist = select().leftOuterJoin(reportcontent2Data().reportData()).getResultList();
    	List<Reportcontent2Data> nolist = new ArrayList<Reportcontent2Data>();
    	for (Reportcontent2Data data : reslist) {
    		if (data.reportData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }


	public List<Reportcontent2Data> findByTrackDataId(Long trackDataId) {
		List<Reportcontent2Data> rtn = select().innerJoin(reportcontent2Data().reportData())
 	             .where(
 	            		 	and(
	 	            			eq(reportcontent2Data().reportData().trackdataid(), trackDataId),
		            			ne(reportcontent2Data().reportData().filepath(), "")
	            			)
 	            )
 	            .orderBy(desc("registtime")).getResultList();
		return rtn;
	}
}
