/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.loginData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.gt;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.LoginData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class LoginDataService extends AbstractService<LoginData> {

    public LoginData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    public List<LoginData> check() {
    	List<LoginData> reslist = select().leftOuterJoin(loginData().trackData()).where(gt(loginData().trackdataid(), 0l)).getResultList();
    	List<LoginData> nolist = new ArrayList<LoginData>();
    	for (LoginData data : reslist) {
    		if (data.trackData == null)
    			nolist.add(data);
    	}
    	reslist = select().leftOuterJoin(loginData().groupInfo()).where(gt(loginData().groupid(), 0l)).getResultList();
    	for (LoginData data : reslist) {
    		if (data.groupInfo == null)
    			nolist.add(data);
    	}
    	/*reslist = select().leftOuterJoin(loginData().disasterMaster()).where(gt(loginData().disasterid(), 0)).getResultList();
    	for (LoginData data : reslist) {
    		if (data.disasterMaster == null)
    			nolist.add(data);
    	}*/
    	reslist = select().leftOuterJoin(loginData().demoInfo()).where(
    			and(
    					ne(loginData().demoinfoid(), 0l),
    					isNotNull(loginData().demoinfoid(), null))).getResultList();
    	for (LoginData data : reslist) {
    		if (data.demoInfo == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
