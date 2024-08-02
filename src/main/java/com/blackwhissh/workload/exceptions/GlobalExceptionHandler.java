package com.blackwhissh.workload.exceptions;

import com.blackwhissh.workload.exceptions.list.UserNotFoundException;
import com.blackwhissh.workload.exceptions.list.WorkDayNotFoundException;
import com.blackwhissh.workload.exceptions.list.WorkScheduleNotFoundException;
import com.blackwhissh.workload.exceptions.list.WrongWorkDayTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("User not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(WrongWorkDayTypeException.class)
    public ResponseEntity<Object> handleWrongWorkDayTypeException(WrongWorkDayTypeException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Wrong enum type provided for work day type");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(WorkScheduleNotFoundException.class)
    public ResponseEntity<Object> handleWorkScheduleNotFoundException(WorkScheduleNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Work schedule not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(WorkDayNotFoundException.class)
    public ResponseEntity<Object> handleWorkDayNotFoundException(WorkDayNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Work day not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
