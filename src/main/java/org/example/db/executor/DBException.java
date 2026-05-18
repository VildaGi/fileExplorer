package org.example.db.executor;

/**
 * Исключение для ошибок базы данных
 */
public class DBException extends Exception {
    public DBException(Throwable cause) {
        super(cause);
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}