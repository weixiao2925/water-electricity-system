package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;

import java.util.Date;

@Entity
@Table(name = "account")
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
