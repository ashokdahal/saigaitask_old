/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.seasar.jdbc.query;

import java.sql.PreparedStatement;

import org.seasar.extension.jdbc.AutoInsert;
import org.seasar.extension.jdbc.PropertyMeta;
import org.seasar.extension.jdbc.manager.JdbcManagerImplementor;
import org.seasar.extension.jdbc.query.AutoInsertImpl;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.NumberConversionUtil;

/**
 * エンティティにセットしたIDそのままでインサートする{@link AutoInsert}の実装クラスです。
 * 
 * @param <T>
 *            エンティティの型です。
 */
public class AutoInsertKeepIdImpl<T> extends AutoInsertImpl<T> {

	protected boolean keepId = true;

	/**
	 * @param jdbcManager
	 *            内部的なJDBCマネージャ
	 * @param entity
	 *            エンティティ
	 */
	public AutoInsertKeepIdImpl(final JdbcManagerImplementor jdbcManager, final T entity) {
		super(jdbcManager, entity);
	}

	@Override
	protected void prepareTargetProperties() {
		// デフォルトの動作
		super.prepareTargetProperties();
		// ID そのままの場合は、IDをインサート列対象として追加しておく
		if(keepId) {
			useGetGeneratedKeys = false;
			for (final PropertyMeta propertyMeta : entityMeta.getIdPropertyMetaList()) {
				// 未追加なら追加する
				if(!targetProperties.contains(propertyMeta)) {
					targetProperties.add(propertyMeta);
				}
			}
		}
	}

	@Override
	protected Object getIdValue(final PropertyMeta propertyMeta) {
		// デフォルトの動作
		if(!keepId) {
			return super.getIdValue(propertyMeta);
		}

		// ID そのままの場合は、オブジェクトにセットされている値をそのまま使用する
		Object value = FieldUtil.get(propertyMeta.getField(), entity);
		if (propertyMeta.isVersion()) {
			if (value == null
					|| Number.class.cast(value).longValue() <= 0L) {
				value = INITIAL_VERSION;
				final Class<?> fieldClass = ClassUtil
						.getWrapperClassIfPrimitive(propertyMeta
								.getPropertyClass());
				FieldUtil.set(propertyMeta.getField(), entity,
						NumberConversionUtil.convertNumber(fieldClass, value));
			}
		}
		return value;
	}

	@Override
	protected void postExecute(final PreparedStatement ps) {
		// デフォルトの動作
		if(!keepId) super.postExecute(ps);
		// ID そのままの場合は、なにもしない
	}
}
