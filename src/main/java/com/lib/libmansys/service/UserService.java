package com.lib.libmansys.service;


import com.lib.libmansys.dto.CreateUserRequest;
import com.lib.libmansys.dto.EditUser;
import com.lib.libmansys.dto.UpdateUserRequest;
import com.lib.libmansys.entity.Enum.LoanPeriodStatus;
import com.lib.libmansys.entity.Enum.MembershipStatus;
import com.lib.libmansys.entity.Enum.UserRole;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());
        user.setMembershipStatus(MembershipStatus.ACTIVE);
        user.setRole(UserRole.MEMBER);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setName(updateUserRequest.getName());
            existingUser.setEmail(updateUserRequest.getEmail());
            existingUser.setUsername(updateUserRequest.getUsername());
            existingUser.setPassword(updateUserRequest.getPassword());
            return userRepository.save(existingUser);
        }
        return null;
    }
    public void makeAdmin(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(UserRole.ADMIN);
            userRepository.save(user);
        } else {
            System.err.println(id +"ID'ye sahip kullanıcı bulunamadı: ");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deactivateUser(Long id) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User user = existingUserOptional.get();
            user.setMembershipStatus(MembershipStatus.SUSPENDED);
            userRepository.save(user);
        }
    }
    public void updateLoanPeriodStatus(User user, LoanPeriodStatus newStatus) {
        user.setLoanPeriodStatus(newStatus);
        userRepository.save(user);
    }
    public void applyPenalties(User user) {
        if (user.getLoanPeriodStatus() == LoanPeriodStatus.NORMAL) {
            user.setLoanPeriodStatus(LoanPeriodStatus.HALF);
        } else {
            user.setMembershipStatus(MembershipStatus.SUSPENDED);
        }
        userRepository.save(user);
    }
    public void notifyUserOfLostBook(User user) {
        String to = user.getEmail();
        String subject = "Kütüphane Bildirimi: Kayıp Kitap";
        String content = "<p>Merhaba " + user.getName() + ",</p>" +
                "<p>Ödünç aldığınız bir kitap kayıp olarak işaretlenmiştir. Detaylı bilgi için kütüphaneye başvurunuz.</p>";
        
        emailService.sendEmail(to, subject, content);

    }
}
