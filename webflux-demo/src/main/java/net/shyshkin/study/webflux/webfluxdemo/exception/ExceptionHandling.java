package net.shyshkin.study.webflux.webfluxdemo.exception;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.dto.InputFailedValidationResponse;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public InputFailedValidationResponse handleValidationException(WebExchangeBindException ex) {

        String message = ex.getFieldErrors().stream()
                .map(fe -> String.format("Field `%s` %s but was %s", fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .collect(Collectors.joining(";"));

        return InputFailedValidationResponse.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Input is not valid")
                .build();
    }

    @ExceptionHandler(VinsInputValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public VinsValidationResponse handleVinsValidationException(VinsInputValidationException ex) {
        return VinsValidationResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .input(ex.getInput())
                .build();
    }

}
