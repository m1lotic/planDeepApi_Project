package com.planApiService.manage.controller;

import com.planApiService.manage.dto.request.UserLoginRequest;
import com.planApiService.manage.dto.request.UserSignupRequest;
import com.planApiService.manage.dto.response.UserResponse;
import com.planApiService.manage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody @Valid UserSignupRequest request) {
        System.out.println("asd");
        userService.signup(request);
    }

    @PostMapping("/login")
    public void login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        UserResponse user = userService.login(request);
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("loginEmail", user.getEmail());
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody @Valid UserSignupRequest request) {
        userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
