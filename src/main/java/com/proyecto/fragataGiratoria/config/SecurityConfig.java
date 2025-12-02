package com.proyecto.fragataGiratoria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authProvider) throws Exception {

        http
            .authenticationProvider(authProvider)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/inicio", "/menu", "/contactanos",
                        "/registro", "/login", "/carrito",
                        "/css/**", "/js/**", "/img/**", "/error/**",
                        "/crud/**",          // ← AGREGADO
                        "/productos/**"      // ← AGREGADO
                ).permitAll()

                .requestMatchers("/roles/admin/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers("/roles/mesero/**").hasAuthority("MESERO")
                .requestMatchers("/roles/cocinero/**").hasAuthority("COCINERO")
                .requestMatchers("/roles/cliente/**").hasAuthority("CLIENTE")

                .anyRequest().authenticated()
            )

            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("correo")  // ← COINCIDIR con tu formulario
                .passwordParameter("password")

                .successHandler((request, response, authentication) -> {
                    String rol = authentication.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority();

                    switch (rol) {
                        case "ADMINISTRADOR":
                            response.sendRedirect("/roles/admin/dashboard");
                            break;

                        case "MESERO":
                            response.sendRedirect("/roles/mesero/dashboard");
                            break;

                        case "COCINERO":
                            response.sendRedirect("/roles/cocinero/dashboard");
                            break;

                        case "CLIENTE":
                        default:
                            response.sendRedirect("/roles/cliente/inicio");
                            break;
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}