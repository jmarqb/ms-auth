package com.jmarqb.ms.auth.app.dtos.request;

import com.jmarqb.ms.auth.app.enums.Gender;
import com.jmarqb.ms.auth.app.validation.ExistEmail;
import com.jmarqb.ms.auth.app.validation.ExistPhone;
import com.jmarqb.ms.auth.app.validation.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateUserDto {

    @Schema(description = "firstName must be between 3 and 30 characters",example = "John")
    @NotBlank
    @Size(min = 3, max = 30, message = "firstName must be between 3 and 30 characters")
    private String firstName;

    @Schema(description = "lastName must be between 3 and 30 characters",example = "Doe")
    @NotBlank
    @Size(min = 3, max = 30, message = "lastName must be between 3 and 30 characters")
    private String lastName;

    @Schema(description = "Email must be a valid email",example = "oR7o0@example.com")
    @NotBlank
    @Email
    @ExistEmail
    private String email;

    @Schema(description = "Age must be at least 18",example = "20")
    @Min(value = 18, message = "Age must be at least 18")
    private int age;

    @Schema(description = "Phone must be a valid phone number",example = "+1234567890")
    @NotBlank
    @ExistPhone
    @Pattern(regexp = "^((\\+[1-9]{1,4}[ -]?)|(\\([0-9]{2,3}\\)[ -]?)|([0-9]{2,4})[ -]?)*?[0-9]{3,4}[ -]?[0-9]{3,4}$",
            message = "Phone must be a valid phone number")
    private String phone;

    @Schema(description = "Gender must be [MALE|FEMALE|NO_DIFFERENTIATION|NO_IDENTIFY_ANY]",example = "MALE")
    @NotBlank
    @ValueOfEnum(enumClass = Gender.class,message = "Gender must be [MALE|FEMALE|NO_DIFFERENTIATION|NO_IDENTIFY_ANY]")
    private String gender;

    @Schema(description = "country",example = "Colombia")
    @NotBlank
    private String country;

}
