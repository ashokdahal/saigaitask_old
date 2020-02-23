package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.*;
import static org.seasar.extension.jdbc.operation.Operations.*;

import jp.ecom_plat.saigaitask.entity.db.DatatransferInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class DatatransferInfoService extends AbstractService<DatatransferInfo> {

	/**
	 * IDで検索
	 * @param id
	 * @return
	 */
    public DatatransferInfo findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return
     */
    public DatatransferInfo findByTablemasterInfoId(Long tid) {
    	return select().where(eq(datatransferInfo().tablemasterinfoid(), tid)).limit(1).getSingleResult();
    }

    /**
     * 記録IDで検索
     * @param tid
     * @return
     */
    public DatatransferInfo findByTablemasterInfoIdAndValid(Long tid) {
    	return select().where(
    			and(
    				eq(datatransferInfo().tablemasterinfoid(), tid),
    				eq(datatransferInfo().valid(), true)
    			)).limit(1).getSingleResult();
    }
}