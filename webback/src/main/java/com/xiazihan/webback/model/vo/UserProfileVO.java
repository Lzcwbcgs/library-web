package com.xiazihan.webback.model.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "用户个人信息视图对象")
public class UserProfileVO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户ID")
    private Long userId;

    // 你可以根据需要添加更多字段，例如：
    // private String nickname;
    // private String avatar;
    // private String email;
    // private String role;
}