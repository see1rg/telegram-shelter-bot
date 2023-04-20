package com.skypro.telegram_team.models;

import javax.persistence.*;

@Entity
@Table(name = "cats")
@DiscriminatorValue("CAT")
public class Cat extends Animal {
    @lombok.Getter
     final  String  ANIMAL_TYPE = "CAT";

}
