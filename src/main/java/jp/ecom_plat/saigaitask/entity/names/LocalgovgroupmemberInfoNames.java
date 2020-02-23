package jp.ecom_plat.saigaitask.entity.names;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;

import javax.annotation.Generated;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupmemberInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovgroupInfoNames._LocalgovgroupInfoNames;
import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link LocalgovgroupmemberInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2018/10/09 12:13:47")
public class LocalgovgroupmemberInfoNames implements EntityNameInterface {

    @Override
    public String entityName() {
        return "自治体グループメンバー情報";
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
     * localgovgroupinfoidのプロパティ名を返します。
     * 
     * @return localgovgroupinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovgroupinfoid() {
        return new PropertyName<Long>("localgovgroupinfoid");
    }

    /**
     * localgovinfoidのプロパティ名を返します。
     * 
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
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
     * validのプロパティ名を返します。
     * 
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
    }

    /**
     * localgovinfoのプロパティ名を返します。
     * 
     * @return localgovinfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovinfo() {
        return new _LocalgovInfoNames("localgovinfo");
    }

    /**
     * localgovgroupinfoのプロパティ名を返します。
     * 
     * @return localgovgroupinfoのプロパティ名
     */
    public static _LocalgovgroupInfoNames localgovgroupinfo() {
        return new _LocalgovgroupInfoNames("localgovgroupinfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _LocalgovgroupmemberInfoNames extends PropertyName<LocalgovgroupmemberInfo> {

        /**
         * インスタンスを構築します。
         */
        public _LocalgovgroupmemberInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _LocalgovgroupmemberInfoNames(final String name) {
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
        public _LocalgovgroupmemberInfoNames(final PropertyName<?> parent, final String name) {
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
         * localgovgroupinfoidのプロパティ名を返します。
         *
         * @return localgovgroupinfoidのプロパティ名
         */
        public PropertyName<Long> localgovgroupinfoid() {
            return new PropertyName<Long>(this, "localgovgroupinfoid");
        }

        /**
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
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
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
        }

        /**
         * localgovinfoのプロパティ名を返します。
         * 
         * @return localgovinfoのプロパティ名
         */
        public _LocalgovInfoNames localgovinfo() {
            return new _LocalgovInfoNames(this, "localgovinfo");
        }

        /**
         * localgovgroupinfoのプロパティ名を返します。
         * 
         * @return localgovgroupinfoのプロパティ名
         */
        public _LocalgovgroupInfoNames localgovgroupinfo() {
            return new _LocalgovgroupInfoNames(this, "localgovgroupinfo");
        }
    }
}
