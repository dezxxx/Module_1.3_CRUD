package com.dezxxx.hometasks.crud;

import com.dezxxx.hometasks.crud.util.HibernateUtil;

public class App {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::shutdown));
        new ApplicationContext().start();
    }
}
