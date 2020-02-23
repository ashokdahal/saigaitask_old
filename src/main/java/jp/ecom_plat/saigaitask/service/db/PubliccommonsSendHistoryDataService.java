/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.publiccommonsSendHistoryData;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendHistoryData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class PubliccommonsSendHistoryDataService extends AbstractService<PubliccommonsSendHistoryData> {

    public PubliccommonsSendHistoryData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    public List<PubliccommonsSendHistoryData> check() {
    	List<PubliccommonsSendHistoryData> reslist = select().leftOuterJoin(publiccommonsSendHistoryData().publiccommonsReportData())
    			.leftOuterJoin(publiccommonsSendHistoryData().publiccommonsSendToInfo()).getResultList();
    	List<PubliccommonsSendHistoryData> nolist = new ArrayList<PubliccommonsSendHistoryData>();
    	for (PubliccommonsSendHistoryData data : reslist) {
    		if (data.publiccommonsReportData == null || data.publiccommonsSendToInfo == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
