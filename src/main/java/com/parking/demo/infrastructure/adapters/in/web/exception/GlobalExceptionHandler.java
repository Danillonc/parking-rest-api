package com.parking.demo.infrastructure.adapters.in.web.exception;

import com.parking.demo.domain.exception.BusinessException;
import com.parking.demo.domain.exception.ParkingFullException;
import com.parking.demo.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Tratamento de Estacionamento Cheio (Regra de Negócio Específica)
    // 422 Unprocessable Entity: O payload está correto, mas a regra de negócio impede o processamento.
    @ExceptionHandler(ParkingFullException.class)
    public ProblemDetail handleParkingFullException(ParkingFullException ex) {
        log.warn("Tentativa de entrada com estacionamento cheio: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Parking Lot is Full");
        problemDetail.setType(URI.create("https://api.garage.com/errors/parking-full"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 2. Tratamento de Recurso Não Encontrado (ex: Ticket ou Vaga não existe)
    // 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 3. Tratamento de Erros de Negócio Genéricos
    // 400 Bad Request (ou 409 Conflict, dependendo da semântica)
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        log.warn("Erro de negócio: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 4. Tratamento de Validação de Payload (@Valid, @NotBlank, etc)
    // 400 Bad Request com detalhamento dos campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request payload");
        problemDetail.setTitle("Validation Failed");

        // Extrai os campos que falharam e suas respectivas mensagens
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        // Adiciona a lista de erros customizada no ProblemDetail
        problemDetail.setProperty("invalid_params", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 5. Tratamento de Argumentos Inválidos (Nossas Guard Clauses no Service)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 6. Catch-all: Erros não mapeados (NullPointer, Banco caiu, etc)
    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllUncaughtException(Exception ex) {
        // Aqui o log como ERROR é crucial para o monitoramento (Datadog, Kibana, etc)
        log.error("Erro sistêmico inesperado", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        // Em produção, nunca vaze a stack trace (ex.getMessage()) para o cliente num erro 500!
        return problemDetail;
    }

}
