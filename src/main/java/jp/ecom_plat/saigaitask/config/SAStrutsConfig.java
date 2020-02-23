package jp.ecom_plat.saigaitask.config;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * SAStruts関係のBean設定
 * 
 * <pre>
 * @Component, @Service, @Repositoryなどのアノテーションが付いていない service, dto, form を自動でBean登録する。
 * </pre>
 * 
 * 本来は @Component などを付与すべきなので、
 * 移行作業完了次第、このConfigは無効化すべき。
 * 
 * @author oku
 *
 */
@Configuration
public class SAStrutsConfig {

	Logger logger = LoggerFactory.getLogger(getClass());

    /* *
     * F I X M E: Spring dummy s2container
     * @return
     * /
    @Bean
    S2Container s2Container() {
    	return new S2ContainerImpl();
    }
    */

    /* *
     * F I X M E: Spring dummy sessionScope
     * 最終的にはsessionScopeを使わないようにする
     * @return
     * /
    @Bean
    Map<String, Object> sessionScope() {
    	return new HashMap<>();
    }
    */

    /* *
     * F I X M E: Spring dummy requestScope
     * 最終的にはrequestScopeを使わないようにする
     * @return
     * /
    @Bean
    Map<String, Object> requestScope() {
    	return new HashMap<>();
    }
    */

    /* *
     * service/dto/formパッケージにあるクラスを Bean として登録する
     * F I X M E: この自動登録は移行完了後に廃止する
     * @return
     * /
    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return bf -> {
            BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) bf;

            // ルートパッケージ名を取得
            String rootPackageName = S2JdbcConfig.class.getPackage().getName();
            rootPackageName = rootPackageName.replace(".config", "");
            
            try {
				List<Class> classes = findMyTypes(rootPackageName);
				
				for(Class clazz : classes) {
					String beanName = StringUtil.decapitalize(clazz.getSimpleName());
					if("deleteCascadeResult".equals(beanName)) continue;
					if(StringUtil.isNotEmpty(beanName)) {
			            logger.debug("Regist Bean: "+beanName+" ("+clazz.toString()+")");
			            beanFactory.registerBeanDefinition(beanName,
			                    BeanDefinitionBuilder.genericBeanDefinition(clazz)
			                            .getBeanDefinition()
					            );
					}
					else {
			            logger.debug("Skip Bean: "+beanName+" ("+clazz.toString()+")");
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
        };
    }
    */


    /**
     * @see http://stackoverflow.com/questions/1456930/how-do-i-read-all-classes-from-a-java-package-in-the-classpath
     */
    private List<Class> findMyTypes(String basePackage) throws IOException, ClassNotFoundException
    {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class> candidates = new ArrayList<Class>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                                   resolveBasePackage(basePackage) + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (isCandidate(basePackage, metadataReader)) {
                    candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }
        return candidates;
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    private boolean isCandidate(String basePackage, MetadataReader metadataReader) throws ClassNotFoundException
    {
        try {
        	
            Class<?> c = Class.forName(metadataReader.getClassMetadata().getClassName());
            logger.trace("Class Found: "+c.toString());
            if(c.getAnnotation(Repository.class)!=null) return false;
            if(c.getAnnotation(Service.class)!=null) return false;
            if(c.getAnnotation(Component.class)!=null) return false;
            if(Modifier.isAbstract(c.getModifiers())) return false;
            // サブクラスありの場合は $1 が最後につく
            if(c.getName().endsWith("$1")==false
            		// $ が途中につく場合はサブクラスなので対象外
            		&& c.getName().indexOf('$')!=c.getName().indexOf("$1")) return false;
            //if(c.getAnnotation(.class)!=null) return false;
            for(String accept : new String[]{"service", "dto", "form"}) {
                if((c.getPackage().getName()+".").startsWith(basePackage+"."+accept+".")) {
                	// Serviceパッケージの場合は、XxxService のみを自動DI対象とする
                	if(accept.equals("service") && c.getSimpleName().endsWith("Service")==false ) return false;
                	return true;
                }
            }
        }
        catch(Throwable e){
        }
        return false;
    }
}
