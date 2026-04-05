package com.deliveratdoor.yumdrop.rateLimit.filter;

import com.deliveratdoor.yumdrop.rateLimit.serviec.SlidingWindowRateLimiter;
import com.deliveratdoor.yumdrop.util.auth.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final SlidingWindowRateLimiter rateLimiter;
    private final JwtUtil jwtUtil;

    public RateLimitFilter(SlidingWindowRateLimiter rateLimiter, JwtUtil jwtUtil) {
        this.rateLimiter = rateLimiter;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        String userId = resolveUser(request);
        if (!rateLimiter.allowRequest(userId)) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String resolveUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (!isBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token).toString();
        }

        return "anonymous"; // fallback if no token
    }
}

