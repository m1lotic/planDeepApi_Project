package com.planApiService.manage.service;

import com.planApiService.manage.config.LoginRateLimiter;
import com.planApiService.manage.config.PasswordEncoder;
import com.planApiService.manage.entity.User;
import com.planApiService.manage.dto.request.UserLoginRequest;
import com.planApiService.manage.dto.request.UserSignupRequest;
import com.planApiService.manage.dto.response.UserResponse;
import com.planApiService.manage.exception.LoginFailedException;
import com.planApiService.manage.jwt.JwtUtil;
import com.planApiService.manage.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //트랜잭션 처리를 위해 분리
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginRateLimiter loginRateLimiter;

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        String encoded = passwordEncoder.encode(request.getPassword()); //암호화
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoded)
                .build();
        User savedUser = null;
        try {
           savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // 중복된 가입 요청
        }

        return new UserResponse(savedUser);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + id));
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserSignupRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + id));

        user.updateUser(request.getUsername(), request.getEmail(), request.getPassword());

        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new LoginFailedException("이메일이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginFailedException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        if (!loginRateLimiter.isAllowed(request.getEmail())) { //요청 과부하
            throw new LoginFailedException("잠시 후 다시 시도해주세요.", HttpStatus.UNAUTHORIZED);
        }

        return jwtUtil.generateToken(user.getEmail(), user.getId());
    }
}
