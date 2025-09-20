package cn.edu.tju.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import cn.edu.tju.core.security.jwt.JWTFilter;
import cn.edu.tju.core.security.jwt.TokenProvider;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    // 从 permitUrlArr 中移除了 "/api/businesses/**"
    private final String[] permitUrlArr = new String[]{
            "/hello",
            "/api/auth",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/**.jsp",
            "/**.html",
            "/httpRest/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        String a = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";
        var jwtTokenFilter = new JWTFilter(new TokenProvider(a, 86400L, 108000L));
        httpSecurity.removeConfigurers(DefaultLoginPageConfigurer.class);

        var chain = httpSecurity
                .authorizeHttpRequests((requests) -> {
                    requests
                            .requestMatchers(permitUrlArr).permitAll()
                            // 已修正: 对 GET /api/businesses/** 允许匿名访问 (例如查看商家列表)
                            .requestMatchers(HttpMethod.GET, "/api/businesses", "/api/businesses/**").permitAll()
                            // 已修正: 对 POST, PUT, DELETE 等修改操作要求认证
                            .requestMatchers(HttpMethod.POST, "/api/businesses").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/businesses/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/businesses/**").authenticated()
                            .requestMatchers(HttpMethod.PATCH, "/api/businesses/**").authenticated()
                            .anyRequest()
                            .authenticated();
                })
                .headers(headersCustomizer -> {headersCustomizer
                        .cacheControl(cache -> cache.disable())
                        .frameOptions(options -> options.sameOrigin());})
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .rememberMe(rememberMe -> rememberMe.disable())
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .build();
        return chain;
    }
}