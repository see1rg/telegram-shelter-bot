package com.skypro.telegram_team.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "shelters")
public class Shelter {
    public enum Type { CATS, DOGS }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column
    private String address;
    @Column
    private String schedule;
    @Column
    private String scheme;
    @Column
    private String safety;
    @Column
    private String docs;
    @Column
    private String rules;
    @Column
    private String arrangements;
    @Column
    private String arrangementsForPuppy;
    @Column
    private String arrangementsForCripple;
    @Column
    private String movement;
    @Column
    private String expertAdvicesFirst;
    @Column
    private String expertAdvicesNext;
    @Column
    private String rejectReasons;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return id == shelter.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
