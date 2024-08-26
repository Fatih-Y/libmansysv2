package com.lib.libmansys.controller;

import com.lib.libmansys.config.AuthenticationService;
import com.lib.libmansys.dto.*;
import com.lib.libmansys.dto.User.CreateUserRequest;
import com.lib.libmansys.dto.User.UpdateUserRequest;
import com.lib.libmansys.dto.User.UserDTO;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authService;

    @GetMapping("/getUserDetails/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserDetailsById(id);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/getUserLoans/{id}")
    public ResponseEntity<List<LoanDTO>> getUserLoans(@PathVariable Long id) {
        List<LoanDTO> loanDTOs = userService.getUserLoansById(id);
        if (loanDTOs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loanDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/makeAdmin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable Long id) {
        try {
            userService.makeAdmin(id);
            return ResponseEntity.ok("Kullanıcı başarıyla ADMIN olarak güncellendi.");
        } catch (Exception e) {
            System.err.println("Kullanıcıyı güncellerken bir hata meydana geldi: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Kullanıcı güncellenirken bir hata oluştu.");
        }
    }

}
