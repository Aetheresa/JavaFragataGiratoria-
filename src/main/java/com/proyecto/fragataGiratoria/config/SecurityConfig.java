package com.proyecto.fragataGiratoria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// *** IMPORTACIÓN NECESARIA PARA SER MÁS ESPECÍFICO CON EL CRUD ***
import org.springframework.http.HttpMethod; 
// *****************************************************************

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
                    // RUTAS PÚBLICAS (accesibles para todos, incluyendo no autenticados)
                    "/", "/inicio", "/menu", "/contactanos",
                    "/registro", "/login", "/carrito",
                    "/css/**", "/js/**", "/img/**", "/error/**",
                    "/productos/**" //  los productos son visibles para clientes/público
                ).permitAll()

                // =========================================================================
                // 🚀 CORRECCIÓN CLAVE: ASIGNAR EL CRUD AL ROL ADMINISTRADOR
                // =========================================================================
                // Permitir acceso total (GET, POST, DELETE, etc.) al CRUD de platillos solo al ADMINISTRADOR
                .requestMatchers("/crud/platillos", "/crud/platillos/**").hasAuthority("ADMINISTRADOR")
                
                // REGLAS PARA DASHBOARDS (deben ir antes del anyRequest)
                .requestMatchers("/roles/admin/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers("/roles/mesero/**").hasAuthority("MESERO")
                .requestMatchers("/roles/cocinero/**").hasAuthority("COCINERO")
                .requestMatchers("/roles/cliente/**").hasAuthority("CLIENTE")

                .anyRequest().authenticated() // Cualquier otra URL requiere autenticación
            )

            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("correo")
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
                            response.sendRedirect("/roles/mesero/mesero");
                            break;

                        case "COCINERO":
                            response.sendRedirect("/roles/cocinero/cocina");
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

            // Ya que tu HTML tiene el token CSRF, no es necesario deshabilitarlo, 
            // pero lo mantendremos así si te está funcionando. Si deseas habilitarlo:
            // .csrf(Customizer.withDefaults());
            .csrf(csrf -> csrf.disable()); 

        return http.build();
    }
}