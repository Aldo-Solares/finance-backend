package com.finance.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

public final class FinanceUserDetails implements UserDetails {

        @Serial
        private static final long serialVersionUID = 1L;

        private final Long userId;
        private final UserDetails delegate;

        public FinanceUserDetails(Long userId, UserDetails delegate) {
                this.userId = userId;
                this.delegate = delegate;
        }

        public Long getUserId() {
                return userId;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return delegate.getAuthorities();
        }

        @Override
        public String getPassword() {
                return delegate.getPassword();
        }

        @Override
        public String getUsername() {
                return delegate.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
                return delegate.isAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
                return delegate.isAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return delegate.isCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
                return delegate.isEnabled();
        }
}
