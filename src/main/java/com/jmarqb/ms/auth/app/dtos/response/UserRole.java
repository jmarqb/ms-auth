package com.jmarqb.ms.auth.app.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class UserRole {

    private String name;

    private String description;

    private String icon;

    private Boolean isAdmin;

    private Boolean isDefaultRole;
}
