/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames._MenutableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablerowstyleInfoNames._TablerowstyleInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablelistcolumnInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/06/20 17:37:29")
public class TablelistcolumnInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Table list item info";
	}

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

    /**
     * menutableinfoidのプロパティ名を返します。
     *
     * @return menutableinfoidのプロパティ名
     */
    public static PropertyName<Long> menutableinfoid() {
        return new PropertyName<Long>("menutableinfoid");
    }

    /**
     * attridのプロパティ名を返します。
     *
     * @return attridのプロパティ名
     */
    public static PropertyName<String> attrid() {
        return new PropertyName<String>("attrid");
    }

    /**
     * nameのプロパティ名を返します。
     *
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * editableのプロパティ名を返します。
     *
     * @return editableのプロパティ名
     */
    public static PropertyName<Boolean> editable() {
        return new PropertyName<Boolean>("editable");
    }

    /**
     * highlightのプロパティ名を返します。
     *
     * @return highlightのプロパティ名
     */
    public static PropertyName<Boolean> highlight() {
        return new PropertyName<Boolean>("highlight");
    }

    /**
     * groupingのプロパティ名を返します。
     *
     * @return groupingのプロパティ名
     */
    public static PropertyName<Boolean> grouping() {
        return new PropertyName<Boolean>("grouping");
    }

    /**
     * sortableのプロパティ名を返します。
     *
     * @return sortableのプロパティ名
     */
    public static PropertyName<Boolean> sortable() {
        return new PropertyName<Boolean>("sortable");
    }

    /**
     * defaultsortのプロパティ名を返します。
     *
     * @return defaultsortのプロパティ名
     */
    public static PropertyName<Integer> defaultsort() {
        return new PropertyName<Integer>("defaultsort");
    }

    /**
     * uploadableのプロパティ名を返します。
     *
     * @return uploadableのプロパティ名
     */
    public static PropertyName<Boolean> uploadable() {
        return new PropertyName<Boolean>("uploadable");
    }

    /**
     * loggableのプロパティ名を返します。
     *
     * @return loggableのプロパティ名
     */
    public static PropertyName<Boolean> loggable() {
        return new PropertyName<Boolean>("loggable");
    }

    /**
     * disporderのプロパティ名を返します。
     *
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * defaultcheckのプロパティ名を返します。
     * 
     * @return defaultcheckのプロパティ名
     */
    public static PropertyName<Boolean> defaultcheck() {
        return new PropertyName<Boolean>("defaultcheck");
    }

    /**
     * groupdefaultcheckのプロパティ名を返します。
     * 
     * @return groupdefaultcheckのプロパティ名
     */
    public static PropertyName<Boolean> groupdefaultcheck() {
        return new PropertyName<Boolean>("groupdefaultcheck");
    }

    /**
     * addableのプロパティ名を返します。
     * 
     * @return addableのプロパティ名
     */
    public static PropertyName<Boolean> addable() {
        return new PropertyName<Boolean>("addable");
    }
    
    /**
     * tablerowstyleInfoListのプロパティ名を返します。
     *
     * @return tablerowstyleInfoListのプロパティ名
     */
    public static _TablerowstyleInfoNames tablerowstyleInfoList() {
        return new _TablerowstyleInfoNames("tablerowstyleInfoList");
    }

    /**
     * menutableInfoのプロパティ名を返します。
     *
     * @return menutableInfoのプロパティ名
     */
    public static _MenutableInfoNames menutableInfo() {
        return new _MenutableInfoNames("menutableInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TablelistcolumnInfoNames extends PropertyName<TablelistcolumnInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TablelistcolumnInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TablelistcolumnInfoNames(final String name) {
            super(name);
        }

        /**
         * インスタンスを構築します。
         *
         * @param parent
         *            親
         * @param name
         *            名前
         */
        public _TablelistcolumnInfoNames(final PropertyName<?> parent, final String name) {
            super(parent, name);
        }

        /**
         * idのプロパティ名を返します。
         *
         * @return idのプロパティ名
         */
        public PropertyName<Long> id() {
            return new PropertyName<Long>(this, "id");
        }

        /**
         * menutableinfoidのプロパティ名を返します。
         *
         * @return menutableinfoidのプロパティ名
         */
        public PropertyName<Long> menutableinfoid() {
            return new PropertyName<Long>(this, "menutableinfoid");
        }

        /**
         * attridのプロパティ名を返します。
         *
         * @return attridのプロパティ名
         */
        public PropertyName<String> attrid() {
            return new PropertyName<String>(this, "attrid");
        }

        /**
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * editableのプロパティ名を返します。
         *
         * @return editableのプロパティ名
         */
        public PropertyName<Boolean> editable() {
            return new PropertyName<Boolean>(this, "editable");
        }

        /**
         * highlightのプロパティ名を返します。
         *
         * @return highlightのプロパティ名
         */
        public PropertyName<Boolean> highlight() {
            return new PropertyName<Boolean>(this, "highlight");
        }

        /**
         * groupingのプロパティ名を返します。
         *
         * @return groupingのプロパティ名
         */
        public PropertyName<Boolean> grouping() {
            return new PropertyName<Boolean>(this, "grouping");
        }

        /**
         * sortableのプロパティ名を返します。
         *
         * @return sortableのプロパティ名
         */
        public PropertyName<Boolean> sortable() {
            return new PropertyName<Boolean>(this, "sortable");
        }

        /**
         * defaultsortのプロパティ名を返します。
         *
         * @return defaultsortのプロパティ名
         */
        public PropertyName<Integer> defaultsort() {
            return new PropertyName<Integer>(this, "defaultsort");
        }

        /**
         * uploadableのプロパティ名を返します。
         *
         * @return uploadableのプロパティ名
         */
        public PropertyName<Boolean> uploadable() {
            return new PropertyName<Boolean>(this, "uploadable");
        }

        /**
         * loggableのプロパティ名を返します。
         *
         * @return loggableのプロパティ名
         */
        public PropertyName<Boolean> loggable() {
            return new PropertyName<Boolean>(this, "loggable");
        }

        /**
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * defaultcheckのプロパティ名を返します。
         *
         * @return defaultcheckのプロパティ名
         */
        public PropertyName<Boolean> defaultcheck() {
            return new PropertyName<Boolean>(this, "defaultcheck");
        }

        /**
         * groupdefaultcheckのプロパティ名を返します。
         *
         * @return groupdefaultcheckのプロパティ名
         */
        public PropertyName<Boolean> groupdefaultcheck() {
            return new PropertyName<Boolean>(this, "groupdefaultcheck");
        }

        /**
         * addのプロパティ名を返します。
         *
         * @return addのプロパティ名
         */
        public PropertyName<Boolean> add() {
            return new PropertyName<Boolean>(this, "add");
        }
        
        /**
         * tablerowstyleInfoListのプロパティ名を返します。
         *
         * @return tablerowstyleInfoListのプロパティ名
         */
        public _TablerowstyleInfoNames tablerowstyleInfoList() {
            return new _TablerowstyleInfoNames(this, "tablerowstyleInfoList");
        }

        /**
         * menutableInfoのプロパティ名を返します。
         *
         * @return menutableInfoのプロパティ名
         */
        public _MenutableInfoNames menutableInfo() {
            return new _MenutableInfoNames(this, "menutableInfo");
        }
    }
}