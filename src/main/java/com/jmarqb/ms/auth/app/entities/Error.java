package com.jmarqb.ms.auth.app.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class Error {
    @Schema(description = "timestamp", example = "2021-01-01T00:00:00.000Z")
    private Date timestamp;

    @Schema(description = "status", example = "400")
    private int status;

    @Schema(description = "error", example = "Bad request")
    private String error;

    @Schema(description = "message", example = "Bad request")
    private String message;

    @Schema(description = "details", example = "[]")
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    public static class FieldError {

        @Schema(description = "field", example = "email")
        private String field;

        @Schema(description = "rejectedValue", example = "oR7o0@example.com")
        private String rejectedValue;

        @Schema(description = "message", example = "Email must be a valid email")
        private String message;
    }
}
