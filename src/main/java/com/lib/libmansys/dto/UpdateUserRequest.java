package com.lib.libmansys.dto;

import com.lib.libmansys.entity.Enum.UserRole;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String username;
    private String password;
    private UserRole role;
}
