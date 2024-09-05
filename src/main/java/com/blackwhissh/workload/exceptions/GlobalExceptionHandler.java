package com.blackwhissh.workload.exceptions;

import com.blackwhissh.workload.exceptions.list.*;
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
    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<Object> handleWorkScheduleNotFoundException(ScheduleNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Work schedule not found");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(WorkDayNotFoundException.class)
    public ResponseEntity<Object> handleWorkDayNotFoundException(WorkDayNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Work day not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(WrongMonthException.class)
    public ResponseEntity<Object> handleWrongMonthException(WrongMonthException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Wrong month provided");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EmployeeAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmployeeAlreadyExistsException(EmployeeAlreadyExistsException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Employee with this work ID already exists");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Employee with this work ID not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HourRemoveException.class)
    public ResponseEntity<Object> handleHourRemoveException(HourRemoveException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("First or last hour of a day can not be removed!");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HourAdditionValidationException.class)
    public ResponseEntity<Object> handleHourAdditionValidationException(HourAdditionValidationException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Validation of adding hour is unsuccessful");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeeklyHoursLimitExceedsException.class)
    public ResponseEntity<Object> handleWeeklyHoursLimitExceedsException(WeeklyHoursLimitExceedsException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("New hour exceeds weekly hours limit");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MonthlyHoursLimitExceedsException.class)
    public ResponseEntity<Object> handleMonthlyHoursLimitExceedsException(MonthlyHoursLimitExceedsException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("New hour exceeds monthly hours limit");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ScheduleGapException.class)
    public ResponseEntity<Object> handleScheduleGapException(ScheduleGapException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Not enough gap between schedules");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HourIsOccupiedException.class)
    public ResponseEntity<Object> handleHourIsOccupiedException(HourIsOccupiedException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Hour is occupied!");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DailyHoursLimitExceedsException.class)
    public ResponseEntity<Object> handleDailyHoursLimitExceedsException(DailyHoursLimitExceedsException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("New hour exceeds daily hours limit");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FirstOrLastHourSwapException.class)
    public ResponseEntity<Object> handleFirstOrLastHourSwapException(FirstOrLastHourSwapException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("First or last hour of a day can not be swapped");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HourSwapException.class)
    public ResponseEntity<Object> handleHourSwapException(HourSwapException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Error during swapping hour");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SwapRequestsExistsException.class)
    public ResponseEntity<Object> handleSwapRequestsExistsException(SwapRequestsExistsException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Swap request already exists for this hour");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
