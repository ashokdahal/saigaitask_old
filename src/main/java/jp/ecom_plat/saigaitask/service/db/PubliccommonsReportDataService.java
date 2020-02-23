/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.publiccommonsReportData;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.category;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.documentId;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.documentIdSerial;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.documentRevision;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.localgovinfoid;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.status;
import static jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames.trackdataid;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastAntidisaster;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastDamage;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastEvent;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastGeneral;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastRefuge;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastShelter;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendHistoryData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.ListUtil;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;

@org.springframework.stereotype.Repository
public class PubliccommonsReportDataService extends AbstractService<PubliccommonsReportData> {

    public PubliccommonsReportData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
	 * 公共情報コモンズデータからファイル名を取得します.
	 * @param publiccommonsData
	 * @return fileName
	 */
	public String getFileName(PubliccommonsReportData publiccommonsData) {
		String fileName = null;
		if(publiccommonsData!=null) {
			fileName = getDocumentUid(publiccommonsData) + "." + publiccommonsData.documentRevision + ".xml";
		}
		return fileName;
	}

	/**
	 * 公共情報コモンズのドキュメントIDを取得します
	 * @param publiccommonsData
	 * @return ドキュメントID
	 */
	public String getDocumentUid(PubliccommonsReportData publiccommonsData) {
		// 設定ファイルから取得した頭文字+記録データID＋発信種別＋シリアル値
		return getDocumentUid(publiccommonsData.trackdataid, publiccommonsData.category, publiccommonsData.documentIdSerial);
	}

	/**
	 * 公共情報コモンズのドキュメントIDを取得します
	 * @param publiccommonsData
	 * @return ドキュメントID
	 */
	public String getDocumentUid(Long trackdataid, String category, int documentIdSerial) {
		// 設定ファイルから取得した頭文字+記録データID＋発信種別＋シリアル値
		return PublicCommonsUtils.getDocumentIdHead() + trackdataid + "-" + category + "-" + documentIdSerial;
	}

	/**
	 * ドキュメントID連番の最大値を取得します.
	 * ない場合は 0 を返します.
	 * @param trackdataid 記録データID
	 * @param category 情報種別
	 * @return
	 */
	public int getMaxDocumentIdSerial(Long trackdataid, String category) {
		SearchOption option = new SearchOption().trackdataid(trackdataid).category(category);
		List<PubliccommonsReportData> publiccommonsDataList = search(option).getResultList();
		return getMaxDocumentIdSerial(publiccommonsDataList);
	}

	/**
	 * おしらせ、イベント情報用ドキュメントID連番の最大値を取得します.
	 * ない場合は 0 を返します.
	 * @param trackdataid 記録データID
	 * @param documentid ドキュメントID
	 * @return
	 */
	public int getMaxGeneralDocumentIdSerial(Long trackdataid, String documentid) {
		SearchOption option = new SearchOption().trackdataid(trackdataid).documentId(documentid);
		List<PubliccommonsReportData> publiccommonsDataList = search(option).getResultList();
		return getMaxDocumentIdSerial(publiccommonsDataList);
	}

	/**
	 * ドキュメントID連番の最大値を取得します.
	 * ない場合は 0 を返します.
	 * @param publiccommonsDataList
	 * @return
	 */
	public int getMaxDocumentIdSerial(List<PubliccommonsReportData> publiccommonsDataList) {
		int lastDocumentIdSerial = 0;
		if(publiccommonsDataList!=null) {
			for(PubliccommonsReportData publiccommonsData : publiccommonsDataList) {
				Integer documentIdSerial = publiccommonsData.documentIdSerial;
				lastDocumentIdSerial = Math.max(lastDocumentIdSerial, documentIdSerial.intValue());
			}
		}
		return lastDocumentIdSerial;
	}

