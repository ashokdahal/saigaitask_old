/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.autocompleteData;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.AutocompleteData;
import jp.ecom_plat.saigaitask.service.AbstractService;

@org.springframework.stereotype.Repository
public class AutocompleteDataService extends AbstractService<AutocompleteData> {

    public AutocompleteData findById(Long id) {
        return select().id(id).getSingleResult();
    }

    /**
     * 候補文字列の配列を返す。
     * @param govid 自治体ID
     * @param groupid 班ID
     * @param table テーブル名
     * @param column 項目名
     * @return 文字列配列
     */
    public String[] findArray(Long govid, Long groupid, Long tableid, String column) {
    	List<AutocompleteData> list = find(govid, groupid, tableid, column);
    	String[] values = new String[list.size()];
    	int idx = 0;
    	for (AutocompleteData data : list)
    		values[idx++] = data.string;
    	return values;
    }
    
    /**
     * オートコンプリートデータを検索する
     * @param govid 自治体ＩＤ
     * @param groupid 班ＩＤ
     * @param table テーブル名
     * @param column 項目名
     * @return オートコンプリートデータリスト
     */
    public List<AutocompleteData> find(Long govid, Long groupid, Long tableid, String column) {
    	return jdbcManager.from(AutocompleteData.class).innerJoin(autocompleteData().autocompleteInfo()).where(
    			and(
    				eq(autocompleteData().autocompleteInfo().localgovinfoid(), govid),
    				eq(autocompleteData().autocompleteInfo().tablemasterinfoid(), tableid),
    				eq(autocompleteData().autocompleteInfo().columnname(), column)
    			)).orderBy(desc(autocompleteData().count()), desc(autocompleteData().lasttime())).limit(20).getResultList();
    }
    
    /**
     * 文字列で検索
     * @param value 値文字列
     * @return オートコンプリートデータ
     */
    public AutocompleteData	findByAutocompleteInfoIdAndString(Long infoid, String value) {
    	return select().where(and(
    			eq(autocompleteData().string(), value),
    			eq(autocompleteData().autocompleteinfoid(), infoid)
    		)).limit(1).getSingleResult();
    }
    
    /**
     * 文字列の登録数を登録する
     * @param iid オートコンプリート情報ID
     * @param value 登録文字列
     */
    public void set(Long iid, String value) {
    	AutocompleteData data = findByAutocompleteInfoIdAndString(iid, value);
    	if (data == null) {
    		data = new AutocompleteData();
    		data.autocompleteinfoid = iid;
    		data.string = value;
    		data.count = 1;
    		data.lasttime = new Timestamp(System.currentTimeMillis());
    		insert(data);
    	}
    	else {
    		data.count = data.count+1;
    		data.lasttime = new Timestamp(System.currentTimeMillis());
    		update(data);
    	}
    }
    
    public List<AutocompleteData> check() {
    	List<AutocompleteData> reslist = select().leftOuterJoin(autocompleteData().autocompleteInfo()).getResultList();
    	List<AutocompleteData> nolist = new ArrayList<AutocompleteData>();
    	for (AutocompleteData data : reslist) {
    		if (data.autocompleteInfo == null)
    			nolist.add(data);
    	}
    	return nolist;
    }
}
