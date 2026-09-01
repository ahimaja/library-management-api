package com.library.library_management_api.exception;

import com.library.library_management_api.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception,
                                                                     HttpServletRequest request){
        Map<String,String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error->fieldErrors.put(error.getField(),error.getDefaultMessage()));
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status,
                                                                String message,
                                                                HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                null);
        return ResponseEntity.status(status).body(response);

    }



    @ExceptionHandler({
            BookNotFoundException.class,
            MemberNotFoundException.class,
            BookRecordNotFoundException.class,
            BorrowRecordNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException exception,
                                                           HttpServletRequest request){
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
                                                        HttpServletRequest request){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }


    @ExceptionHandler({
            DuplicateBookException.class,
            DuplicateMemberException.class,
            BookRecordNotAvailableException.class,
            BorrowLimitExceededException.class,
            OutstandingFineException.class,
            BorrowRecordAlreadyReturnedException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException exception,
                                                           HttpServletRequest request){
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }


}