	/**
	 * 検索オプションで検索する.
	 * @param option
	 * @return select
	 */
	public AutoSelect<PubliccommonsReportData> search(SearchOption option) {
		AutoSelect<PubliccommonsReportData> select = select();

		// Where句
		SimpleWhere where = new SimpleWhere();
		if(ListUtil.isNotEmpty(option.localgovinfoid)) where.in(localgovinfoid(), option.localgovinfoid);
		if(ListUtil.isNotEmpty(option.trackdataid)) where.in(trackdataid(), option.trackdataid);
		if(ListUtil.isNotEmpty(option.category)) where.in(category(), option.category);
		if(ListUtil.isNotEmpty(option.status)) where.in(status(), option.status);
		if(ListUtil.isNotEmpty(option.documentid)) where.in(documentId(), option.documentid);
		if(ListUtil.isNotEmpty(option.documentidserial)) where.in(documentIdSerial(), option.documentidserial);
		if(0<where.getParams().length) select.where(where);

		return select;
	}

	/**
	 * 公共情報コモンズ発信データの検索オプション
	 */
	public static class SearchOption{
		// フィールド
		public List<Long> localgovinfoid = new ArrayList<Long>();
		public List<Long> trackdataid = new ArrayList<Long>();
		public List<String> category = new ArrayList<String>();
		public List<String> documentid = new ArrayList<String>();
		public List<String> status = new ArrayList<String>();
		public List<String> documentidserial = new ArrayList<String>();

		// 追加メソッド
		public SearchOption trackdataid(Long trackdataid) {this.trackdataid.add(trackdataid); return this;}
		public SearchOption category(String category) {this.category.add(category); return this;}
		public SearchOption documentId(String documentId) {this.documentid.add(documentId); return this;}
		public SearchOption status(String status) {this.status.add(status); return this;}
		public SearchOption document_id_serial(String documentIdSerial) {this.documentidserial.add(documentIdSerial);return this;}
	}

	/**
	 * 記録データと情報種別とドキュメントIDの連番で版情報を検索する.
	 * @param trackdataid 記録データ
	 * @param category 情報種別
	 * @param documentIdSerial ドキュメントIDの連番
	 * @return 公共情報コモンズ発信データのリスト
	 */
	public List<PubliccommonsReportData> findRevisionDatas(Long trackdataid, String category, String documentIdSerial) {
//		SearchOption option = new SearchOption().trackdataid(trackdataid).category(category);
		SearchOption option = new SearchOption().trackdataid(trackdataid).category(category).document_id_serial(documentIdSerial);
		return search(option).orderBy(asc(documentRevision())).getResultList();
	}

	/**
	 * 指定ドキュメントIDの版番号の最大値を取得する.
	 * @param trackdataid 記録データID
	 * @param category 情報種別
	 * @param documentId ドキュメントID
	 * @return
	 */
	public int getMaxDocumentRevision(String documentId) {
		SearchOption option = new SearchOption().documentId(documentId);
		List<PubliccommonsReportData> publiccommonsDataList = search(option).getResultList();
		return getMaxDocumentRevision(publiccommonsDataList);
	}

	/**
	 * 指定ドキュメントIDの版番号の最大値を取得する．
	 * @param publiccommonsDataList
	 * @return lastDocumentRevision
	 */
	public int getMaxDocumentRevision(List<PubliccommonsReportData> publiccommonsDataList) {
		int lastDocumentRevision = 0;
		if(publiccommonsDataList!=null) {
			for(PubliccommonsReportData publiccommonsData : publiccommonsDataList) {
				Integer documentRevision = publiccommonsData.documentRevision;
				lastDocumentRevision = Math.max(lastDocumentRevision, documentRevision.intValue());
			}
		}
		return lastDocumentRevision;
	}

	/**
	 * @return nolist
	 */
	public List<PubliccommonsReportData> check() {
		List<PubliccommonsReportData> reslist = select().leftOuterJoin(publiccommonsReportData().localgovInfo())
				.leftOuterJoin(publiccommonsReportData().trackData()).getResultList();
		List<PubliccommonsReportData> nolist = new ArrayList<PubliccommonsReportData>();
		for (PubliccommonsReportData data : reslist) {
			if (data.localgovInfo == null || data.trackData == null)
				nolist.add(data);
		}
		return nolist;
	}

    /**
     * 種別検索
	 *
	 * @param category 避難勧告・指示
     * @return 送信履歴
     */
    public List<PubliccommonsReportData> findByNoticetypeCategory(String category) {
		return select().where(
				eq(publiccommonsReportData().category(), category)
				).orderBy(desc(publiccommonsReportData().id())).getResultList();
    }

