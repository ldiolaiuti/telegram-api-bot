package com.ldiolaiuti.telegram.api.bot.exceptions;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }

}
