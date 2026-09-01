package com.library.library_management_api.exception;

public class MemberNotFoundException extends RuntimeException{

    public MemberNotFoundException(String message){
        super(message);
    }
}
