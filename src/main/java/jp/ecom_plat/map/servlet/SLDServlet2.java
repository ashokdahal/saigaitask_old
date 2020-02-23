package jp.ecom_plat.map.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.jdbc.FilterToSQL;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.filter.IsEqualsToImpl;
import org.geotools.filter.IsGreaterThanImpl;
import org.geotools.filter.IsGreaterThanOrEqualToImpl;
import org.geotools.filter.IsLessThenImpl;
import org.geotools.filter.IsLessThenOrEqualToImpl;
import org.geotools.filter.IsNotEqualToImpl;
import org.geotools.filter.IsNullImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.StyledLayer;
import org.geotools.styling.StyledLayerDescriptor;
import org.opengis.filter.Filter;
import org.seasar.framework.util.StringUtil;

/**
 * eコミのSLDServletの付加機能を提供する.
 * 将来的には SLDServlet に統合したい.
 */
public class SLDServlet2 {

	/**
	 * SLDルールをeコミマップの検索キーワードに変換する.
	 * 検索キーワードが下記２点非対応のため、SLDから検索クエリの生成は{@link #getSLDFilterQueryMap(StyledLayerDescriptor)}の利用を推奨する.
	 * - 空文字の表現ができない -> "attr0="といった値省略形式？
	 * - IsNullの表現ができない -> "attr0 is null"といった形式？
	 * 
	 * 検索キーワードの表記一覧
	 * - 含む: attr0~*123
	 * - 含まない： attr0!~*123 
	 * - 等しい： attr0=123
	 * - 異なる： attr0<>123
	 * - 以下： attr0<=123
	 * - 以上： attr0>=123
	 * - より小さい：attr0<123
	 * - より大きい：attr0>123
	 * 
	 * @param rule SLDルール
	 * @return 検索キーワード
	 */
	public static String toFilterKeywords(Rule rule) {
		String keywords = null;

		Filter filter = rule.getFilter();
		// TODO: 値が空文字の場合は eコミの検索キーワードが非対応なので要調整
		if(filter instanceof IsEqualsToImpl) {
			IsEqualsToImpl impl = (IsEqualsToImpl) filter;
			String comparator = "=";
			keywords = impl.getExpression1().toString() + comparator + impl.getExpression2().toString();
		}
		else if (filter instanceof IsNotEqualToImpl) {
			IsNotEqualToImpl impl = (IsNotEqualToImpl) filter;
			String comparator = "<>";
			keywords = impl.getExpression1().toString() + comparator + impl.getExpression2().toString();
		}
		else if (filter instanceof IsLessThenImpl) {
			IsLessThenImpl impl = (IsLessThenImpl) filter;
			String comparator = "<";
			keywords = impl.getExpression1().toString() + comparator + impl.getExpression2().toString();
		}
		else if (filter instanceof IsLessThenOrEqualToImpl) {
			IsLessThenOrEqualToImpl impl = (IsLessThenOrEqualToImpl) filter;
			String comparator = "<=";
			keywords = impl.getExpression1().toString() + comparator + impl.getExpression2().toString();
		}
		else if (filter instanceof IsGreaterThanImpl) {
			IsGreaterThanImpl impl = (IsGreaterThanImpl) filter;
			String comparator = ">";
			keywords = impl.getExpression1().toString() + comparator + impl.getExpression2().toString();
		}
		else if (filter instanceof IsGreaterThanOrEqualToImpl) {
			IsGreaterThanOrEqualToImpl impl = (IsGreaterThanOrEqualToImpl) filter;
			String comparator = ">=";
			keywords = impl.getExpression1().toString() + comparator + impl.getExpression2().toString();
		}
		// TODO: IsNULL は eコミの検索キーワードが非対応なので要調整
		else if (filter instanceof IsNullImpl) {
			IsNullImpl impl = (IsNullImpl) filter;
			String comparator = " is null";
			keywords = impl.getExpression1().toString() + comparator;
		}
		else {
			throw new RuntimeException("unsupported filter: "+filter.toString());
		}
		
		return keywords;
	}

