package com.example.krypto_aes.exceptions;

public class MessageException extends Exception{
    public MessageException(String message) {
        super(message);
    }
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
