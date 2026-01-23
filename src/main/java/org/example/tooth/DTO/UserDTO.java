package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户账号密码")
public class UserDTO {
    @NotBlank(message = "name不能为空")
    @Schema(description = "用户名", example = "alice", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @NotBlank(message = "password不能为空")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
