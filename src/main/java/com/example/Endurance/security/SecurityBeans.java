package com.example.Endurance.security;

import com.example.Endurance.user.Roles;
import com.example.Endurance.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class SecurityBeans {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users){
        return username -> {
            var u = users.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

            var roles = (u.getRoles() != null) ? u.getRoles() : Roles.USER;
            var auths = List.of(new SimpleGrantedAuthority("ROLE_" + roles.name()));

            return new org.springframework.security.core.userdetails.User(
                    u.getName(),
                    u.getPassword(),
                    auths
            );
        };
    }
}
