/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Listに関するユーティリティメソッドを提供します.
 */
public class ListUtil {

	/**
	 * nullか空かチェックします.
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list) {
		return list==null || list.size()==0;
	}

	/**
	 * nullか空でないかチェックします.
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(List<?> list) {
		return ! isEmpty(list);
	}

	/**
	 * IntegerのArrayListに変換します.
	 * @param list
	 * @return
	 * @throws NumberFormatException
	 */
	public static List<Integer> toIntegerList(List<?> list) throws NumberFormatException {
		List<Integer> newList = null;
		if(list!=null) {
			newList = new ArrayList<Integer>();
			for(Object e : list) {
				if(e==null) {
					newList.add(null);
				}
				else {
					String s = e.toString();
					Integer val = Integer.valueOf(s);
					newList.add(val);
				}
			}
		}
		return newList;
	}

	/**
	 * LongのArrayListに変換します.
	 * @param list
	 * @return
	 * @throws NumberFormatException
	 */
	public static List<Long> toLongList(List<?> list) throws NumberFormatException {
		List<Long> newList = null;
		if(list!=null) {
			newList = new ArrayList<Long>();
			for(Object e : list) {
				if(e==null) {
					newList.add(null);
				}
				else {
					String s = e.toString();
					Long val = Long.valueOf(s);
					newList.add(val);
				}
			}
		}
		return newList;
	}
}
