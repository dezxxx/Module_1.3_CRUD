package com.dezxxx.hometasks.crud.util;

public class UserCancelledException extends RuntimeException {

    public UserCancelledException() {
        super("Operation cancelled by user");
    }
}
