package com.lib.libmansys.dto;


import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String username;
    private String password;

}
