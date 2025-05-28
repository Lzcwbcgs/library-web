package com.xiazihan.webback.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {
    private String token;
    private UserVO userInfo;
}