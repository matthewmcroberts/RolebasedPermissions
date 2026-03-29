package com.matthewmcroberts.rankmanager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Checks for header: X-API-KEY: <token>
public class SimpleTokenAuthFilter extends OncePerRequestFilter {

    private final String expectedToken;

    public SimpleTokenAuthFilter(String expectedToken) {
        this.expectedToken = expectedToken;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = request.getHeader("X-API-KEY");

        if (token != null && token.equals(expectedToken)) {
            // Mark user as authenticated with a simple role
            AbstractAuthenticationToken auth =
                    new AbstractAuthenticationToken(
                            AuthorityUtils.createAuthorityList("ROLE_USER")) {
                        @Override
                        public Object getCredentials() {
                            return token;
                        }

                        @Override
                        public Object getPrincipal() {
                            return "roblox-client";
                        }
                    };
            auth.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}