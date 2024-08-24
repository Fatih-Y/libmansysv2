package com.lib.libmansys.dto;

import com.lib.libmansys.entity.Enum.UserRole;
import lombok.Data;

@Data
public class EditUser {
    private String name;
    private String email;
    private String username;
    private String password;

}
