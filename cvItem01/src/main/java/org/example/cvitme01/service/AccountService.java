package org.example.cvitme01.service;

import org.example.cvitme01.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account findByUsername(String username);
}
