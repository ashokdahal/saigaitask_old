package jp.ecom_plat.saigaitask.config;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.dialect.PostgreDialect;
import org.seasar.extension.jdbc.manager.JdbcManagerImpl;
import org.seasar.extension.jdbc.meta.ColumnMetaFactoryImpl;
import org.seasar.extension.jdbc.meta.EntityMetaFactoryImpl;
import org.seasar.extension.jdbc.meta.PropertyMetaFactoryImpl;
import org.seasar.extension.jdbc.meta.TableMetaFactoryImpl;
import org.seasar.framework.convention.impl.PersistenceConventionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bitronix.tm.BitronixTransactionSynchronizationRegistry;

@Configuration
public class S2JdbcConfig {

	static Logger logger = LoggerFactory.getLogger(S2JdbcConfig.class);

	static {
		// Tomcat8にWARをデプロイすると No suitable driver found for エラーが発生するため、
		// DriverManager に手動で登録する
		try {
			DriverManager.registerDriver(new org.postgresql.Driver());
		} catch (SQLException e) {
			logger.error("failed to register jdbc driver", e);
		}
	}

    @Bean
    TransactionSynchronizationRegistry synchronizationRegistry(TransactionManager transactionManager) {
        return new BitronixTransactionSynchronizationRegistry();
    }

    @Bean
    JdbcManager jdbcManager(DataSource dataSource, TransactionSynchronizationRegistry syncRegistry) {
        PersistenceConventionImpl persistenceConvention = new PersistenceConventionImpl();
        ColumnMetaFactoryImpl columnMetaFactory = new ColumnMetaFactoryImpl();
        columnMetaFactory.setPersistenceConvention(persistenceConvention);
        PropertyMetaFactoryImpl propertyMetaFactory = new PropertyMetaFactoryImpl();
        propertyMetaFactory.setColumnMetaFactory(columnMetaFactory);
        propertyMetaFactory.setPersistenceConvention(persistenceConvention);
        TableMetaFactoryImpl tableMetaFactory = new TableMetaFactoryImpl();
        tableMetaFactory.setPersistenceConvention(persistenceConvention);
        EntityMetaFactoryImpl entityMetaFactory = new EntityMetaFactoryImpl();
        entityMetaFactory.setPersistenceConvention(persistenceConvention);
        entityMetaFactory.setPropertyMetaFactory(propertyMetaFactory);
        entityMetaFactory.setTableMetaFactory(tableMetaFactory);
        
        PostgreDialect dialect = new PostgreDialect();
        JdbcManagerImpl jdbcManager = new JdbcManagerImpl();
        // OK: org.seasar.extension.dbcp.impl.XADataSourceImpl
        // NG: org.postgresql.xa.PGXADataSource
        jdbcManager.setDataSource(dataSource);
        jdbcManager.setDialect(dialect);
        jdbcManager.setEntityMetaFactory(entityMetaFactory);
        jdbcManager.setPersistenceConvention(persistenceConvention);
        jdbcManager.setSyncRegistry(syncRegistry);
        jdbcManager.setMaxRows(0);
        jdbcManager.setFetchSize(0);
        jdbcManager.setQueryTimeout(0);

        return jdbcManager;
    }
}
