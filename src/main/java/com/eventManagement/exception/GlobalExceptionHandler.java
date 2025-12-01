package com.eventManagement.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    /*
     * @ExceptionHandler(RuntimeException.class)
     * public ResponseEntity<Map<String, Object>> handleRunTimeException(Exception
     * ex) {
     * Map<String, Object> errorResponse = new HashMap<>();
     * errorResponse.put("timestamp", LocalDateTime.now());
     * errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
     * errorResponse.put("error", "Server Error");
     * errorResponse.put("message", ex.getMessage());
     * return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
     * }
     */

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("error-page");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView("error-page");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Server Error");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
}
