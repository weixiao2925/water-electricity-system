package org.example.cvitme01.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVO {
    @Email
    String email;
    @NotNull
    String username;
    @Length(max = 6,min = 6)
    String code;
    @Length(min = 6)
    String password;
}
