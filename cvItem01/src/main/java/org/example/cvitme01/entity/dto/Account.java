package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;

import java.util.Date;

@Entity
public interface Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String username();

    String password();

    String email();

    String role();

    String avatar();

    Date registerTime();

}
