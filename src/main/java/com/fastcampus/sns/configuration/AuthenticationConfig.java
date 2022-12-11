package com.fastcampus.sns.configuration;

import com.fastcampus.sns.configuration.filter.JwtTokenFilter;
import com.fastcampus.sns.exception.CustomAuthenticationEntryPoint;
import com.fastcampus.sns.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig {

    @Value("${jwt.secret-key}")
    private String key;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService)
        throws Exception {

        return http
            .cors(httpSecurityCorsConfigurer -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.addAllowedMethod(CorsConfiguration.ALL);
                configuration.addAllowedOrigin(CorsConfiguration.ALL);
                configuration.addAllowedHeader(CorsConfiguration.ALL);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            })
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin()
            .disable()
            .httpBasic()
            .disable()
            .authorizeHttpRequests(authorization -> authorization
                .requestMatchers(PathRequest.toStaticResources()
                                            .atCommonLocations())
                .permitAll()
                .antMatchers("/api/*/users/join", "/api/*/users/login")
                .permitAll()
                .antMatchers("/api/**")
                .authenticated())
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()
            .addFilterBefore(new JwtTokenFilter(key, userService),
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }

}