	/**
	 * 指定URLからSLDを取得し、SLD からフィルタクエリを生成する
	 * @param url SLDのURL
	 * @return Map<レイヤID, フィルタクエリ>
	 *         フィルタクエリが空文字の場合は描画ルール無しということである。
	 * @throws IOException
	 * @throws FilterToSQLException
	 */
	public static Map<String, String> getSLDFilterQueryMap(URL url) throws IOException, FilterToSQLException {
		StyledLayerDescriptor sld = new SLDParser(new StyleFactoryImpl(), url).parseSLD();
		return getSLDFilterQueryMap(sld);
	}

	/**
	 * SLD からフィルタクエリを生成する.
	 * @param sld SLDオブジェクト
	 * @return Map<レイヤID, フィルタクエリ>
	 *         フィルタクエリが空文字の場合は描画ルール無しということである。
	 * @throws FilterToSQLException
	 */
	public static Map<String, String> getSLDFilterQueryMap(StyledLayerDescriptor sld) throws FilterToSQLException {
		Map<String, String> map = new HashMap<>();

		// SLDをパース
		// 下記のようなlayers複数指定の場合は レイヤごとに NamedLayer が生成されている
		// http://localhost:18080/map/sld?cid=2&mid=10&layer=c42,c50&rule=c50:2:3:4:5,c42:2:3:4:5
		for(StyledLayer styledLayer : sld.getStyledLayers()) {
			NamedLayer namedLayer = (NamedLayer) styledLayer;

			// mapに登録
			String name = namedLayer.getName();
			if(name.contains(":")) name = name.split(":")[1];
			StringBuffer filterQuery = map.containsKey(name) ? new StringBuffer(map.get(name)) : new StringBuffer();

			// フィルタ無し描画ルールの存在フラグ
			boolean existsNoFilterRule = false;

			// 描画ルールにフィルタ

			// スタイルからfilterQuery を生成
			for(Style style : namedLayer.getStyles()) {
				// TODO: FeatureTypeStyle の複数対応が必要か？
				for(FeatureTypeStyle fStyle : style.featureTypeStyles()) {
					List<Rule> rules = fStyle.rules();

					// 描画ルールがある場合
					for(Rule rule : rules) {
						Filter filter = rule.getFilter();
						// フィルタ無しの描画ルール（デフォルト描画ルール）
						if(filter==null) {
							existsNoFilterRule = true;
						}
						// フィルタ有りの描画ルール（属性による描画切替）
						else {
							FilterToSQL filterToSQL = new FilterToSQL();
							String sql = filterToSQL.encodeToString(filter);
							if(StringUtil.isNotEmpty(sql)) {
								if(sql.startsWith("WHERE ")) {
									sql = sql.replaceFirst("WHERE ", ""); // WHERE が最初についてしまうので除去
									
									// 下記のように parseIntを使ったSQLになることがあるため (attr28)::integer にする
									// WHERE (attr21 = '開設済' AND (parseInt(attr28) >= 51 AND parseInt(attr28) <= 100))
									// WHERE (attr21 = '開設済' AND ((attr28)::integer >= 51 AND (attr28)::integer <= 100))
									String regex = "parseInt\\((.+?)\\)";
									Pattern p = Pattern.compile(regex);
									Matcher m = p.matcher(sql);
									sql = m.replaceAll("($1)::integer");

									// OR で連結
									if(0<filterQuery.length()) {
										filterQuery.append(" OR ");
									}
									// フィルタクエリを追加
									filterQuery.append("("+sql+")");
								}
							}
						}
					}
				}
			}

			// フィルタ無しの描画ルールなら TRUE を返す
			if(filterQuery.length()==0) {
				filterQuery.append(String.valueOf(existsNoFilterRule).toUpperCase());
			}

			map.put(name, filterQuery.toString());
		}

		return map;
	}
}
