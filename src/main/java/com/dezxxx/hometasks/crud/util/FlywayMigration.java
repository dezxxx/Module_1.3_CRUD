package com.dezxxx.hometasks.crud.util;

import org.flywaydb.core.Flyway;

public final class FlywayMigration {

    private FlywayMigration() {}

    private static final String MYSQL_URL      = "jdbc:mysql://localhost:3306/hibernate_db_1";
    private static final String MYSQL_USER     = "root";
    private static final String MYSQL_PASSWORD = "dezxxx";

    private static final String POSTGRES_URL      = "jdbc:postgresql://localhost:5432/hibernate_db_1";
    private static final String POSTGRES_USER     = "postgres";
    private static final String POSTGRES_PASSWORD = "dezxxx";

    public static void run(DatabaseType type) {
        boolean isPostgres = type == DatabaseType.POSTGRES;

        Flyway.configure()
                .dataSource(
                        isPostgres ? POSTGRES_URL      : MYSQL_URL,
                        isPostgres ? POSTGRES_USER     : MYSQL_USER,
                        isPostgres ? POSTGRES_PASSWORD : MYSQL_PASSWORD
                )
                .locations(isPostgres ? "classpath:db/migration/postgres" : "classpath:db/migration")
                .load()
                .migrate();
    }
}