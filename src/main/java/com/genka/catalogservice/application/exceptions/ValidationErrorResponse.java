package com.genka.catalogservice.application.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ValidationErrorResponse {
    @Builder.Default
    private Date timestamp = new Date();
    @Builder.Default
    private Integer code = HttpStatus.BAD_REQUEST.value();
    @Builder.Default
    private String status = HttpStatus.BAD_REQUEST.name();
    @Builder.Default
    private String message = "invalid input";
    private List<ValidationErrorDetails> errors;
}
