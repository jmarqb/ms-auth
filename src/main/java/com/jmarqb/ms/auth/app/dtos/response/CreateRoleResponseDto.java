package com.jmarqb.ms.auth.app.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleResponseDto {

    @Schema(description = "id",example = "1")
    private Long id;

    @Schema(description = "name",example = "ROLE_NAME")
    private String name;

    @Schema(description = "description",example = "ROLE_DESCRIPTION")
    private String description;

    @Schema(description = "icon",example = "ROLE_ICON")
    private String icon;

    @Schema(description = "isAdmin",example = "false")
    private Boolean isAdmin;

    @Schema(description = "isDefaultRole",example = "true")
    private Boolean isDefaultRole;

    @Schema(description = "deleted",example = "false")
    private boolean deleted;

    @Schema(description = "deletedAt",example = "null")
    private Date deletedAt;
}
