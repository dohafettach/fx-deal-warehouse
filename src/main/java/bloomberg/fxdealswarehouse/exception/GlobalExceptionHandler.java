package bloomberg.fxdealswarehouse.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateDealException.class)
    public ResponseEntity<Map<String, Object>> duplicateDealException(DuplicateDealException e) {
        logger.warn("Duplicate deal attempt: {}", e.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Duplicate Deal");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        logger.error("Validation failed");
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("errors", fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidDealException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDeal(InvalidDealException e){
        logger.error("Invalid deal: {}", e.getMessage());
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.BAD_REQUEST.value());
        error.put("error","Invalid Deal");
        error.put("message",e.getMessage());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        logger.error("Unexpected error", e);
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error","Internal Server Error");
        error.put("message", "An unexpected error occurred");
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }
