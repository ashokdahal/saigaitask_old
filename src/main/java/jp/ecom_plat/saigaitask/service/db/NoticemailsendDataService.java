/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticemailsendData;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class NoticemailsendDataService extends AbstractService<NoticemailsendData> {

    public NoticemailsendData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    public List<NoticemailsendData> check() {
    	List<NoticemailsendData> reslist = select().innerJoin(noticemailsendData().noticemailData())
    			.innerJoin(noticemailsendData().noticgroupInfo()).getResultList();
    	List<NoticemailsendData> nolist = new ArrayList<NoticemailsendData>();
    	for (NoticemailsendData send : reslist) {
    		if (send.noticemailData == null || send.noticgroupInfo == null)
    			nolist.add(send);
    	}
    	return nolist;
    }
}
