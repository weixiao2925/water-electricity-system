package org.example.cvitme01.repository;

import org.babyfish.jimmer.spring.repository.JRepository;
import org.example.cvitme01.entity.dto.Account;

public interface AccountRepository extends JRepository<Account, Long> {
    Account findByUsername(String username);
}
