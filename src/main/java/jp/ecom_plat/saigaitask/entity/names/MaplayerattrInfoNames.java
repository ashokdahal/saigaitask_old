/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames._MaplayerInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MaplayerattrInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 15:32:00")
public class MaplayerattrInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Map layer attribute info";
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
     * maplayerinfoidのプロパティ名を返します。
     * 
     * @return maplayerinfoidのプロパティ名
     */
    public static PropertyName<Long> maplayerinfoid() {
        return new PropertyName<Long>("maplayerinfoid");
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
     * disporderのプロパティ名を返します。
     * 
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * maplayerInfoのプロパティ名を返します。
     * 
     * @return maplayerInfoのプロパティ名
     */
    public static _MaplayerInfoNames maplayerInfo() {
        return new _MaplayerInfoNames("maplayerInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MaplayerattrInfoNames extends PropertyName<MaplayerattrInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MaplayerattrInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MaplayerattrInfoNames(final String name) {
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
        public _MaplayerattrInfoNames(final PropertyName<?> parent, final String name) {
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
         * maplayerinfoidのプロパティ名を返します。
         *
         * @return maplayerinfoidのプロパティ名
         */
        public PropertyName<Long> maplayerinfoid() {
            return new PropertyName<Long>(this, "maplayerinfoid");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * maplayerInfoのプロパティ名を返します。
         * 
         * @return maplayerInfoのプロパティ名
         */
        public _MaplayerInfoNames maplayerInfo() {
            return new _MaplayerInfoNames(this, "maplayerInfo");
        }
    }
}
