package com.library.library_management_api.exception;

public class DuplicateMemberException extends RuntimeException{

    public DuplicateMemberException(String message){
        super(message);
    }
}
