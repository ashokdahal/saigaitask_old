package jp.ecom_plat.saigaitask.beans;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * アクションフォームの List<String>パラメータをバインドするエディタ.
 * 
 * 通常のSpringのバインドだと、String文字列内にカンマがあると勝手に区切られてしまう問題があるため、
 * たとえば、wkt=A&wkt=B というパラメータの場合は、
 * list.add("A"); list.add("B"); をした結果と同じになるようにしている。
 * 
 * 使い方
 * ・@InitBinder("アクションフォーム変数名")　の initBinder関数を Controller に追加
 * ・binder.registerCustomEditr(変数型, "変数名", new StringToListEditor()); を追加
 * <pre>
 * @InitBinder("mapApiForm")
 * public void initBinder(WebDataBinder binder, HttpServletRequest request) {
 *     binder.registerCustomEditor(List.class, "wkt", new StringToListEditor());
 * }
 * </pre>
 * 
 * [例]String文字列内にカンマがあると勝手に区切られてしまう問題
 * wkt=POLYGON((141.8978462147285+39.296348292384714%2C141.9005927967589+39.30458422301887%2C141.90711592908661+39.301130563630714%2C141.8978462147285+39.296348292384714))
 * この場合はカンマ(%2C)があるので、
 * list.add("POLYGON((141.8978462147285+39.296348292384714");
 * list.add("141.9005927967589+39.30458422301887");
 * list.add("141.90711592908661+39.301130563630714");
 * list.add("41.8978462147285+39.296348292384714))");
 * という４つの要素に分割されてしまう。
 * 
 */
public class StringToListEditor extends PropertyEditorSupport {

	List<String> list = new ArrayList<>();
	
    @Override
    public void setAsText(String text) {
        if (!StringUtils.hasText(text)) {
            setValue(null);
        }
        else {
        	list.add(text);
        }
    }

    @Override
    public Object getValue() {
    	return list;
    }

    @Override
    public void setValue(Object value) {
    	try {
    		if(value!=null) {
    			// パラメータを複数指定する場合は String[] で渡されるので、
    			// そのままListに変換する
    			// [例]wkt=A&wkt=B だと new String[]{"A","B"} が渡ってくる
    			if(value instanceof String[]) {
            		list = Arrays.asList((String[]) value);
    			}
        		return;
    		}
    	} catch(Exception e) {
    		// do nothing
    	}

    	super.setValue(value);
    }
}
