package org.anchor.config

import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.test.context.ContextConfigurationAttributes
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.ContextCustomizerFactory
import tech.jhipster.config.JHipsterConstants
import java.util.*

class TestContainersSpringContextCustomizerFactory : ContextCustomizerFactory {

    private val log = LoggerFactory.getLogger(TestContainersSpringContextCustomizerFactory::class.java)

    companion object {
        private var redisBean: RedisTestContainer? = null
        private var prodTestContainer: SqlTestContainer? = null
    }

    override fun createContextCustomizer(
        testClass: Class<*>,
        configAttributes: MutableList<ContextConfigurationAttributes>
    ): ContextCustomizer {
        return ContextCustomizer { context, _ ->
            val beanFactory = context.beanFactory
            var testValues = TestPropertyValues.empty()
            val redisAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedRedis::class.java)
            if (null != redisAnnotation) {
                log.debug("detected the EmbeddedRedis annotation on class {}", testClass.name)
                log.info("Warming up the redis database")
                if (null == redisBean) {
                    redisBean = beanFactory.createBean(RedisTestContainer::class.java)
                    beanFactory.registerSingleton(RedisTestContainer::class.java.name, redisBean)
                    // (beanFactory as (DefaultListableBeanFactory)).registerDisposableBean(RedisTestContainer::class.java.name, redisBean)
                }
                redisBean?.let {
                    testValues = testValues.and(
                        "jhipster.cache.redis.server=redis://" + it.getRedisContainer()?.containerIpAddress + ":" + it.getRedisContainer()
                            ?.getMappedPort(6379)
                    )
                }
            }
            val sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedSQL::class.java)
            if (null != sqlAnnotation) {
                log.debug("detected the EmbeddedSQL annotation on class {}", testClass.name)
                log.info("Warming up the sql database")
                if (context.environment.activeProfiles.asList()
                        .contains("test${JHipsterConstants.SPRING_PROFILE_PRODUCTION}")
                ) {
                    if (null == prodTestContainer) {
                        try {
                            val containerClass =
                                Class.forName("${javaClass.packageName}.PostgreSqlTestContainer") as Class<out SqlTestContainer>
                            prodTestContainer = beanFactory.createBean(containerClass)
                            beanFactory.registerSingleton(containerClass.name, prodTestContainer)
                            // (beanFactory as (DefaultListableBeanFactory)).registerDisposableBean(containerClass.name, prodTestContainer)
                        } catch (e: ClassNotFoundException) {
                            throw RuntimeException(e)
                        }
                    }
                    prodTestContainer?.let {
                        testValues =
                            testValues.and("spring.datasource.url=" + it.getTestContainer().jdbcUrl + "?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true")
                        testValues = testValues.and("spring.datasource.username=" + it.getTestContainer().username)
                        testValues = testValues.and("spring.datasource.password=" + it.getTestContainer().password)
                    }
                }
            }

            testValues.applyTo(context)
        }
    }
}
