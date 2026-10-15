package com.library.library_management_api.exception;

public class UserAccountNotFoundException extends RuntimeException{

    public UserAccountNotFoundException(String message){
        super(message);
    }
}
