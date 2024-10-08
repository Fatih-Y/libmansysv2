package com.lib.libmansys.service;


import com.lib.libmansys.dto.*;
import com.lib.libmansys.dto.Book.BookDTO;
import com.lib.libmansys.dto.User.CreateUserRequest;
import com.lib.libmansys.dto.User.UpdateUserRequest;
import com.lib.libmansys.dto.User.UserDTO;
import com.lib.libmansys.entity.Enum.LoanPeriodStatus;
import com.lib.libmansys.entity.Enum.MembershipStatus;
import com.lib.libmansys.entity.Enum.UserRole;
import com.lib.libmansys.entity.User;
import com.lib.libmansys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null); // todo: need detailed check
    }
    public UserDTO getUserDetailsById(Long id) {
        User user = userRepository.findById(id).orElse(null); // checked

        if (user == null) {
            return null; //
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());

        return userDTO;
    }
    @Transactional
    public List<LoanDTO> getUserLoansById(Long id) {
        User user = userRepository.findById(id).orElse(null); // checked

        if (user == null) {
            return Collections.emptyList();
        }

        List<LoanDTO> loanDTOs = user.getLoans().stream().map(loan -> {   // learn more about the suggested fix
            LoanDTO loanDTO = new LoanDTO();
            loanDTO.setId(loan.getId());
            loanDTO.setLoanDate(loan.getLoanDate());
            loanDTO.setExpectedReturnDate(loan.getExpectedReturnDate());
            loanDTO.setStatus(loan.getStatus());

            BookDTO bookDTO = new BookDTO();
            bookDTO.setId(loan.getBook().getId());
            bookDTO.setTitle(loan.getBook().getTitle());

            bookDTO.setBase64image(loan.getBook().getBase64image());
            loanDTO.setBook(bookDTO);

            return loanDTO;
        }).collect(Collectors.toList());

        return loanDTOs;
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

    public boolean updateUser(Long id, UpdateUserRequest updateUserRequest) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setName(updateUserRequest.getName());
            existingUser.setEmail(updateUserRequest.getEmail());
            existingUser.setUsername(updateUserRequest.getUsername());
            existingUser.setPassword(updateUserRequest.getPassword());
            userRepository.save(existingUser);
            return true;
        }
        return false;
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
