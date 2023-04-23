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
    private String arrangements_for_puppy;
    @Column
    private String arrangements_for_cripple;
    @Column
    private String movement;
    @Column
    private String expert_advices_first;
    @Column
    private String expert_advices_next;
    @Column
    private String reject_reasons;

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
