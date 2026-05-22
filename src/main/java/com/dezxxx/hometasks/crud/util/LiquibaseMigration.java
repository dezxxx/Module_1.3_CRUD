package com.dezxxx.hometasks.crud.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

public class LiquibaseMigration {  // <-- добавь это

    public static void run(Connection connection) {

        try {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(
                            new JdbcConnection(connection)
                    );

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new liquibase.Contexts(), new liquibase.LabelExpression());

            System.out.println("Liquibase migrations completed");

        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed", e);
        }
    }

}