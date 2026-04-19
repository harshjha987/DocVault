package com.harsh.project.Exception;

import com.harsh.project.Dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handles when file or user is not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponse(404, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }


    // handles duplicate email on registration
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(409, ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    // handles file save/delete failures
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorage(
            FileStorageException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(500, ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // handles when uploaded file exceeds size limit
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(
            MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(413, "File too large"),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    // catches anything else that slips through
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ex.printStackTrace();
        System.out.println("EXCEPTION CLASS: " + ex.getClass().getName());
        System.out.println("EXCEPTION MESSAGE: " + ex.getMessage());
        if (ex.getCause() != null) {
            System.out.println("CAUSED BY: " + ex.getCause().getMessage());
        }
        return new ResponseEntity<>(
                new ErrorResponse(500, "Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(
            UnauthorizedAccessException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(403, ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }
}