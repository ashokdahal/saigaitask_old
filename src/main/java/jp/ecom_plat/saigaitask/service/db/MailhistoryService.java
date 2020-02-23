/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticemailData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.List;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * {@link Mailhistory}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/09/18 16:23:17")
@org.springframework.stereotype.Repository
public class MailhistoryService extends AbstractService<NoticemailData> {

	/**
	 * 通知データリスト取得
	 * @param trackdataid 記録データID
	 * @param noticetypeid 通知種別ID
	 * @param numPage 現在のページ番号
	 * @return 通知データリスト
	 */
    public List<NoticemailData> findByNoticetypeId(Long trackdataid, int noticetypeid, String numPage, int pageDataCnt) {
    	// 記録データIDがある場合は結合
    	if (trackdataid > 0) {
	    	return select().innerJoin(noticemailData().trackData())
					             .innerJoin(noticemailData().noticetypeMaster())
					  .where(
					  and(
					  eq(noticemailData().trackdataid(), trackdataid),
					  eq(noticemailData().noticetypeid(), noticetypeid)
					  ))
					  .orderBy(desc(noticemailData().sendtime())
					  )
					  .limit(pageDataCnt)
	                 .offset((Integer.parseInt(numPage)-1)*pageDataCnt)
					 .getResultList();
    	} else {
		    return select().innerJoin(noticemailData().noticetypeMaster())
				  .where(
				  and(
				  eq(noticemailData().trackdataid(), trackdataid),
				  eq(noticemailData().noticetypeid(), noticetypeid)
				  ))
				  .orderBy(desc(noticemailData().sendtime())
				  )
				  .limit(pageDataCnt)
		        .offset((Integer.parseInt(numPage)-1)*pageDataCnt)
				 .getResultList();
    	}
    }

    /**
     * データ数取得
     * @param trackdataid 通知データID
     * @param noticetypeid 通知種別ID
     * @return データ数
     */
    public long dataCount(Long trackdataid, int noticetypeid) {
		return select().innerJoin(noticemailData().trackData())
				             .innerJoin(noticemailData().noticetypeMaster())
				  .where(
				  and(
				  eq(noticemailData().trackdataid(), trackdataid),
				  eq(noticemailData().noticetypeid(), noticetypeid)
				  ))
				  .getCount();
    }
}
