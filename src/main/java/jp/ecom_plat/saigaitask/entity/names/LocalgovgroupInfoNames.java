package jp.ecom_plat.saigaitask.entity.names;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;

import javax.annotation.Generated;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovgroupmemberInfoNames._LocalgovgroupmemberInfoNames;
import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link LocalgovgroupInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2018/10/09 12:13:47")
public class LocalgovgroupInfoNames implements EntityNameInterface {

    @Override
    public String entityName() {
        return "自治体グループ情報";
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
     * nameのプロパティ名を返します。
     * 
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
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
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
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
     * localgovgroupmemberInfoListのプロパティ名を返します。
     * 
     * @return localgovgroupmemberInfoListのプロパティ名
     */
    public static _LocalgovgroupmemberInfoNames localgovgroupmemberInfoList() {
        return new _LocalgovgroupmemberInfoNames("localgovgroupmemberInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _LocalgovgroupInfoNames extends PropertyName<LocalgovgroupInfo> {

        /**
         * インスタンスを構築します。
         */
        public _LocalgovgroupInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _LocalgovgroupInfoNames(final String name) {
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
        public _LocalgovgroupInfoNames(final PropertyName<?> parent, final String name) {
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
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
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
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
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
         * localgovgroupmemberInfoListのプロパティ名を返します。
         * 
         * @return localgovgroupmemberInfoListのプロパティ名
         */
        public _LocalgovgroupmemberInfoNames localgovgroupmemberInfoList() {
            return new _LocalgovgroupmemberInfoNames(this, "localgovgroupmemberInfoList");
        }
    }
}
