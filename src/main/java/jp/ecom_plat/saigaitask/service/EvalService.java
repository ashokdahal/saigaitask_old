/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.TablecalculateInfo;
import jp.ecom_plat.saigaitask.entity.db.TablecalculatecolumnInfo;
import jp.ecom_plat.saigaitask.service.db.TablecalculateInfoService;
import jp.ecom_plat.saigaitask.service.db.TablecalculatecolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;
import jp.hishidama.eval.ExpRuleFactory;
import jp.hishidama.eval.Expression;
import jp.hishidama.eval.func.Function;

/**
 * 四則演算等、計算を行うサービスクラスです。
 */
@org.springframework.stereotype.Service
public class EvalService {

	@Resource
	TablemasterInfoService tablemasterInfoService;
	@Resource
	TableService tableService;
	@Resource
	LayerService layerService;
	@Resource
	TablecalculateInfoService tablecalculateInfoService;
	@Resource
	TablecalculatecolumnInfoService tablecalculatecolumnInfoService;

    /** Logger */
    protected Logger logger = Logger.getLogger(AbstractAction.class);
    
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

    /**
     * 計算実行
     * @param calc 計算情報
     * @param layerid レイヤ
     * @param tblname テーブル名
     * @param fid フィーチャID
     * @return 計算結果
     */
	public Object eval(TablecalculateInfo calc, String layerid, String tblname, Long fid) {
		Object val = null;
		LayerInfo linfo = layerService.getLayerInfo(layerid);
		if (calc.function.indexOf("age") >= 0)
			val = evalTime(calc, layerid, tblname, fid);
		else {
			AttrInfo attr = linfo.getAttrInfo(calc.tablecalculatecolumnInfo.columnname);
			if(attr==null) {
				StringBuffer sb = new StringBuffer();
				sb.append(lang.__("Invalid setting.")+ lang.__("Confirm table calculation item info(ID={0}).", calc.tablecalculatecolumninfoid));
				sb.append(lang.__("In layer {0}, attribution \"{1}\" not found.", layerid, calc.tablecalculatecolumnInfo.columnname));
				throw new ServiceException(sb.toString());
			}
			else if (attr.dataType==AttrInfo.DATATYPE_INTEGER)
				val = evalLong(calc, layerid, tblname, fid);
			else if (attr.dataType==AttrInfo.DATATYPE_FLOAT)
				val = evalDouble(calc, layerid, tblname, fid);
			else if (attr.dataType==AttrInfo.DATATYPE_DATE || attr.dataType==AttrInfo.DATATYPE_DATETIME || attr.dataType==AttrInfo.DATATYPE_TIMESTAMP)
				val = evalTime(calc, layerid, tblname, fid);
			//else if (attr.dataType==AttrInfo.DATATYPE_TEXT)
			//	val = evalTime(calc, layerid, tblname, fid);
		}
		return val;
	}

