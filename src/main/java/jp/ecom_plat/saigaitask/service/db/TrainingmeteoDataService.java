/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.meteorequestInfo;
import static jp.ecom_plat.saigaitask.entity.Names.trainingmeteoData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.TrainingmeteoData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class TrainingmeteoDataService extends AbstractService<TrainingmeteoData> {

    public TrainingmeteoData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * プランで所有する外部データ一覧を取得する
     * @param trainingplanid
     * @return List<TrainingmeteoInfo>
     */
    public List<TrainingmeteoData> findByPlandataId(Long trainingplandataid){
    	return select()
    			.innerJoin(meteorequestInfo().meteotypeMaster())
    			.where( 
    			and(
    				eq(trainingmeteoData().trainingplandataid(), trainingplandataid),
    				ne(trainingmeteoData().deleted(), true)
    			)).orderBy( desc(trainingmeteoData().id()) ).getResultList();
    }
    
    /**
     * idと訓練プランIDを利用して検索。
     * @param id : trainingmeteo_dataのid
     * @param trainingplandataid : 該当プランのtrainingplan_data のid
     * @return
     */
    public TrainingmeteoData findByIdAndPlanId(Long id, Long trainingplandataid){
    	return select()
    			.where( 
    			and(
    				eq(trainingmeteoData().id(), id),
    				eq(trainingmeteoData().trainingplandataid(), trainingplandataid),
    				ne(trainingmeteoData().deleted(), true)
    			)).orderBy( desc(trainingmeteoData().id()) ).getSingleResult();

    }
}
