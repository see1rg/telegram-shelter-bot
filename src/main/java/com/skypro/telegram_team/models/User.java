package com.skypro.telegram_team.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    private String name;
    private String surname;
    private String phone;
    private String animal;
    private String email;
    @Lob
    private byte[] photo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name)
                && Objects.equals(surname, user.surname)
                && Objects.equals(phone, user.phone)
                && Objects.equals(animal, user.animal)
                && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, surname, phone, animal, email);
        result = 31 * result;
        return result;
    }
}
