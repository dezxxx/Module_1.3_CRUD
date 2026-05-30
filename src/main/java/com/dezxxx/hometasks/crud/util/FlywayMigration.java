package com.dezxxx.hometasks.crud.util;

import org.flywaydb.core.Flyway;

public final class FlywayMigration {

    private FlywayMigration() {}

    private static final String URL      = "jdbc:mysql://localhost:3306/hibernate_db_1";
    private static final String USER     = "root";
    private static final String PASSWORD = "dezxxx";

    public static void run() {
        Flyway.configure()
                .dataSource(URL, USER, PASSWORD)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }
}