package com.skypro.telegram_team.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    private String breed;
    private String description;
    private String image;
    private String owner;
    private String ownerId;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id) && Objects.equals(name, animal.name)
                && Objects.equals(breed, animal.breed) && Objects.equals(description, animal.description)
                && Objects.equals(image, animal.image) && Objects.equals(owner, animal.owner)
                && Objects.equals(ownerId, animal.ownerId) && Objects.equals(status, animal.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, breed, description, image, owner, ownerId, status);
    }
}
