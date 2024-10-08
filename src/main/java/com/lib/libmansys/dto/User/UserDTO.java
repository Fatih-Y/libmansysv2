package com.lib.libmansys.dto.User;

import com.lib.libmansys.entity.Enum.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String username;
    private UserRole role;

}

