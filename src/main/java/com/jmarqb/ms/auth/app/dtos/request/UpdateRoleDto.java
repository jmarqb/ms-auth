package com.jmarqb.ms.auth.app.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateRoleDto {

        @Schema(description = "name must be between 3 and 30 characters",example = "ROLE_NAME")
        @Size(min = 3, max = 30, message = "name must be between 3 and 30 characters")
        private String name;

        @Schema(description = "description must be between 3 and 30 characters",example = "ROLE_DESCRIPTION")
        @Size(min = 3, max = 30, message = "description must be between 3 and 30 characters")
        private String description;

        @Schema(description = "icon",example = "ROLE_ICON")
        private String icon;

        @Schema(description = "isAdmin",example = "false")
        private Boolean isAdmin;

        @Schema(description = "isDefaultRole",example = "true")
        private Boolean isDefaultRole;

}
