package org.anchor

import org.anchor.config.AsyncSyncConfiguration
import org.anchor.config.EmbeddedRedis
import org.anchor.config.EmbeddedSQL
import org.anchor.config.TestSecurityConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

/**
 * Base composite annotation for integration tests.
 */
@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [AnchorApp::class, AsyncSyncConfiguration::class, TestSecurityConfiguration::class])
@EmbeddedRedis
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
annotation class IntegrationTest
