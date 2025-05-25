package com.planApiService.manage.service;

import com.planApiService.manage.config.PasswordEncoder;
import com.planApiService.manage.entity.User;
import com.planApiService.manage.dto.request.UserLoginRequest;
import com.planApiService.manage.dto.request.UserSignupRequest;
import com.planApiService.manage.dto.response.UserResponse;
import com.planApiService.manage.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        String encoded = passwordEncoder.encode(request.getPassword()); //암호화
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoded)
                .build();
        User savedUser = userRepository.save(user);

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
        // updateUser 메서드는 User entity에 만들어야 합니다.

        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse login(UserLoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        User user = userOpt.get();


        //평문을 받아 해시화 후 저장된 해시값과 비교.
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }

        return new UserResponse(user);
    }
}
