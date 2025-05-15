package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "meter")
public interface Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    String type();

    @Key
    String location();

    @Key
    @Nullable
    Date installDate();

    @JoinColumn(name = "user_id")
    @ManyToOne
    @Key
    Account account();

    @OneToMany(mappedBy = "meter")
    List<Reading> readings();
}