	/**
	 * 整数の計算実行
     * @param calc 計算情報
     * @param layerid レイヤ
     * @param tblname テーブル名
     * @param fid フィーチャID
     * @return 計算結果
	 */
	public Long evalLong(TablecalculateInfo calc, String layerid, String tblname, Long fid) {
		try {
			Expression exp = getExpression(calc, layerid, tblname, fid, "", new MathFunction());
			if (exp != null)
				return exp.evalLong();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 浮動小数の計算実行
     * @param calc 計算情報
     * @param layerid レイヤ
     * @param tblname テーブル名
     * @param fid フィーチャID
     * @return 計算結果
	 */
	public Double evalDouble(TablecalculateInfo calc, String layerid, String tblname, Long fid) {
		try {
			Expression exp = getExpression(calc, layerid, tblname, fid, "", new MathFunction());
			if (exp != null) {
				double val = exp.evalDouble();
				BigDecimal dec = new BigDecimal(val);
				BigDecimal ret = dec.setScale(5, BigDecimal.ROUND_HALF_UP);
				return ret.doubleValue();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 時刻の計算実行
     * @param calc 計算情報
     * @param layerid レイヤ
     * @param tblname テーブル名
     * @param fid フィーチャID
     * @return 計算結果
	 */
	public Object evalTime(TablecalculateInfo calc, String layerid, String tblname, Long fid) {
		try {
			Expression exp = getExpression(calc, layerid, tblname, fid, "\"", new TimeFunction());
			if (exp != null)
				return exp.eval();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	protected Expression getExpression(TablecalculateInfo calc, String layerid, String tblname, Long fid, String prfx, Function function) {
		String func = calc.function;
		func = parseFunction(func, layerid, tblname, fid, prfx);
		if (func == null) return null;

		Expression exp = ExpRuleFactory.getDefaultRule().parse(func);
		if (function != null)
			exp.setFunction(function);
		return exp;
	}

	/**
	 * 式をパースして計算式に変換する。
	 * @param func 計算式
	 * @param layerid レイヤID
	 * @param tblname テーブル名
	 * @param fid フィーチャID
	 * @param prfx 接頭語
	 * @return 変換後の式
	 */
	public String parseFunction(String func, String layerid, String tblname, Long fid, String prfx) {
		int idx = func.indexOf("{");
		int idx2 = func.indexOf("}");
		if (idx >= 0 && idx2 >= 0) {
			String idtag = func.substring(idx, idx2+1);
			String sid = func.substring(idx+1, idx2);
			long cid = Long.parseLong(sid);
			TablecalculatecolumnInfo ccinfo = tablecalculatecolumnInfoService.findById(cid);
			if (!StringUtil.isEmpty(layerid)) {//eコミマップ
				String val = tableService.getEcomColValue(tblname, ccinfo.columnname, fid.toString());
				//if (val==null) return null;
				if (val==null) val = "0"; // 値が取得できない場合、0に変更（必ず入力しないと足し算などで演算エラーとなるため）
				func = func.replace(idtag, prfx+val+prfx);
				return parseFunction(func, layerid, tblname, fid, prfx);
			} else {//TODO:通常のテーブル

			}
		}
		return func;
	}

	/**
	 * JavaのMathクラスのFunctionクラス
	 */
@org.springframework.stereotype.Service
	public class MathFunction implements Function {

		public long evalLong(Object object, String name, Long[] args) throws Throwable {
			Class<?>[] types = new Class[args.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = long.class;
			}

			Method m = Math.class.getMethod(name, types);
			Object ret = m.invoke(null, (Object[])args);
			// return Long.parseLong(ret.toString());
			return ((Number) ret).longValue();
		}

		public double evalDouble(Object object, String name, Double[] args) throws Throwable {
			Class<?>[] types = new Class[args.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = double.class;
			}

			Method m = Math.class.getMethod(name, types);
			Object ret = m.invoke(null, (Object[])args);
			// return Long.parseLong(ret.toString());
			return ((Number) ret).doubleValue();
		}

		public Object evalObject(Object object, String name, Object[] args) throws Throwable {
			return null;
		}

		@Override
		public Object eval(String name, Object[] args) throws Exception {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public Object eval(Object object, String name, Object[] args)
				throws Exception {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}
	}

	/**
	 * 時刻計算用Functionクラス
	 */
@org.springframework.stereotype.Service
	public class TimeFunction implements Function {

		public double evalDouble(Object arg0, String arg1, Double[] arg2)
				throws Throwable {
			return 0;
		}

		public long evalLong(Object arg0, String arg1, Long[] arg2)
				throws Throwable {
			return 0;
		}

		public Object evalObject(Object arg0, String name, Object[] args) throws Throwable {
			if (name != null && name.equals("age"))
				return evalTime(arg0, name, args);
			return null;
		}

		/**
		 *
		 * @param arg0
		 * @param name
		 * @param args
		 * @return 時間long
		 * @throws Throwable
		 */
		public Object evalTime(Object arg0, String name, Object[] args) throws Throwable {
			Class<?>[] types = new Class[args.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = String.class;
			}
			/*Object[] targs = new Timestamp[args.length];

			for (int i = 0; i < args.length; i++) {
				Date date = TimeUtil.toDate(args[i]);
				if (date != null)
					targs[i] = new Timestamp(date.getTime());
			}*/

			Method m = TimeUtil.class.getMethod(name, types);
			Object ret = m.invoke(null, (Object[])args);
			// return Long.parseLong(ret.toString());
			return ((Number) ret).longValue();
		}

		@Override
		public Object eval(String name, Object[] args) throws Exception {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public Object eval(Object object, String name, Object[] args)
				throws Exception {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}
	}
}
