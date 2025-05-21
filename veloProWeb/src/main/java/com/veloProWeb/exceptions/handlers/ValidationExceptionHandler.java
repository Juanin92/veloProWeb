package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.validation.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler extends BaseExceptionHandler {

    /**
     * Maneja validaciones personalizadas lanzadas manualmente.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(ValidationException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de validación de @Valid (por ejemplo, en @RequestBody).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage())
                                .orElse("Mensaje no disponible"),
                        (existing, replacement) -> existing
                ));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de validación en parámetros simples con @Validated (como @RequestParam, @PathVariable).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> errors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> extractFieldName(violation.getPropertyPath().toString()),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Extrae el nombre del campo desde el propertyPath completo de la excepción.
     */
    private String extractFieldName(String propertyPath) {
        // Para casos como: "getDtoByRut.rut" → devuelve "rut"
        if (propertyPath == null || !propertyPath.contains(".")) return propertyPath;
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }
}
