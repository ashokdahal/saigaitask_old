/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.trainingplanlinkData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class TrainingplanlinkDataService extends AbstractService<TrainingplanlinkData> {

    public TrainingplanlinkData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 訓練プランの連携自治体IDをListで取得する
     * @param trainingplandataid
     * @return List<trainingplanlinkData>
     */
    public List<TrainingplanlinkData> findByTrainingplandataId(Long trainingplandataid){
    	return select()
    			.leftOuterJoin(trainingplanlinkData().localgovInfo())
				.leftOuterJoin(trainingplanlinkData().trainingplanData())
    			.where( eq(trainingplanlinkData().trainingplandataid(), trainingplandataid) ).orderBy(asc(trainingplanlinkData().localgovinfoid())).getResultList();
    }

    /**
     * planinfoidとlocalgovinfoidの条件に合うレコードを検索
     * @param trainingplandataid
     * @param localgovinfoid
     * @return List<trainingplanlinkData>
     */
    public TrainingplanlinkData findByLocalgovinfoIdPlaninfoId(Long trainingplandataid, Long localgovinfoid){
    	return select()
    			.leftOuterJoin(trainingplanlinkData().localgovInfo())
				.leftOuterJoin(trainingplanlinkData().trainingplanData())
    			.where( and(
    					eq(trainingplanlinkData().trainingplandataid(), trainingplandataid),
    					eq(trainingplanlinkData().localgovinfoid(), localgovinfoid))
    			).getSingleResult();
    }


    /**
     * plandataidとlocalgovinfoidの条件に合うレコードがあればtrue
     * @param trainingplandataid
     * @param localgovinfoid
     * @return true or false
     */
    public boolean checktrainingplanlinkDataId(Long trainingplandataid, Long localgovinfoid){
    	TrainingplanlinkData trainingplanData = select().where( and( eq(trainingplanlinkData().trainingplandataid(), trainingplandataid), eq(trainingplanlinkData().localgovinfoid(), localgovinfoid) ) ).getSingleResult();
    	if(trainingplanData != null) return true;
    	else return false;
    }

}
