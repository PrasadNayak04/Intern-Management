package com.robosoft.internmanagement.exception;

import com.robosoft.internmanagement.constants.AppConstants;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.UnexpectedTypeException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<?> handleUnsupportedTypeException(JwtTokenException tokenException) {
        System.out.println("inside handler");
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseData<>("TOKEN NOT VALID", AppConstants.INVALID_INFORMATION));
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<?> handleDatabaseException(DatabaseException databaseException) {
        Result result = databaseException.getResult();
        Result results = new Result(result.getValue(), result.getDescription(), result.getOpinion());
        ResponseData responseData = new ResponseData("RECORD MISMATCH", results);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
    }

    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<?> handleFileEmptyException(FileEmptyException fileEmptyException) {
        Result result = fileEmptyException.getResult();
        Result results = new Result(result.getValue(), result.getDescription(), result.getOpinion());
        ResponseData responseData = new ResponseData("File not found", results);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
    }

}
