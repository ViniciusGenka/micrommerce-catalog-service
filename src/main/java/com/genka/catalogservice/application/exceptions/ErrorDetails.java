package com.genka.catalogservice.application.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ErrorDetails {
    @Builder.Default
    private Date timestamp = new Date();
    private Integer code;
    private String status;
    private String message;
}
