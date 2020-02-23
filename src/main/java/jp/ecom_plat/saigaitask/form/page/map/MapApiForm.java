/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jp.ecom_plat.map.db.FeatureSearchCondition;
import jp.ecom_plat.saigaitask.form.FileForm;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;

/**
 * 地図情報APIのアクションフォームクラスです.
 */
@lombok.Getter @lombok.Setter
public class MapApiForm {

	Logger logger = Logger.getLogger(MapApiForm.class);
	private static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	// 災害対応システムとeコミマップで意識してわける必要はないかも？
	// とりあえずは、念のためにわけて書いておく。

	//============================================================
	// 災害対応システム関係
	//============================================================

	/** 地図IDの指定 */
	public long mapId;

	/** 凡例畳み込み情報グループID */
	public String legendExpandGroupId;

	/** 凡例畳み込み情報の畳み込みフラグ */
	public Boolean expanded;

	/** WKT */
	public List<String> wkt;

	/** メタデータのレコード配列 */
	public String records;

	/** メニューID */
	public Long menuid;

	/** 属性検索条件 */
	public JSONObject conditionValue;

	/** ランドマーク関連のレコード配列 */
	public String landmarkData;

	//============================================================
	// eコミマップ関係
	//============================================================

	/** eコミマップのサイトID */
	public int cid;

	/** eコミマップの地図ID */
	public long mid;

	/** eコミマップのレイヤID */
	public String layer;

	/**
	 * eコミマップのレイヤID(Comma-separate)
	 * 配列の取得は{@link #getLayersArray()}を利用してください.
	 */
	public String layers;

	/**
	 * レイヤIDを配列で取得します.
	 * @return String[] レイヤID配列、なければ null
	 */
	public String[] getLayersArray(){
		if(StringUtil.isNotEmpty(layers)) {
			return layers.split(",");
		}
		return new String[0];
	}

	/** eコミマップのフィーチャID */
	public long fid;

	/**
	 * カンマ区切りのフィーチャIDの
	 * フィーチャの書式は c##.## で、
	 * 半角コンマ区切りで複数指定可能です.
	 * 配列で取得する場合は{@link #getFeaturesArray()}を利用します.
	 */
	public String features;

	/**
	 * パラメータfeatures をパースして配列で取得します.
	 * @return String[] c##.## 形式の文字の配列
	 */
	public String[] getFeaturesArray() {
		if(StringUtil.isNotEmpty(features)) {
			return features.split(",");
		}
		return new String[0];
	}

	/**
	 * 属性ID
	 */
	public List<String> attrIds;

	/** 属性検索のキーワード */
	public String keyword;

	// 空間検索
	/**
	 * 空間検索の検索方法のID
	 * <ol>
	 * <li>範囲に含まれる</li>
	 * <li>範囲に含まれない</li>
	 * <li>範囲に完全に含まれる</li>
	 * <li>範囲に完全に含まれない</li>
	 * <li>範囲に重なる</li>
	 * <li>範囲に重ならない</li>
	 * </ol>
	 */
	public int spatialType;

	/** 空間検索の範囲のバッファ(m) */
	public double buffer;

	/**
	 * 空間検索の矩形範囲
	 * double[]での取得は{@link #getBboxDoubleArray}を利用してください.
	 */
	public String bbox;

	/**
	 * 矩形をdouble[]で取得します.
	 * @return double[] なければ null
	 */
	public double[] getBboxDoubleArray(){
		String param = bbox;
		if (param != null) {
			try {
				String[] values = param.split(",");
				return
					new double[] {
						Double.parseDouble(values[0]),
						Double.parseDouble(values[1]),
						Double.parseDouble(values[2]),
						Double.parseDouble(values[3])
				};
			} catch (NumberFormatException e) {}
		}
		return null;
	}

	/**
	 * 範囲検索の円の中心 x座標
	 */
	public double x;

	/**
	 * 範囲検索の円の中心 y座標
	 */
	public double y;

	/**
	 * 範囲検索の円の半径
	 */
	public double dist;

	/**
	 * SLDの表示ルール
	 */
	public String rule;
	
