package com.proyecto.fragataGiratoria.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // 🔹 actualizado

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(authProvider())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/inicio", "/menu", "/contactanos",
                        "/registro", "/login", "/carrito",
                        "/css/**", "/js/**", "/img/**"
                ).permitAll()

                .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers("/mesero/**").hasAuthority("MESERO")
                .requestMatchers("/cocina/**").hasAuthority("COCINERO")
                .requestMatchers("/cliente/**").hasAuthority("CLIENTE") // 🔹 acceso clientes

                .anyRequest().authenticated()
            )

            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("correo")
                .passwordParameter("password")

                // ⭐ Redirección según rol ⭐
                .successHandler((request, response, authentication) -> {

                    String rol = authentication.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority();

                    switch (rol) {
                        case "ADMINISTRADOR":
                            response.sendRedirect("/admin/dashboard");
                            break;

                        case "MESERO":
                            response.sendRedirect("/mesero/dashboard");
                            break;

                        case "COCINERO":
                            response.sendRedirect("/cocina/dashboard");
                            break;

                        case "CLIENTE":
                        default:
                            response.sendRedirect("/cliente/inicio"); // 🔹 nuevo inicio cliente
                            break;
                    }
                })
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
