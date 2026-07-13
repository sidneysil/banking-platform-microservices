package com.sidney.banking.api;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<Problem> notFound(Exception e){return response(HttpStatus.NOT_FOUND,e);}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class,MethodArgumentNotValidException.class}) ResponseEntity<Problem> badRequest(Exception e){return response(HttpStatus.BAD_REQUEST,e);}
 private ResponseEntity<Problem> response(HttpStatus s,Exception e){return ResponseEntity.status(s).body(new Problem(Instant.now(),s.value(),s.getReasonPhrase(),e.getMessage()));} record Problem(Instant timestamp,int status,String error,String message){}
}
