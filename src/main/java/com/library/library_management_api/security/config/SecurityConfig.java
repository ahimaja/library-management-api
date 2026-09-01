package com.library.library_management_api.security.config;

import com.library.library_management_api.security.filter.JwtAuthenticationFilter;
import com.library.library_management_api.security.handler.CustomAccessDeniedHandler;
import com.library.library_management_api.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler){
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint=customAuthenticationEntryPoint;
        this.customAccessDeniedHandler=customAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(
                        session->session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/auth/register", "/auth/login")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET,"/books/**","/book-records/**")
                        .hasAnyRole("MEMBER","EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.POST,"/books/**","/book-records/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.PUT,"/books/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.PATCH,"/book-records/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.DELETE,"/books/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.GET,"/members/me")
                        .hasRole("MEMBER")

                        .requestMatchers(HttpMethod.POST,"/members/me/pay-fine")
                        .hasRole("MEMBER")

                        .requestMatchers("/members/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.GET,"/borrow-records/me","/borrow-records/me/active")
                        .hasRole("MEMBER")

                        .requestMatchers(HttpMethod.GET,"/borrow-records/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.POST,"/borrow-records/**")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers(HttpMethod.PATCH,"/borrow-records/*/void")
                        .hasAnyRole("EMPLOYEE","ADMIN")

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exception->
                        exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }
}
