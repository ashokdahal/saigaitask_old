/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TablelistkarteInfo;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames._MenutableInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablelistkarteInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/11/02 12:22:42")
public class TablelistkarteInfoNames implements EntityNameInterface{

	@Override
	public String entityName() {
		return "Table list karte info";
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
     * closedのプロパティ名を返します。
     *
     * @return closedのプロパティ名
     */
    public static PropertyName<Boolean> closed() {
        return new PropertyName<Boolean>("closed");
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
     * tipsのプロパティ名を返します。
     *
     * @return tipsのプロパティ名
     */
    public static PropertyName<String> tips() {
        return new PropertyName<String>("tips");
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
    public static class _TablelistkarteInfoNames extends PropertyName<TablelistkarteInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TablelistkarteInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TablelistkarteInfoNames(final String name) {
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
        public _TablelistkarteInfoNames(final PropertyName<?> parent, final String name) {
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
         * closedのプロパティ名を返します。
         *
         * @return closedのプロパティ名
         */
        public PropertyName<Boolean> closed() {
            return new PropertyName<Boolean>(this, "closed");
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
         * tipsのプロパティ名を返します。
         *
         * @return tipsのプロパティ名
         */
        public PropertyName<String> tips() {
            return new PropertyName<String>(this, "tips");
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
