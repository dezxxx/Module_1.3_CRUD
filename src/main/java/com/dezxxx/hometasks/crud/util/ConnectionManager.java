package com.dezxxx.hometasks.crud.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL = "jdbc:mysql://localhost:3306/my_rdb";
    private static final String USER = "root";
    private static final String PASSWORD = "dezxxx";

    private static ConnectionManager instance;

    private final Connection connection;

    private ConnectionManager() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        this.connection.setAutoCommit(true);
    }

    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            try {
                instance = new ConnectionManager();
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Failed to create database connection", e
                );
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
