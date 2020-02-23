/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.AdminmenuInfo;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AdminmenuInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.43", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/08/10 10:48:25")
public class AdminmenuInfoNames {

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

    /**
     * classifyのプロパティ名を返します。
     *
     * @return classifyのプロパティ名
     */
    public static PropertyName<String> classify() {
        return new PropertyName<String>("classify");
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
     * levelのプロパティ名を返します。
     *
     * @return levelのプロパティ名
     */
    public static PropertyName<Integer> level() {
        return new PropertyName<Integer>("level");
    }

    /**
     * publicmodeのプロパティ名を返します。
     *
     * @return publicmodeのプロパティ名
     */
    public static PropertyName<Integer> publicmode() {
        return new PropertyName<Integer>("publicmode");
    }

    /**
     * groupidのプロパティ名を返します。
     *
     * @return groupidのプロパティ名
     */
    public static PropertyName<Long> groupid() {
        return new PropertyName<Long>("groupid");
    }

    /**
     * unitidのプロパティ名を返します。
     *
     * @return unitidのプロパティ名
     */
    public static PropertyName<Long> unitid() {
        return new PropertyName<Long>("unitid");
    }

    /**
     * urlのプロパティ名を返します。
     *
     * @return urlのプロパティ名
     */
    public static PropertyName<String> url() {
        return new PropertyName<String>("url");
    }

    /**
     * validのプロパティ名を返します。
     *
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
    }

    /**
     * groupInfoのプロパティ名を返します。
     *
     * @return groupInfoのプロパティ名
     */
    public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames("groupInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _AdminmenuInfoNames extends PropertyName<AdminmenuInfo> {

        /**
         * インスタンスを構築します。
         */
        public _AdminmenuInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _AdminmenuInfoNames(final String name) {
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
        public _AdminmenuInfoNames(final PropertyName<?> parent, final String name) {
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
         * classifyのプロパティ名を返します。
         *
         * @return classifyのプロパティ名
         */
        public PropertyName<String> classify() {
            return new PropertyName<String>(this, "classify");
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
         * levelのプロパティ名を返します。
         *
         * @return levelのプロパティ名
         */
        public PropertyName<Integer> level() {
            return new PropertyName<Integer>(this, "level");
        }

        /**
         * publicmodeのプロパティ名を返します。
         *
         * @return publicmodeのプロパティ名
         */
        public PropertyName<Integer> publicmode() {
            return new PropertyName<Integer>(this, "publicmode");
        }

        /**
         * groupidのプロパティ名を返します。
         *
         * @return groupidのプロパティ名
         */
        public PropertyName<Long> groupid() {
            return new PropertyName<Long>(this, "groupid");
        }

        /**
         * unitidのプロパティ名を返します。
         *
         * @return groupidのプロパティ名
         */
        public PropertyName<Long> unitid() {
            return new PropertyName<Long>(this, "unitid");
        }

        /**
         * urlのプロパティ名を返します。
         *
         * @return urlのプロパティ名
         */
        public PropertyName<String> url() {
            return new PropertyName<String>(this, "url");
        }

        /**
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
        }

        /**
         * groupInfoのプロパティ名を返します。
         *
         * @return groupInfoのプロパティ名
         */
        public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }
    }
}
