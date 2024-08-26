package com.lib.libmansys.dto.User;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
}
