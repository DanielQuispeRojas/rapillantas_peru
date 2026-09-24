package pe.rapillantas.api.common;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrorHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
    var msg = e.getBindingResult().getFieldErrors().stream()
        .map(f -> f.getField() + ": " + f.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b).orElse("validacion");
    return ResponseEntity.badRequest().body(Map.of("error", msg));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, String>> state(IllegalStateException e) {
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> arg(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
  }
}
