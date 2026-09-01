package com.library.library_management_api.exception;

public class DuplicateBookException extends RuntimeException{
    public DuplicateBookException(String message){
        super(message);
    }
}
