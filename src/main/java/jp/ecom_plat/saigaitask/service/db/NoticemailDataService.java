/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.noticemailData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.contains;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

/**
 * {@link NoticemailData}のサービスクラスです。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2013/09/18 16:23:17")
@org.springframework.stereotype.Repository
public class NoticemailDataService extends AbstractService<NoticemailData> {

    /**
     * 識別子でエンティティを検索します。
     *
     * @param id
     *            識別子
     * @return エンティティ
     */
    public NoticemailData findById(Long id) {
        return select().id(id).getSingleResult();
    }

//    /**
//     * 識別子の昇順ですべてのエンティティを検索します。
//     *
//     * @return エンティティのリスト
//     */
//    public List<NoticemailData> findAllOrderById() {
//        return select().orderBy(asc(id())).getResultList();
//    }

    public List<NoticemailData> check() {
    	List<NoticemailData> reslist = select().innerJoin(noticemailData().trackData())
    			.innerJoin(noticemailData().noticetypeMaster()).getResultList();
    	List<NoticemailData> nolist = new ArrayList<NoticemailData>();
    	for (NoticemailData mail : reslist) {
    		if (mail.trackData == null || mail.noticetypeMaster == null)
    			nolist.add(mail);
    	}
    	return nolist;
    }

    /**
     * 通知履歴・通知種別検索
	 *
	 * @param noticetypeid 通知種別
	 * @param pcommonssendtype コモンズ送信種別
     * @return 通知履歴
     */
    public List<NoticemailData> findByNoticetypeId(int noticetypeid, String pcommonssendtype) {
    	List<NoticemailData> rtn = new ArrayList<NoticemailData>();

    	// 避難勧告・避難指示
    	if (PubliccommonsSendType.EVACUATION_ORDER.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
      	             .innerJoin(noticemailData().noticetypeMaster())
      	             .where(
      	            		 and(eq(noticemailData().noticetypeid(), noticetypeid),
      	            			contains(noticemailData().content(), lang.__("[Number of target households]"))
      	            		)
      	            )
      	            .orderBy(desc("sendtime")).getResultList();
        // 避難所
    	} else if (PubliccommonsSendType.SHELTER.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
   	             .innerJoin(noticemailData().noticetypeMaster())
   	             .where(
   	            		 and(eq(noticemailData().noticetypeid(), noticetypeid),
   	            			contains(noticemailData().content(), lang.__("[Opening date and time]"))
   	            		)
   	            )
   	            .orderBy(desc("sendtime")).getResultList();
    	// 緊急メール速報
    	} else if (PubliccommonsSendType.URGENT_MAIL.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
      	             .innerJoin(noticemailData().noticetypeMaster())
      	             .where(
      	            		 and(eq(noticemailData().noticetypeid(), noticetypeid))
      	            )
      	            .orderBy(desc("sendtime")).getResultList();
    	// お知らせ情報
    	} else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
      	             .innerJoin(noticemailData().noticetypeMaster())
      	             .where(
      	            		 and(eq(noticemailData().noticetypeid(), noticetypeid),
      	            			contains(noticemailData().content(), lang.__("[Info identification type]"))
      	            		)
      	            )
      	            .orderBy(desc("sendtime")).getResultList();
    	// イベント情報
    	} else if (PubliccommonsSendType.EVENT.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
 	             .innerJoin(noticemailData().noticetypeMaster())
 	             .where(
 	            		 and(eq(noticemailData().noticetypeid(), noticetypeid),
 	            			contains(noticemailData().content(), lang.__("[Contact name]"))
 	            		)
 	            )
 	            .orderBy(desc("sendtime")).getResultList();
		// 被害情報
    	} else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
 	             .innerJoin(noticemailData().noticetypeMaster())
 	             .where(
 	            		 and(eq(noticemailData().noticetypeid(), noticetypeid),
 	            			contains(noticemailData().content(), lang.__("[Slightly injured]"))
 	            		)
 	            )
 	            .orderBy(desc("sendtime")).getResultList();
    	// 災害対策本部設置状況
    	} else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(pcommonssendtype)) {
    		rtn = select().innerJoin(noticemailData().trackData())
 	             .innerJoin(noticemailData().noticetypeMaster())
 	             .where(
 	            		 and(eq(noticemailData().noticetypeid(), noticetypeid),
 	            			contains(noticemailData().content(), lang.__("[HQ type]"))
 	            		)
 	            )
 	            .orderBy(desc("sendtime")).getResultList();
    	}

    	return rtn;
    }

	@Override
	public DeleteCascadeResult deleteCascade(NoticemailData entity, DeleteCascadeResult result) {

		result.cascade(NoticemailsendData.class, Names.noticemailsendData().noticemaildataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
