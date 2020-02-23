package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;
import jp.ecom_plat.saigaitask.entity.db.JsonimportapiInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JsonimportapiInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2018/09/20 13:32:10")
public class JsonimportapiInfoNames {

    /**
     * idのプロパティ名を返します。
     * 
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
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
     * testingのプロパティ名を返します。
     * 
     * @return testingのプロパティ名
     */
    public static PropertyName<Boolean> testing() {
        return new PropertyName<Boolean>("testing");
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
     * authkeyのプロパティ名を返します。
     * 
     * @return authkeyのプロパティ名
     */
    public static PropertyName<String> authkey() {
        return new PropertyName<String>("authkey");
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
     * intervalのプロパティ名を返します。
     * 
     * @return intervalのプロパティ名
     */
    public static PropertyName<Integer> interval() {
        return new PropertyName<Integer>("interval");
    }

    /**
     * noteのプロパティ名を返します。
     * 
     * @return noteのプロパティ名
     */
    public static PropertyName<String> note() {
        return new PropertyName<String>("note");
    }

    /**
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _JsonimportapiInfoNames extends PropertyName<JsonimportapiInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JsonimportapiInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _JsonimportapiInfoNames(final String name) {
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
        public _JsonimportapiInfoNames(final PropertyName<?> parent, final String name) {
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
        }

        /**
         * testingのプロパティ名を返します。
         *
         * @return testingのプロパティ名
         */
        public PropertyName<Boolean> testing() {
            return new PropertyName<Boolean>(this, "testing");
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
         * authkeyのプロパティ名を返します。
         *
         * @return authkeyのプロパティ名
         */
        public PropertyName<String> authkey() {
            return new PropertyName<String>(this, "authkey");
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
         * intervalのプロパティ名を返します。
         *
         * @return intervalのプロパティ名
         */
        public PropertyName<Integer> interval() {
            return new PropertyName<Integer>(this, "interval");
        }

        /**
         * noteのプロパティ名を返します。
         *
         * @return noteのプロパティ名
         */
        public PropertyName<String> note() {
            return new PropertyName<String>(this, "note");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}