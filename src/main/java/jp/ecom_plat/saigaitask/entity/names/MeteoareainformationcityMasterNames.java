/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;
import jp.ecom_plat.saigaitask.entity.db.MeteoareainformationcityMaster;
import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteoareainformationcityMaster}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/09/09 18:18:57")
public class MeteoareainformationcityMasterNames {

    /**
     * idのプロパティ名を返します。
     * 
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

    /**
     * codeのプロパティ名を返します。
     * 
     * @return codeのプロパティ名
     */
    public static PropertyName<String> code() {
        return new PropertyName<String>("code");
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
     * namewarnのプロパティ名を返します。
     * 
     * @return namewarnのプロパティ名
     */
    public static PropertyName<String> namewarn() {
        return new PropertyName<String>("namewarn");
    }

    /**
     * warnflagのプロパティ名を返します。
     * 
     * @return warnflagのプロパティ名
     */
    public static PropertyName<Boolean> warnflag() {
        return new PropertyName<Boolean>("warnflag");
    }

    /**
     * tatsumakiflagのプロパティ名を返します。
     * 
     * @return tatsumakiflagのプロパティ名
     */
    public static PropertyName<Boolean> tatsumakiflag() {
        return new PropertyName<Boolean>("tatsumakiflag");
    }

    /**
     * landslideflagのプロパティ名を返します。
     * 
     * @return landslideflagのプロパティ名
     */
    public static PropertyName<Boolean> landslideflag() {
        return new PropertyName<Boolean>("landslideflag");
    }

    /**
     * riverflagのプロパティ名を返します。
     * 
     * @return riverflagのプロパティ名
     */
    public static PropertyName<Boolean> riverflag() {
        return new PropertyName<Boolean>("riverflag");
    }

    /**
     * nameseismicのプロパティ名を返します。
     * 
     * @return nameseismicのプロパティ名
     */
    public static PropertyName<String> nameseismic() {
        return new PropertyName<String>("nameseismic");
    }

    /**
     * namevolcanoのプロパティ名を返します。
     * 
     * @return namevolcanoのプロパティ名
     */
    public static PropertyName<String> namevolcano() {
        return new PropertyName<String>("namevolcano");
    }

    /**
     * pointのプロパティ名を返します。
     * 
     * @return pointのプロパティ名
     */
    public static PropertyName<String> point() {
        return new PropertyName<String>("point");
    }

    /**
     * lineのプロパティ名を返します。
     * 
     * @return lineのプロパティ名
     */
    public static PropertyName<String> line() {
        return new PropertyName<String>("line");
    }

    /**
     * polygonのプロパティ名を返します。
     * 
     * @return polygonのプロパティ名
     */
    public static PropertyName<String> polygon() {
        return new PropertyName<String>("polygon");
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
     * @author S2JDBC-Gen
     */
    public static class _MeteoareainformationcityMasterNames extends PropertyName<MeteoareainformationcityMaster> {

        /**
         * インスタンスを構築します。
         */
        public _MeteoareainformationcityMasterNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MeteoareainformationcityMasterNames(final String name) {
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
        public _MeteoareainformationcityMasterNames(final PropertyName<?> parent, final String name) {
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
         * codeのプロパティ名を返します。
         *
         * @return codeのプロパティ名
         */
        public PropertyName<String> code() {
            return new PropertyName<String>(this, "code");
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
         * namewarnのプロパティ名を返します。
         *
         * @return namewarnのプロパティ名
         */
        public PropertyName<String> namewarn() {
            return new PropertyName<String>(this, "namewarn");
        }

        /**
         * warnflagのプロパティ名を返します。
         *
         * @return warnflagのプロパティ名
         */
        public PropertyName<Boolean> warnflag() {
            return new PropertyName<Boolean>(this, "warnflag");
        }

        /**
         * tatsumakiflagのプロパティ名を返します。
         *
         * @return tatsumakiflagのプロパティ名
         */
        public PropertyName<Boolean> tatsumakiflag() {
            return new PropertyName<Boolean>(this, "tatsumakiflag");
        }

        /**
         * landslideflagのプロパティ名を返します。
         *
         * @return landslideflagのプロパティ名
         */
        public PropertyName<Boolean> landslideflag() {
            return new PropertyName<Boolean>(this, "landslideflag");
        }

        /**
         * riverflagのプロパティ名を返します。
         *
         * @return riverflagのプロパティ名
         */
        public PropertyName<Boolean> riverflag() {
            return new PropertyName<Boolean>(this, "riverflag");
        }

        /**
         * nameseismicのプロパティ名を返します。
         *
         * @return nameseismicのプロパティ名
         */
        public PropertyName<String> nameseismic() {
            return new PropertyName<String>(this, "nameseismic");
        }

        /**
         * namevolcanoのプロパティ名を返します。
         *
         * @return namevolcanoのプロパティ名
         */
        public PropertyName<String> namevolcano() {
            return new PropertyName<String>(this, "namevolcano");
        }

        /**
         * pointのプロパティ名を返します。
         *
         * @return pointのプロパティ名
         */
        public PropertyName<String> point() {
            return new PropertyName<String>(this, "point");
        }

        /**
         * lineのプロパティ名を返します。
         *
         * @return lineのプロパティ名
         */
        public PropertyName<String> line() {
            return new PropertyName<String>(this, "line");
        }

        /**
         * polygonのプロパティ名を返します。
         *
         * @return polygonのプロパティ名
         */
        public PropertyName<String> polygon() {
            return new PropertyName<String>(this, "polygon");
        }

        /**
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }
    }
}