	/**
	 * SLDの表示ルール
	 */
	public HashMap<String, HashSet<String>> ruleVisible() {
		//ルール毎の表示設定 (パラメータ例 &rulevisible=layer1:0:1:2:3:4:5,layer2:1:5:6)
		HashMap<String, HashSet<String>> ruleVisible = null;
		String ruleVisibleStr = this.rule;
		if (ruleVisibleStr != null) {
			ruleVisible = new HashMap<String, HashSet<String>>();
			String[] layerRules = ruleVisibleStr.split(",");
			for (int i=0; i<layerRules.length; i++) {
				String[] idxs = layerRules[i].split(":");
				HashSet<String> idxSet = new HashSet<String>();
				for (int j=1; j<idxs.length; j++) {
					idxSet.add(idxs[j]);
				}
				ruleVisible.put(idxs[0], idxSet);
			}
		}
		return ruleVisible;
	}

	/**
	 * セッションからクリアするSLDの表示ルール
	 */
	public String clearrule;
	
	/** 検索結果数の上限 */
	public int limit;

	/** 検索結果のオフセット */
	public int offset;

	/** ファイルフォームリスト */
	public List<FileForm> files;

	/** ファイル情報リスト */
	public JSONArray fileList;

	// 検索条件パラメータ
	/** 属性検索条件キーワード */
	public String keywords;

	/** 空間検索範囲レイヤ条件 */
	public JSONArray spatialLayer;

	/** 条件反転フラグ */
	public boolean isnot;

	/** MGRSコード */
	public String mgrs;

	/** 表示データ時間 */
	public String time;

