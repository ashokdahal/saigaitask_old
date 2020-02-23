/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.reportcontentData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.ReportcontentData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class ReportcontentDataService extends AbstractService<ReportcontentData> {

    public ReportcontentData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録データIDから検索
     * @param id
     */
//	public List<ReportcontentData> findByTrackdataId(Long tid) {
//		return select().innerJoin(reportcontentData().reportData())
//				.where(eq(reportcontentData().reportData().trackdataid(), tid))
//				.orderBy(desc(reportcontentData().id()))
//				.getResultList();
//	}
	public List<ReportcontentData> findByTrackDataId(Long tid) {
		List<ReportcontentData> rtn = select().innerJoin(reportcontentData().reportData())
 	             .where(
 	            		 	and(
	 	            			eq(reportcontentData().reportData().trackdataid(), tid),
		            			ne(reportcontentData().reportData().filepath(), "")
	            			)
 	            )
 	            .orderBy(desc("registtime")).getResultList();
		return rtn;
	}

	public List<ReportcontentData> check() {
    	List<ReportcontentData> reslist = select().leftOuterJoin(reportcontentData().reportData()).getResultList();
    	List<ReportcontentData> nolist = new ArrayList<ReportcontentData>();
    	for (ReportcontentData data : reslist) {
    		if (data.reportData == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
