package com.fastcampus.sns.configuration;

import com.fastcampus.sns.configuration.filter.JwtTokenFilter;
import com.fastcampus.sns.exception.CustomAuthenticationEntryPoint;
import com.fastcampus.sns.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
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

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // /api/로 시작하는 패스외에는 ignore
//        return (web) -> web.ignoring()
//                           .regexMatchers("^(?!/api/).*")
//                           .antMatchers(HttpMethod.POST, "/api/*/users/join", "/api/*/users/login");
//    }

    // securityContext가 필요없는 곳
    @Bean
    @Order(0)
    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
        return http.requestMatchers(matchers -> matchers
                       .regexMatchers("^(?!/api/).*")
                   )
                   .authorizeHttpRequests(authorize -> authorize
                       .anyRequest()
                       .permitAll())
                   .requestCache(RequestCacheConfigurer::disable)
                   .securityContext(AbstractHttpConfigurer::disable)
                   .sessionManagement(AbstractHttpConfigurer::disable)
                   .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain ignore(HttpSecurity http) throws Exception {
        return http.requestMatchers(matchers -> matchers
                       .antMatchers(HttpMethod.POST, "/api/*/users/join", "/api/*/users/login")
                   )
                   .cors()
                   .disable()
                   .csrf()
                   .disable()
                   .authorizeHttpRequests(authorize -> authorize
                       .anyRequest()
                       .permitAll())
                   .requestCache(RequestCacheConfigurer::disable)
                   .securityContext(AbstractHttpConfigurer::disable)
                   .sessionManagement(AbstractHttpConfigurer::disable)
                   .build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService)
        throws Exception {

        return http
            .requestMatchers(matchers -> matchers
                .antMatchers("/api/**")
            )
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
                .anyRequest()
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
