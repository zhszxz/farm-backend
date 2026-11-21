package com.farm.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private Long userId;
    private String nickname;
    private String phone;
    private Integer role;
}
