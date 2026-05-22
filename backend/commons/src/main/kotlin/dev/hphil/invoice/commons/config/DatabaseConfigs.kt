package dev.hphil.invoice.commons.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.config.ApplicationConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import javax.sql.DataSource

fun Application.configureDatabase() {
    log.info("Configuring database")
    val dataSource = dataSource(environment.config)
    Flyway.configure()
        .dataSource(dataSource)
        .outOfOrder(true)
        .validateOnMigrate(true)
        .baselineOnMigrate(true)
        .load()
        .migrate()
    Database.connect(dataSource)
}

private fun dataSource(config: ApplicationConfig): DataSource {
    val prefix = "database"
    val serviceName = config.property("name").getString()
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = config.property("$prefix.jdbcUrl").getString()
        username = config.property("$prefix.username").getString()
        password = config.property("$prefix.password").getString()
        driverClassName = config.property("$prefix.driverClassName").getString()

        maximumPoolSize = config.property("$prefix.maximumPoolSize").getString().toInt()
        minimumIdle = config.property("$prefix.minimumIdle").getString().toInt()
        isAutoCommit = config.property("$prefix.isAutoCommit").getString().toBoolean()

        transactionIsolation = config.property("$prefix.transactionIsolation").getString()

        schema = serviceName
        connectionInitSql = "SET search_path TO $serviceName"

        validate()
    }
    return HikariDataSource(hikariConfig)
}
