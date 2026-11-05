package com.agenda.personal.excepcion;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> detalles = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                detalles.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "Revisa los campos enviados.",
                detalles,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarFormatoIncorrecto(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Formato incorrecto",
                "Uno o más datos tienen un formato inválido. "
                        + "Revisa las fechas, los valores y los tipos de datos.",
                null,
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentosInvalidos(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                ex.getMessage(),
                null,
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarConflictos(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                "Conflicto de datos",
                "No se pudo guardar la información porque "
                        + "entra en conflicto con un registro existente.",
                null,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInterno(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Error interno en {}", request.getRequestURI(), ex);

        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno",
                "Ocurrió un error inesperado. Inténtalo nuevamente.",
                null,
                request
        );
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus estado,
            String error,
            String mensaje,
            Map<String, String> detalles,
            HttpServletRequest request) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("timestamp", Instant.now());
        respuesta.put("status", estado.value());
        respuesta.put("error", error);
        respuesta.put("mensaje", mensaje);

        if (detalles != null && !detalles.isEmpty()) {
            respuesta.put("detalles", detalles);
        }

        respuesta.put("ruta", request.getRequestURI());

        return ResponseEntity.status(estado).body(respuesta);
    }
}