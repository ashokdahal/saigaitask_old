/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.postingphotolayerData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.PostingphotolayerData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class PostingphotolayerDataService extends AbstractService<PostingphotolayerData> {

    public PostingphotolayerData findById(Long id) {
        return select().id(id).getSingleResult();
    }
    
    /**
     * レイヤ情報から取得
     * @param pid
     * @return
     */
    public List<PostingphotolayerData> findByPostingphotolayerInfoId(long pid) {
    	return select().where(
    			eq(postingphotolayerData().postingphotolayerinfoid(), pid)
    			).getResultList();
    }
    
    /**
     * コピー先災害IDと、コピー元GIDから検索
     * @param tid
     * @param gid
     * @return
     */
    public PostingphotolayerData findByCopytrackDataIdAndPhotoGId(long tid, long gid) {
    	return select().where(
    			and(
    				eq(postingphotolayerData().copytrackdataid(), tid),
    				eq(postingphotolayerData().photogid(), gid)
    			)).limit(1).getSingleResult();
    }
}