	/**
	 * @return 表示データ時間配列
	 */
	public Date[] getTimeParams() {
		try{
			if(StringUtil.isNotEmpty(time)) {
				Date d = TimeUtil.parseISO8601(time);
				return new Date[]{d};
			}
		}catch(Exception e) {
			logger.error("error time parameter: "+e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 時間パラメータのレイヤ個別指定
	 * カンマ区切り "レイヤID1,時間1,レイヤID2,時間2"
	 */
	public String layertimes;
	/**
	 * layertimes のパース結果
	 */
	public Map<String, String> layertimesMap;
	/**
	 * 
	 * @param layerId
	 * @return レイヤ個別の時間パラメータ
	 */
	public String layertime(String layerId) {
		if(StringUtil.isNotEmpty(layertimes)) {
			// Mapを初期化
			if(layertimesMap==null) {
				layertimesMap = new HashMap<String, String>();
				String layerid = null;
				String layertime = null;
				for(String param : layertimes.split(",")) {
					if(layerid==null) layerid = param;
					else {
						layertime = param;
						layertimesMap.put(layerid, layertime);
						// clear
						layerid = layertime = null;
					}
				}
			}
			return layertimesMap.get(layerId);
		}
		return null;
	}

	/**
	 * @param layertimesLayerId layertimesのレイヤID
	 * @return 表示データ時間配列
	 */
	public Date[] getTimeParams(String layertimesLayerId) {
		try{
			String time = layertime(layertimesLayerId);
			if(StringUtil.isNotEmpty(time)) {
				Date d = TimeUtil.parseISO8601(time);
				return new Date[]{d};
			}
		}catch(Exception e) {
			logger.error("error time parameter: "+e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @return 検索条件JSON形式
	 */
	public JSONObject getConditionValue() {
		JSONObject conditionValue = this.conditionValue;
		if(conditionValue==null) {
			try {
				//<div lang="ja">検索条件をJSON文字列に変換</div>
				//<div lang="en">convert search contitions into a JSON string</div>
				Vector<FeatureSearchCondition> vecFeatureSearchCondition = FeatureSearchCondition.parseKeywordString(keywords);
				JSONArray condition  = new JSONArray();
				if (vecFeatureSearchCondition != null) {
					for (FeatureSearchCondition cond : vecFeatureSearchCondition) {
						condition .put(cond.toJSON());
					}
				}
				// 検索条件JSON形式の生成
				conditionValue = new JSONObject();
				if (condition != null) conditionValue.put("condition", condition);
				conditionValue.put("buffer", buffer);
				conditionValue.put("spatial", spatialType);
				if (spatialLayer != null) conditionValue.put("spatiallayer", spatialLayer);
				if (isnot) conditionValue.put("isnot", isnot);
				if (StringUtil.isNotEmpty(layer)) conditionValue.put("layerId", layer);
			} catch(JSONException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return conditionValue;
	}

	//============================================================
	// パラメータ検証メソッド
	//============================================================

	/**
	 * {@link jp.ecom_plat.saigaitask.action.page.map.IndexAction #getEMapContents}実行メソッドの
	 * パラメータ検証メソッドです.
	 * @return ActionMessages
	 */
	public ActionMessages validateGetEMapContents(){
		// Required チェック
		return new RequiredValidator(this).layer().fid().validate();
	}

	/**
	 * {@link jp.ecom_plat.saigaitask.action.page.map.IndexAction#uploadEMapContents()}実行メソッドの
	 * パラメータ検証メソッドです.
	 * @return ActionMessages
	 */
	public ActionMessages validateUploadEMapContents(){
		// Required チェック
		return new RequiredValidator(this).mid().layer().files().validate();
	}

	/**
	 * {@link jp.ecom_plat.saigaitask.action.page.map.IndexAction #attrInfo}実行メソッドの
	 * パラメータ検証メソッドです.
	 * @return ActionMessages
	 */
	public ActionMessages validateAttrInfo(){
		// Required チェック
		return new RequiredValidator(this).layer().validate();
	}

	/**
	 * {@link jp.ecom_plat.saigaitask.action.page.map.IndexAction #eMapSeachFeatureGeometry}実行メソッドの
	 * パラメータ検証メソッドです.
	 * @return ActionMessages
	 */
	public ActionMessages validateEMapSeachFeatureGeometry() {
		return new RequiredValidator(this).mid().layer().validate();
	}

	public ActionMessages validateEMapSeachRangeWKT() {
		return new RequiredValidator(this).features().validate();
	}

	/**
	 * パラメータが指定されているかを検証するクラスです.
	 */
	public static class RequiredValidator{

		/** 検証対象のMapForm */
		MapApiForm mapApiForm;

		/** パラメータが指定されていない必須のパラメータ名リスト（検証結果） */
		List<String> requiredParams = new ArrayList<String>();

		/**
		 * 検証対象のMapFormをセットするコンストラクタです.
		 * @param mapApiForm 検証対象
		 */
		public RequiredValidator(MapApiForm mapApiForm) {
			this.mapApiForm = mapApiForm;
		}

		/**
		 * midが指定されているか検証します.
		 * @return RequiredValidator
		 */
		public RequiredValidator mid(){
			if(mapApiForm.mid==0) requiredParams.add("mid");
			return this;
		}

		/**
		 * layerが指定されているか検証します.
		 * @return RequiredValidator
		 */
		public RequiredValidator layer(){
			if(StringUtil.isEmpty(mapApiForm.layer)) requiredParams.add("layer");
			return this;
		}

		/**
		 * fidが指定されているか検証します.
		 * @return RequiredValidator
		 */
		public RequiredValidator fid(){
			if(mapApiForm.fid==0) requiredParams.add("fid");
			return this;
		}
		public RequiredValidator features(){
			if(StringUtil.isEmpty(mapApiForm.features)) requiredParams.add("features");
			return this;
		}

		/**
		 * filesが指定されているか検証します.
		 * @return RequiredValidator
		 */
		public RequiredValidator files(){
			boolean valid = true;
			if(mapApiForm.files==null) valid = false;
			else {
				for(FileForm fileForm : mapApiForm.files) {
					if(fileForm.formFile==null || fileForm.formFile.getSize()==0) valid = false;
				}
			}
			if(valid==false) {
				requiredParams.add("files");
			}
			return this;
		}

		/**
		 * 検証結果のrequiredParamsからActionMessagesに変換します.
		 * @return ActionMessages
		 */
		public ActionMessages validate(){
			ActionMessages errors = new ActionMessages();
			for(String requiredParam : requiredParams){
				errors.add(requiredParam, new ActionMessage(lang.__("{0} must be required.", requiredParam), false));
			}
			return errors;
		}
	}
}
