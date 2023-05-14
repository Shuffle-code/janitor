package isands.example.janitor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import isands.example.janitor.security.validation.FieldMatch;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldMatch(firstFieldName = "password", secondFieldName = "matchingPassword", message = "The passwords must match")

public class UserDto {
    @JsonIgnore
    private Long id;

    @NotBlank
    @Size(min = 4, max = 8)
    private String username;
    @NotBlank
    @Size(min = 4, max = 8)
    private String password;
    @NotNull
    @Size(min = 4, max = 8)
    private String matchingPassword;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 5, max = 20)
    private String phone;
}
