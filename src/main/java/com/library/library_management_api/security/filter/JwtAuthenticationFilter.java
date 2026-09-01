package com.library.library_management_api.security.filter;

import com.library.library_management_api.security.handler.CustomAuthenticationEntryPoint;
import com.library.library_management_api.security.service.CustomUserDetailsService;
import com.library.library_management_api.security.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService,
                                   CustomAuthenticationEntryPoint authenticationEntryPoint){
        this.jwtService=jwtService;
        this.userDetailsService=userDetailsService;
        this.authenticationEntryPoint=authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);
            String userName = jwtService.extractUserName(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (!userDetails.isEnabled()) {
                authenticationEntryPoint.commence(
                        request,
                        response,
                        new BadCredentialsException("Account is disabled")
                );
                return;
            }
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()

                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }
        catch (JwtException |UsernameNotFoundException exception){
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid authentication token",exception)
            );
        }
    }
}
