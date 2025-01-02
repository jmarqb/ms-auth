package com.jmarqb.ms.auth.app.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeleteResponseDto {
    @Schema(description = "Acknowledged", example = "true")
    private boolean acknowledged;

    @Schema(description = "Deleted count", example = "1")
    private int deletedCount;
}
