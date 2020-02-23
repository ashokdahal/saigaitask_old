/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.telemeterData;
import static org.seasar.extension.jdbc.operation.Operations.desc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.seasar.extension.jdbc.parameter.Parameter;
import org.seasar.extension.jdbc.where.SimpleWhere;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleData;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * テレメータデータのエンティティに対する操作を格納するクラス
 */
@org.springframework.stereotype.Repository
public class TelemeterDataService extends AbstractService<TelemeterData> {

	/**
	 * IDで検索
	 * @param id
	 * @return テレメータデータ
	 */
    public TelemeterData findById(Long id) {
        return select().id(id).getSingleResult();
    }

	/**
	 * データを取得
	 * @param code 観測所コード
	 * @param itemcode データ項目コード 
	 * @param num
	 * @return
	 */
    public List<TelemeterData> getData(Long code, Integer itemCode, Integer num, Date[] timeParam) {
    	SimpleWhere where = new SimpleWhere();
    	where.eq(telemeterData().code(), code);
    	where.eq(telemeterData().itemcode(), itemCode);
    	if(timeParam!=null) {
    		if(timeParam.length==1) {
        		where.le(telemeterData().observtime(), timeParam[0]);
    		}
    		else throw new ServiceException("unsupport time parameter: "+Arrays.toString(timeParam));
    	}
    	return select().leftOuterJoin(telemeterData().judgeresultstyleDataList())
    			.leftOuterJoin(telemeterData().judgeresultstyleDataList().judgeresultstyleInfo())
    			.leftOuterJoin(telemeterData().judgeresultstyleDataList().judgeresultstyleInfo().judgemanInfo())
    			.where(where)
    			.orderBy(desc(telemeterData().observtime())).limit(num).getResultList();
    }

	/**
	 * データを取得
	 * @param code 観測所コード
	 * @param itemcode データ項目コード 
	 * @param num
	 * @return
	 */
    public List<TelemeterData> getHourData(Long code, Integer itemCode, Integer num, Date[] timeParam) {
    	StringBuffer whereSQL = new StringBuffer();
    	List<Object> whereParams = new ArrayList<>();
    	// condition: minute
    	whereSQL.append("date_part('minute', observtime)=?");
    	whereParams.add(0);
    	// condition: code
    	whereSQL.append(" and "+telemeterData().code()+"=?");
    	whereParams.add(code);
    	// condition: itemCode
    	whereSQL.append(" and "+telemeterData().itemcode()+"=?");
    	whereParams.add(itemCode);
    	if(timeParam!=null) {
    		if(timeParam.length==1) {
    	    	// condition: timestamp
    	    	whereSQL.append(" and "+telemeterData().observtime()+"<=?");
    	    	whereParams.add(Parameter.timestamp(timeParam[0]));
    		}
    		else throw new ServiceException("unsupport time parameter: "+Arrays.toString(timeParam));
    	}

    	return select().leftOuterJoin(telemeterData().judgeresultstyleDataList())
    			.leftOuterJoin(telemeterData().judgeresultstyleDataList().judgeresultstyleInfo())
    			.leftOuterJoin(telemeterData().judgeresultstyleDataList().judgeresultstyleInfo().judgemanInfo())
    			.where(whereSQL.toString(), whereParams)
    			.orderBy(desc(telemeterData().observtime())).limit(num).getResultList();
    }

	@Override
	public DeleteCascadeResult deleteCascade(TelemeterData entity, DeleteCascadeResult result) {

		result.cascade(JudgeresultstyleData.class, Names.judgeresultstyleData().telemeterdataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
