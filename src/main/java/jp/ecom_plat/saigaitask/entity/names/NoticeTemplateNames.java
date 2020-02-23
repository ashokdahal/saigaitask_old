/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticetemplatetypeMasterNames._NoticetemplatetypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.NoticetypeMasterNames._NoticetypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticeTemplate}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class NoticeTemplateNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notification template";
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
     * noticetypeidのプロパティ名を返します。
     * 
     * @return noticetypeidのプロパティ名
     */
    public static PropertyName<Integer> noticetypeid() {
        return new PropertyName<Integer>("noticetypeid");
    }

    /**
     * noticetemplatetypeidのプロパティ名を返します。
     * 
     * @return noticetemplatetypeidのプロパティ名
     */
    public static PropertyName<Integer> noticetemplatetypeid() {
        return new PropertyName<Integer>("noticetemplatetypeid");
    }

    /**
     * templateclassのプロパティ名を返します。
     * 
     * @return templateclassのプロパティ名
     */
    public static PropertyName<String> templateclass() {
        return new PropertyName<String>("templateclass");
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
     * contentのプロパティ名を返します。
     * 
     * @return contentのプロパティ名
     */
    public static PropertyName<String> content() {
        return new PropertyName<String>("content");
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
     * noticetemplatetypeMasterのプロパティ名を返します。
     * 
     * @return noticetemplatetypeMasterのプロパティ名
     */
    public static _NoticetemplatetypeMasterNames noticetemplatetypeMaster() {
        return new _NoticetemplatetypeMasterNames("noticetemplatetypeMaster");
    }

    /**
     * noticetypeMasterのプロパティ名を返します。
     * 
     * @return noticetypeMasterのプロパティ名
     */
    public static _NoticetypeMasterNames noticetypeMaster() {
        return new _NoticetypeMasterNames("noticetypeMaster");
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
    public static class _NoticeTemplateNames extends PropertyName<NoticeTemplate> {

        /**
         * インスタンスを構築します。
         */
        public _NoticeTemplateNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticeTemplateNames(final String name) {
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
        public _NoticeTemplateNames(final PropertyName<?> parent, final String name) {
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
         * noticetypeidのプロパティ名を返します。
         *
         * @return noticetypeidのプロパティ名
         */
        public PropertyName<Integer> noticetypeid() {
            return new PropertyName<Integer>(this, "noticetypeid");
        }

        /**
         * noticetemplatetypeidのプロパティ名を返します。
         *
         * @return noticetemplatetypeidのプロパティ名
         */
        public PropertyName<Integer> noticetemplatetypeid() {
            return new PropertyName<Integer>(this, "noticetemplatetypeid");
        }

        /**
         * templateclassのプロパティ名を返します。
         *
         * @return templateclassのプロパティ名
         */
        public PropertyName<String> templateclass() {
            return new PropertyName<String>(this, "templateclass");
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
         * contentのプロパティ名を返します。
         *
         * @return contentのプロパティ名
         */
        public PropertyName<String> content() {
            return new PropertyName<String>(this, "content");
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
         * noticetemplatetypeMasterのプロパティ名を返します。
         *
         * @return noticetemplatetypeMasterのプロパティ名
         */
        public _NoticetemplatetypeMasterNames noticetemplatetypeMaster() {
            return new _NoticetemplatetypeMasterNames(this, "noticetemplatetypeMaster");
        }

        /**
         * noticetypeMasterのプロパティ名を返します。
         *
         * @return noticetypeMasterのプロパティ名
         */
        public _NoticetypeMasterNames noticetypeMaster() {
            return new _NoticetypeMasterNames(this, "noticetypeMaster");
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
