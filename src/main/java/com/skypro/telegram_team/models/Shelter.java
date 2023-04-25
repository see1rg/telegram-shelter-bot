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
    public enum Type {CATS, DOGS}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private Animal.TypeAnimal type;
    private String address;
    private String schedule;
    private String scheme;
    private String safety;
    private String docs;
    private String rules;
    private String arrangements;
    private String arrangementsForPuppy;
    private String arrangementsForCripple;
    private String movement;
    private String expertAdvicesFirst;
    private String expertAdvicesNext;
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
