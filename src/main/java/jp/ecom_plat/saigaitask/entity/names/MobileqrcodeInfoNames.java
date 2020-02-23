package jp.ecom_plat.saigaitask.entity.names;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;

import java.sql.Timestamp;
import javax.annotation.Generated;
import jp.ecom_plat.saigaitask.entity.db.MobileqrcodeInfo;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UnitInfoNames._UnitInfoNames;
import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MobileqrcodeInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2018/04/04 18:09:43")
public class MobileqrcodeInfoNames implements EntityNameInterface {

    @Override
    public String entityName() {
		return "Mobile QR code Info";
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
     * localgovinfoidのプロパティ名を返します。
     * 
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
    }

    /**
     * oauthconsumeridのプロパティ名を返します。
     * 
     * @return oauthconsumeridのプロパティ名
     */
    public static PropertyName<Long> oauthconsumerid() {
        return new PropertyName<Long>("oauthconsumerid");
    }

    /**
     * titleのプロパティ名を返します。
     * 
     * @return titleのプロパティ名
     */
    public static PropertyName<String> title() {
        return new PropertyName<String>("title");
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
     * tablemasterinfoidのプロパティ名を返します。
     * 
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
    }

    /**
     * authenticationdateのプロパティ名を返します。
     * 
     * @return authenticationdateのプロパティ名
     */
    public static PropertyName<Timestamp> authenticationdate() {
        return new PropertyName<Timestamp>("authenticationdate");
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
     * groupのプロパティ名を返します。
     * 
     * @return groupのプロパティ名
     */
    public static _GroupInfoNames group() {
        return new _GroupInfoNames("group");
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
     * tablemasterinfoのプロパティ名を返します。
     * 
     * @return tablemasterinfoのプロパティ名
     */
    public static _TablemasterInfoNames tablemasterinfo() {
        return new _TablemasterInfoNames("tablemasterinfo");
    }

    /**
     * unitのプロパティ名を返します。
     * 
     * @return unitのプロパティ名
     */
    public static _UnitInfoNames unit() {
        return new _UnitInfoNames("unit");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MobileqrcodeInfoNames extends PropertyName<MobileqrcodeInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MobileqrcodeInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MobileqrcodeInfoNames(final String name) {
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
        public _MobileqrcodeInfoNames(final PropertyName<?> parent, final String name) {
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
         * oauthconsumeridのプロパティ名を返します。
         * 
         * @return oauthconsumeridのプロパティ名
         */
        public PropertyName<Long> oauthconsumerid() {
            return new PropertyName<Long>(this, "oauthconsumerid");
        }

        /**
         * titleのプロパティ名を返します。
         *
         * @return titleのプロパティ名
         */
        public PropertyName<String> title() {
            return new PropertyName<String>(this, "title");
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
         * @return unitidのプロパティ名
         */
        public PropertyName<Long> unitid() {
            return new PropertyName<Long>(this, "unitid");
        }

        /**
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
        }

        /**
         * authenticationdateのプロパティ名を返します。
         *
         * @return authenticationdateのプロパティ名
         */
        public PropertyName<Timestamp> authenticationdate() {
            return new PropertyName<Timestamp>(this, "authenticationdate");
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
         * groupのプロパティ名を返します。
         * 
         * @return groupのプロパティ名
         */
        public _GroupInfoNames group() {
            return new _GroupInfoNames(this, "group");
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
         * tablemasterinfoのプロパティ名を返します。
         * 
         * @return tablemasterinfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterinfo() {
            return new _TablemasterInfoNames(this, "tablemasterinfo");
        }

        /**
         * unitのプロパティ名を返します。
         * 
         * @return unitのプロパティ名
         */
        public _UnitInfoNames unit() {
            return new _UnitInfoNames(this, "unit");
        }
    }
}
