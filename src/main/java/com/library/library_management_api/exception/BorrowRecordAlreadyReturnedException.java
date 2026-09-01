package com.library.library_management_api.exception;

public class BorrowRecordAlreadyReturnedException extends RuntimeException{

    public BorrowRecordAlreadyReturnedException(String message){
        super(message);
    }
}
