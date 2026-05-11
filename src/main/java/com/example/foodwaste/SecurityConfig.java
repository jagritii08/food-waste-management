
package com.example.foodwaste;

import com.example.foodwaste.entity.User;
import com.example.foodwaste.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register",
                                 "/css/**", "/js/**",
                                 "/api/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/restaurant/**").hasRole("RESTAURANT")
                .requestMatchers("/ngo/**").hasRole("NGO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities()
                        .iterator().next().getAuthority();
                    if (role.equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } else if (role.equals("ROLE_RESTAURANT")) {
                        response.sendRedirect("/restaurant/dashboard");
                    } else {
                        response.sendRedirect("/ngo/dashboard");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User nahi mila: " + username));
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner createAdmin(UserRepository repo,
                                         PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setEmail("admin@foodwaste.com");
                admin.setRole("ADMIN");
                admin.setOrganizationName("Food Waste Admin");
                admin.setPhone("9999999999");
                admin.setAddress("India");
                repo.save(admin);
                System.out.println("✅ Admin ban gaya!");
            }
        };
    }

    @Bean
    public CommandLineRunner createRestaurant(UserRepository repo,
                                               PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("rest1").isEmpty()) {
                User rest = new User();
                rest.setUsername("rest1");
                rest.setPassword(encoder.encode("rest123"));
                rest.setEmail("rest1@foodwaste.com");
                rest.setRole("RESTAURANT");
                rest.setOrganizationName("Nani Ki Rasoi");
                rest.setPhone("9876543210");
                rest.setAddress("Jabalpur");
                repo.save(rest);
                System.out.println("✅ Restaurant ban gaya!");
            }
        };
    }

    @Bean
    public CommandLineRunner createNgo(UserRepository repo,
                                        PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("ngo1").isEmpty()) {
                User ngo = new User();
                ngo.setUsername("ngo1");
                ngo.setPassword(encoder.encode("ngo123"));
                ngo.setEmail("ngo1@foodwaste.com");
                ngo.setRole("NGO");
                ngo.setOrganizationName("Help NGO");
                ngo.setPhone("9876543211");
                ngo.setAddress("Jabalpur");
                repo.save(ngo);
                System.out.println("✅ NGO ban gaya!");
            }
        };
    }
}