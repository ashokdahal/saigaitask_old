/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.trainingplanData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrainingmeteoData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class TrainingplanDataService extends AbstractService<TrainingplanData> {

    public TrainingplanData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 削除フラグがtrueでないレコードを検索する
     * @param id
     * @return
     */
    public TrainingplanData findNotDeletedById(Long id) {
        return select()
        		.where( 
        		 and( 
        			ne(trainingplanData().deleted(), true),
        			eq(trainingplanData().id(), id)
        		)).getSingleResult();
    }

    
    /**
     * 自治体で所有する訓練プランを取得する
     * @param localgovinfoid
     * @return List<TrainingData>
     */
    public List<TrainingplanData> findByLocalgovinfoId(Long localgovinfoid){
    	return select()
    			.leftOuterJoin(trainingplanData().localgovInfo())
				//.leftOuterJoin(trainingplanData().disasterMaster())
    			.where( 
    			 and(
    				eq(trainingplanData().localgovinfoid(), localgovinfoid),
    				ne(trainingplanData().deleted(), true)
    			)).getResultList();
    }

    /**
     * 全自治体で所有する訓練プランを取得する(削除フラグがtrue以外)
     * @return List<TrainingData>
     */
    public List<TrainingplanData> findByLocalgovinfoId(){
    	return select()
    			.leftOuterJoin(trainingplanData().localgovInfo())
    			//.leftOuterJoin(trainingplanData().disasterMaster())
    			.where( ne(trainingplanData().deleted(), true) ).getResultList();
    }

	@Override
	public DeleteCascadeResult deleteCascade(TrainingplanData entity, DeleteCascadeResult result) {

		result.cascade(TrackData.class, Names.trackData().trainingplandataid(), entity.id);
		result.cascade(TrainingmeteoData.class, Names.trainingmeteoData().trainingplandataid(), entity.id);
		result.cascade(TrainingplanlinkData.class, Names.trainingplanlinkData().trainingplandataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
