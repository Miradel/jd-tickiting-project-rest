package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.MailDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ConfirmationToken;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.service.ConfirmationTokenService;
import com.cybertek.service.RoleService;
import com.cybertek.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/User")
@Tag(name = "User Controller", description = "User API")
public class UserController {

    @Value("${app.local-url}")
    private String BASE_URL;

    private UserService userService;

    public UserController(UserService userService, MapperUtil mapperUtil, RoleService roleService, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.roleService = roleService;
        this.confirmationTokenService = confirmationTokenService;
    }

    private MapperUtil mapperUtil;
    private RoleService roleService;
    private ConfirmationTokenService confirmationTokenService;

    @PostMapping("/create-user")
    @Operation(summary = "Create new account")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {

        UserDTO createdUser = userService.save(userDTO);

        sendEmail(createEmail(createdUser));

        return ResponseEntity.ok(new ResponseWrapper("User has been created!", createdUser));

    }

    @GetMapping
    @Operation(summary = "Read All Users")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll() {

        List<UserDTO> result = userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved users", result));
    }


    @GetMapping("/{username}")
    @Operation(summary = "Read User")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again")
    // TODO  Only admin should see other profiles or current user can see his/her profile

    public ResponseEntity<ResponseWrapper> readByUserName(@PathVariable("username") String username) throws AccessDeniedException {

        UserDTO user = userService.findByUserName(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved user", user));

    }

    @PutMapping
    @Operation(summary = "Update User")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO userDTO) throws TicketingProjectException, AccessDeniedException {

        UserDTO updatedUser = userService.update(userDTO);
        return ResponseEntity.ok(new ResponseWrapper("Successfully Updated", updatedUser));
    }


    @DeleteMapping("/{username}")
    @Operation(summary = "Delete User")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) throws TicketingProjectException {
        userService.delete(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully Deleted"));
    }


    @GetMapping("role")
    @Operation(summary = "Read by role")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readByRole(@RequestParam String role) {
        List<UserDTO> userDTOList = userService.listAllByRole(role);
        return ResponseEntity.ok(new ResponseWrapper("Successfully Updated", userDTOList));
    }


    private MailDTO createEmail(UserDTO userDTO) {

        User user = mapperUtil.convert(userDTO, new User());

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setIsDeleted(false);

        ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);

        return MailDTO.builder()
                .emailTo(user.getUserName())
                .token(createdConfirmationToken.getToken())
                .subject("Confirim Registration")
                .message("To confirm your account, please click here: ")
                .url(BASE_URL + "/confirmation?token=")
                .build();

    }

    private void sendEmail(MailDTO mailDTO) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailDTO.getEmailTo());
        mailMessage.setSubject(mailDTO.getSubject());
        mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken());

        confirmationTokenService.sendEmail(mailMessage);
    }


}
