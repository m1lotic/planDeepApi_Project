package com.planApiService.manage.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {//요청 제한기

    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final long TIME_WINDOW = 10000L; // 10초

    public boolean isAllowed(String key) {
        LoginAttempt attempt = loginAttempts.getOrDefault(key, new LoginAttempt(0, System.currentTimeMillis()));

        long now = System.currentTimeMillis();
        if (now - attempt.timestamp > TIME_WINDOW) {
            // Reset
            loginAttempts.put(key, new LoginAttempt(1, now));
            return true;
        } else {
            if (attempt.count >= MAX_ATTEMPTS) return false;
            loginAttempts.put(key, new LoginAttempt(attempt.count + 1, attempt.timestamp));
            return true;
        }
    }

    private static class LoginAttempt {
        int count; //요청수
        long timestamp; //마지막 요청 시간
        LoginAttempt(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}
