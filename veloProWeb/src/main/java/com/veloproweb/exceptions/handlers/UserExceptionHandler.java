package com.veloproweb.exceptions.handlers;

import com.veloproweb.exceptions.BaseExceptionHandler;
import com.veloproweb.exceptions.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class UserExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            UserAlreadyActivatedException.class,
            UserAlreadyDeletedException.class,
            UserAlreadyExistsException.class,
            UserMasterRoleSelectedException.class,
            UsernameAlreadyExistsException.class,
            EmailAlreadyRegisterException.class,
            PasswordMismatchException.class,
            InvalidCredentialsException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<Map<String, String>> handleUserException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UserNotFoundException.class, UserRoleNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleUserNotFound(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
