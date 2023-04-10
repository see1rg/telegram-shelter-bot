package com.skypro.telegram_team.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private long telegramId;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private int daysForTest;
    private LocalDateTime endTest;

    @OneToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id")
    private Animal animal;

    @Enumerated(EnumType.STRING)
    private OwnerStateEnum state;

    public enum OwnerStateEnum {
        SEARCH, PROBATION, ACCEPTED, REFUSE, PROLONGED, DECISION, BLACKLIST
    }

    private boolean isVolunteer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", telegramId=" + telegramId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", daysForTest=" + daysForTest +
                ", endTest=" + endTest +
                ", state=" + state +
                ", isVolunteer=" + isVolunteer +
                '}';
    }
}
