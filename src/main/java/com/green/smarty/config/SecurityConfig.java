package com.green.smarty.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.green.smarty.mapper.UserMapper;
import com.green.smarty.util.JWTCheckFilter;
import com.green.smarty.util.JwtTokenProvider;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserMapper userMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/hc", "/env").permitAll();
                    registry.requestMatchers("/api/security/**").permitAll();
                    registry.requestMatchers("/api/user/class/").permitAll();
                    registry.requestMatchers("/api/user/products/").permitAll();
                    registry.requestMatchers("/api/user/reservation/").permitAll();
                    registry.requestMatchers("/api/admin/facilities/images/**").permitAll();
                    registry.requestMatchers("/api/admin/products/images/**").permitAll();
                    registry.requestMatchers("/api/user/reservation/uploads/**").permitAll();
                    registry.requestMatchers("/api/user/products/images/**").permitAll();
                    registry.requestMatchers("/api/user/products/files/**").permitAll();
                    registry.requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER");
                    registry.requestMatchers("/api/auth/**").hasAnyRole("ADMIN", "USER");
                    registry.requestMatchers("/api/admin/**").hasRole("ADMIN");
                    registry.anyRequest().authenticated();
                });
        http.addFilterBefore(new JWTCheckFilter(jwtTokenProvider, userMapper), AnonymousAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 Bcrypt 인코더
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