    /**
	 * 発信が成功した公共情報コモンズ発信データを取得します
	 * @param category カテゴリ名
	 * @param trackdataid 記録データID
	 * @return
	 */
    public List<PubliccommonsReportData> findByCategoryTracjdataid(String category, Long trackdataid) {
    	if (trackdataid == 0) {
	    	return select().where(
	    					and(
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().status(), "SEND")
	    						)
	    			).orderBy(desc(publiccommonsReportData().sendtime())).getResultList();
    	} else {
	    	return select().where(
					and(
						eq(publiccommonsReportData().trackdataid(), trackdataid),
						eq(publiccommonsReportData().category(), category),
						eq(publiccommonsReportData().status(), "SEND")
						)
			).orderBy(desc(publiccommonsReportData().sendtime())).getResultList();
    	}
    }

    /**
	 * 発信が成功した公共情報コモンズ発信データと最終送信履歴を取得します
	 * @param category カテゴリ名
	 * @param trackdataid 記録データID (避難勧告・避難指示、避難所は災害時のみ発信する仕様のため必須指定)
	 * @param localgovinfoid 自治体ID (被害情報、お知らせ、イベントは記録データIDがないため必須指定)
	 * @param chikuName 地区名(避難勧告・避難指示のみ任意指定)
	 * @return
	 */
    public List<PubliccommonsReportData> findByReportDataWithLast(String category, Long trackdataid, Long localgovinfoid, String chikuName) {
    	List<PubliccommonsReportData> pReportList = new ArrayList<PubliccommonsReportData>();

		// 避難勧告・避難指示
		if (PubliccommonsSendType.EVACUATION_ORDER.equals(category)) {
			// 地区名が指定されている場合
			if (StringUtil.isNotEmpty(chikuName)) {
				pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastRefugeList())
					.where(
							and(
								eq(publiccommonsReportData().trackdataid(), trackdataid),
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().publiccommonsReportDataLastRefugeList().chikuname(), chikuName),
								eq(publiccommonsReportData().status(), "SEND")
								)
						)
						.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();
			} else {
				pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastRefugeList())
						.where(
								and(
									eq(publiccommonsReportData().trackdataid(), trackdataid),
									eq(publiccommonsReportData().category(), category),
									eq(publiccommonsReportData().status(), "SEND")
									)
							)
						.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();
			}
		// 避難所
		} else if (PubliccommonsSendType.SHELTER.equals(category)) {
			pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastShelterList())
					.where(
							and(
								eq(publiccommonsReportData().trackdataid(), trackdataid),
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().status(), "SEND")
							)
					)
		.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();
		// 被害情報
		} else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(category)) {
			pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastDamageList())
		    		.where(
							and(
								eq(publiccommonsReportData().localgovinfoid(), localgovinfoid),
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().status(), "SEND")
							)
		    		)
		    		.orderBy(desc(publiccommonsReportData().createtime())).getResultList();
		// イベント
		} else if (PubliccommonsSendType.EVENT.equals(category)) {
			pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastEventList())
		    		.where(
							and(
								eq(publiccommonsReportData().localgovinfoid(), localgovinfoid),
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().status(), "SEND")
							)
		    		)
		    		.orderBy(desc(publiccommonsReportData().createtime())).getResultList();
		// お知らせ
		} else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(category)) {
			pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastGeneralList())
		    		.where(
							and(
								eq(publiccommonsReportData().localgovinfoid(), localgovinfoid),
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().status(), "SEND")
							)
		    		)
		    		.orderBy(desc(publiccommonsReportData().createtime())).getResultList();
			//災害対策本部設置状況
		} else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(category)) {
			pReportList = select().innerJoin(publiccommonsReportData().publiccommonsReportDataLastAntidisaster())
					.where(
							and(
								eq(publiccommonsReportData().trackdataid(), trackdataid),
								eq(publiccommonsReportData().category(), category),
								eq(publiccommonsReportData().status(), "SEND")
							)
					)
			.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();
		}
    	return pReportList;
    }

    /**
	 * 全解除対象の公共情報コモンズ発信データを取得します
	 * @param trackdataid 記録データID
	 * @return
	 */
    public List<PubliccommonsReportData> findByReportDataByIdEvacuationOrder(Long trackdataid) {
    	// 最後の発信を取得
        List<PubliccommonsReportData> report = select().where(
						and(
							eq(publiccommonsReportData().trackdataid(), trackdataid),
							eq(publiccommonsReportData().category(), PubliccommonsSendType.EVACUATION_ORDER),
							ne(publiccommonsReportData().status(), "cancelSend"),	// 取消発信は除外
							eq(publiccommonsReportData().status(), "SEND")			// 発信成功分のみ
							)
					)
				.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();

        if (report.size() == 0) {
        	return null;
        } else {
        	// 最後の発信の発令内容を取得
			return select().innerJoin(
					publiccommonsReportData().publiccommonsReportDataLastRefugeList())
					.where(eq(publiccommonsReportData().id(), report.get(0).id))
					.getResultList();
        }
    }

    /**
	 * 全閉鎖対象の公共情報コモンズ発信データを取得します
	 * @param trackdataid 記録データID
	 * @return
	 */
    public List<PubliccommonsReportData> findByReportDataByIdShelter(Long trackdataid) {
    	// 最後の発信を取得
        List<PubliccommonsReportData> report = select().where(
						and(
							eq(publiccommonsReportData().trackdataid(), trackdataid),
							eq(publiccommonsReportData().category(), PubliccommonsSendType.SHELTER),
							ne(publiccommonsReportData().status(), "cancelSend"),	// 取消発信は除外
							eq(publiccommonsReportData().status(), "SEND")			// 発信成功分のみ
							)
					)
				.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();

        if (report.size() == 0) {
        	return null;
        } else {
        	// 最後の発信の発令内容を取得
			return select().innerJoin(
					publiccommonsReportData().publiccommonsReportDataLastShelterList())
					.where(eq(publiccommonsReportData().id(), report.get(0).id))
					.getResultList();
        }
    }

    /**
	 * 全解散対象の公共情報コモンズ発信データを取得します
	 * @param trackdataid 記録データID
	 * @return
	 */
    public List<PubliccommonsReportData> findByReportDataByIdAntidisaster(Long trackdataid) {
    	// 最後の発信を取得
        List<PubliccommonsReportData> report = select().where(
						and(
							eq(publiccommonsReportData().trackdataid(), trackdataid),
							eq(publiccommonsReportData().category(), PubliccommonsSendType.ANTIDISASTER_HEADQUARTER),
							ne(publiccommonsReportData().status(), "cancelSend"),	// 取消発信は除外
							eq(publiccommonsReportData().status(), "SEND")			// 発信成功分のみ
							)
					)
				.orderBy(desc(publiccommonsReportData().sendtime())).getResultList();

        if (report.size() == 0) {
        	return null;
        } else {
        	// 最後の発信の発令内容を取得
			return select().innerJoin(
					publiccommonsReportData().publiccommonsReportDataLastAntidisaster())
					.where(eq(publiccommonsReportData().id(), report.get(0).id))
					.getResultList();
        }
    }

	@Override
	public DeleteCascadeResult deleteCascade(PubliccommonsReportData entity, DeleteCascadeResult result) {

		result.cascade(PubliccommonsReportDataLastAntidisaster.class, Names.publiccommonsReportDataLastAntidisaster().pcommonsreportdataid(), entity.id);
		result.cascade(PubliccommonsReportDataLastDamage.class, Names.publiccommonsReportDataLastDamage().pcommonsreportdataid(), entity.id);
		result.cascade(PubliccommonsReportDataLastEvent.class, Names.publiccommonsReportDataLastEvent().pcommonsreportdataid(), entity.id);
		result.cascade(PubliccommonsReportDataLastGeneral.class, Names.publiccommonsReportDataLastGeneral().pcommonsreportdataid(), entity.id);
		result.cascade(PubliccommonsReportDataLastRefuge.class, Names.publiccommonsReportDataLastRefuge().pcommonsreportdataid(), entity.id);
		result.cascade(PubliccommonsReportDataLastShelter.class, Names.publiccommonsReportDataLastShelter().pcommonsreportdataid(), entity.id);
		result.cascade(PubliccommonsSendHistoryData.class, Names.publiccommonsSendHistoryData().publiccommonsReportDataId(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
