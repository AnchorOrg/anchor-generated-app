package org.anchor.config

import org.slf4j.LoggerFactory
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import java.util.Collections

class MysqlTestContainer : SqlTestContainer {

    private val log = LoggerFactory.getLogger(javaClass)
    private val memoryInBytes = (100 * 1024 * 1024).toLong()
    private val memorySwapInBytes = (200 * 1024 * 1024).toLong()

    private var mysqlContainer: MySQLContainer<*>? = null

    override fun destroy() {
        if (null != mysqlContainer && mysqlContainer?.isRunning == true) {
            mysqlContainer?.stop()
        }
    }

    override fun afterPropertiesSet() {
        if (null == mysqlContainer) {
            mysqlContainer = MySQLContainer("mysql:8.0.30-debian")
                .withDatabaseName("anchorApp")
                .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
                .withLogConsumer(Slf4jLogConsumer(log))
                .withReuse(true)
                .withPrivilegedMode(true)
                .withConfigurationOverride("testcontainers/mysql")
                .withCreateContainerCmdModifier { cmd ->
                    cmd.hostConfig!!
                        .withMemory(memoryInBytes)
                        .withMemorySwap(memorySwapInBytes)
                }
        }
        if (mysqlContainer?.isRunning != true) {
            mysqlContainer?.start()
        }
    }

    override fun getTestContainer() = mysqlContainer as JdbcDatabaseContainer<*>
}
