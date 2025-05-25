package com.planApiService.manage.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

//webconfig에서 빈 등록 ( 노 컴포넌트 )
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String requestURI = httpReq.getRequestURI();

        // 인증 예외 경로 설정 (회원가입, 로그인은 예외)
        if (requestURI.startsWith("/users/register") || requestURI.startsWith("/users/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 세션에서 유저 확인
        Object user = httpReq.getSession().getAttribute("loginEmail");
        if (user == null) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpRes.getWriter().write("로그인이 필요합니다.");
            return;
        }

        chain.doFilter(request, response);
    }
}
