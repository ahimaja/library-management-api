package com.library.library_management_api.exception;

public class BookRecordNotFoundException extends RuntimeException{
    public BookRecordNotFoundException(String message){
        super(message);
    }
}
