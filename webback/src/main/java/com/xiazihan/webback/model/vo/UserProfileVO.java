package com.xiazihan.webback.model.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "用户个人信息视图对象")
public class UserProfileVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色")
    private String role;
}