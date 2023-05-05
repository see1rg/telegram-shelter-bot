package com.skypro.telegram_team.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "animals")
@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    private String breed;
    private String description;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToOne
    @JoinColumn(name = "shelter_id", referencedColumnName = "id")
    @JsonIgnore
    private Shelter shelter;

    @Lob
    @JsonIgnore
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    private AnimalStateEnum state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAnimal type;

    public enum TypeAnimal {
        DOG, CAT
    }

    public enum AnimalStateEnum {
        IN_SHELTER, IN_TEST, HAPPY_END
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", breed='" + breed + '\'' +
                ", description='" + description + '\'' +
                ", photo=" + Arrays.toString(photo) +
                ", state=" + state +
                '}';
    }
}
