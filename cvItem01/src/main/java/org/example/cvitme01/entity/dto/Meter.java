package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;

@Entity
@Table(name = "meter")
public interface Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String type();

    @Key
    String location();

    @JoinColumn(name = "user_id")
    @ManyToOne
    @Key
    Account account();
}
