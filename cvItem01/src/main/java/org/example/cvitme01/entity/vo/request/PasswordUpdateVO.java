package org.example.cvitme01.entity.vo.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Valid
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateVO {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmPassword;

    public boolean isValid() {
        return  oldPassword != null
                && newPassword != null
                && newPassword.equals(confirmPassword);
    }
}
