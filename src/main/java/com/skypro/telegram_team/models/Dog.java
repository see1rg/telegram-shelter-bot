package com.skypro.telegram_team.models;

import javax.persistence.*;

@Entity
@Table(name = "dogs")
@DiscriminatorValue("DOG")
public class Dog extends Animal {
    @lombok.Getter
    final String ANIMAL_TYPE = "DOG";
}
