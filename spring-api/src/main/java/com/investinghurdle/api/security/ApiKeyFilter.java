package com.investinghurdle.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Optional API key filter. If investing-hurdle.api-key is set, requests must include X-API-Key.
 * If the key is blank, the filter is effectively disabled.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${investing-hurdle.api-key:}")
    private String apiKey;

    private static final String HEADER_NAME = "X-API-Key";
    private static final Set<String> OPEN_PATHS = Set.of(
        "/calculations/health",
        "/calculations/config",
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui/index.html",
        "/swagger-resources",
        "/error"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (!StringUtils.hasText(apiKey)) {
            return true; // disabled when no key configured
        }
        String path = request.getRequestURI();
        if (path == null) return false;
        // allow swagger and health/config
        if (path.equals("/")) return false;
        for (String open : OPEN_PATHS) {
            if (path.startsWith(open)) {
                return true;
            }
        }
        // Allow swagger static assets
        return path.startsWith("/swagger-ui/") || path.startsWith("/webjars/") || path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String supplied = request.getHeader(HEADER_NAME);
        if (!StringUtils.hasText(supplied) || !supplied.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: missing or invalid API key");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
