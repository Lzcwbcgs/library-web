package com.xiazihan.webback.security;

import com.xiazihan.webback.common.constant.Constants;
import com.xiazihan.webback.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("处理请求: {}", requestURI);

        String authHeader = request.getHeader(Constants.JWT.HEADER);
        if (authHeader != null && authHeader.startsWith(Constants.JWT.TOKEN_PREFIX)) {
            String authToken = authHeader.substring(Constants.JWT.TOKEN_PREFIX.length());
            try {
                String username = jwtTokenUtil.getUsernameFromToken(authToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("认证成功，用户: {}", username);
                    }
                }
            } catch (Exception e) {
                log.error("JWT验证失败: {}", e.getMessage());
            }
        } else {
            log.debug("未找到JWT token");
        }
        
        chain.doFilter(request, response);
    }    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();        log.debug("检查是否需要过滤请求：{}", path);
        return antPathMatcher.match("/auth/login", path)
            || antPathMatcher.match("/auth/register", path)
            || antPathMatcher.match("/doc.html", path)
            || antPathMatcher.match("/webjars/**", path)
            || antPathMatcher.match("/v3/api-docs/**", path)
            || antPathMatcher.match("/swagger-ui/**", path)
            || antPathMatcher.match("/swagger-resources/**", path)
            || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}