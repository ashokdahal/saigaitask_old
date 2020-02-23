/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction;

import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNDot;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNFilter;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNId;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNLiteral;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNVal;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TagFunctionNode;

/**
 * １つのタグ（<%...%>） を処理するクラス
 */
public class TagFunction {

	public TagFunctionService tagFunctionService;
	private String tagExpr;
	protected TagFunctionNode rootNode = null;
	public String attr;
	public int rowId;
	public TagFunctionShared share;
	protected TagFunctionException error = null;
	public boolean editable = false;
	private int[] idCount = null;

	/**
	 * コンストラクタ
	 *
	 * @param tagExpr
	 * @param tagFunctionService
	 */
	protected TagFunction(String tagExpr, TagFunctionService tagFunctionService) {
		this.tagExpr = tagExpr;
		this.tagFunctionService = tagFunctionService;
	}

	/**
	 * タグ関数をパーズする
	 */
	protected void parse() {
		TagFunctionParser parser = new TagFunctionParser(this, tagExpr);
		try {
			rootNode = parser.parse();
		} catch (TagFunctionException e) {
			error = e;
		}
	}

	/**
	 * データを共有できることを示すフラグをセットする
	 *
	 * @param share
	 */
	protected void setShare(TagFunctionShared share) {
		this.share = share;
	}

	/**
	 * Ｉｄ　ノードの自動生成のために、同一のタグ式を１つにまとまる
	 */
	public void collectMissingIdNode(Map<TagFunction, int[]> autoGeneratedIds) {
		if (rootNode != null && (
				rootNode instanceof TFNVal ||
				rootNode instanceof TFNDot && rootNode.firstChild.nextSib instanceof TFNFilter)) {
			idCount = autoGeneratedIds.get(this);
			if (idCount == null) {
				idCount = new int[]{0};
				autoGeneratedIds.put(this, idCount);
			}
		}
	}

	/**
	 * .id(n) が省略された場合に、自動的に　Ｉｄ　ノードを生成する
	 */
	public void generateMissingIdNode() {
		if (rootNode != null && (
				rootNode instanceof TFNVal ||
				rootNode instanceof TFNDot && rootNode.firstChild.nextSib instanceof TFNFilter)) {
			idCount[0]++;
			//share.autoGeneratedId++;
			TagFunctionNode dot = new TFNDot(this);
			dot.addChild(rootNode);
			TFNId id = new TFNId(this);
			TFNLiteral literal = new TFNLiteral(this, TagFunctionToken.NUMBER, null, idCount[0]);
			id.addChild(literal);
			rootNode.addNext(id);
			rowId = idCount[0];
			rootNode = dot;
		}
	}

	/**
	 * ページ用のデータを取得する
	 *
	 * @param page
	 * @return
	 */
	public String execute(int page) {
		if (error == null) {
			Map<String, Object> arg = new HashMap<String, Object>();
			arg.put("page", new Integer(page));
			try {
				String value = (String)rootNode.execute(arg).get("value");
				if (value == null)
					throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
				return value;
			} catch (TagFunctionException e) {
				error = e;
			}
		}

		// エラー表示
		return "<***" + error.getMessage() + "***>";
	}

	@Override
	public boolean equals(Object tagFunction) {
		if (error != null || ((TagFunction)tagFunction).error != null)
			return false;
		return rootNode.equals(((TagFunction)tagFunction).rootNode);
	}

	@Override
	public int hashCode() {
		if (error != null)
			return 0;
		return rootNode.hashCode();
	}

}
