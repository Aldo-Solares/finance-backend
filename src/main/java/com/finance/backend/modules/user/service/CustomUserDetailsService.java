package com.finance.backend.modules.user.service;

import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import com.finance.backend.security.FinanceUserDetails;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        public CustomUserDetailsService(
                        UserRepository userRepository) {

                this.userRepository = userRepository;
        }

        // ===================
        // CARGA DE USUARIO
        // ===================

        @Override
        public UserDetails loadUserByUsername(
                        String email)
                        throws UsernameNotFoundException {

                User user = userRepository
                                .findByEmailIgnoreCase(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado"));

                UserDetails details = new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                user.getEmailVerified(),
                                true,
                                true,
                                true,
                                List.of(
                                                new SimpleGrantedAuthority(
                                                                "ROLE_" + user.getRole().name())));

                return new FinanceUserDetails(user.getUserId(), details);
        }
}
