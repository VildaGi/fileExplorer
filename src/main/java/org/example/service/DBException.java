package org.example.service;

public class DBException extends Exception {
    public DBException(Throwable throwable) {
        super(throwable);
    }
}