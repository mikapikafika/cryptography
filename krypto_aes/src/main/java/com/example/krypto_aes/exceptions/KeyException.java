package com.example.krypto_aes.exceptions;

public class KeyException extends RuntimeException{
    public KeyException(String message) {
        super(message);
    }
    public KeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
