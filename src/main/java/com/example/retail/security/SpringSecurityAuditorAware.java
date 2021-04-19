package com.example.retail.security;

import com.example.retail.config.Constants;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (SecurityUtils.getUser() == null) {
            return Optional.of(Constants.SYSTEM_ACCOUNT);
        } else {
            return Optional.of(SecurityUtils.getUser().getUsername());
        }
    }
}
