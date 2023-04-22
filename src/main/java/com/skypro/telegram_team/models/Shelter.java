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

    //Удалить статические методы
    public static String getAddress() {
        return "Адрес такой то...";
    }

    public static String getSchedule() {
        return "Расписание такое...";
    }

    public static String getScheme() {
        return "Проехать так то...";
    }

    public static String getSafety() {
        return "Будьте осторожны...";
    }

    public static String getRules() {
        return "Правила...";
    }

    public static String getDocs() {
        return "Документы...";
    }

    public static String getArrangements() {
        return "Рекомендации...";
    }

    public static String getMove() {
        return "Рекомендации...";
    }

    public static String getArrangementsForPuppy() {
        return "Рекомендации...";
    }

    public static String getArrangementsForCripple() {
        return "Рекомендации...";
    }

    public static String getExpertAdvicesFirst() {
        return "Советы кинолога...";
    }

    public static String getExpertAdvicesNext() {
        return "Советы кинолога...";
    }

    public static String getRejectReasons() {
        return "Первая причина это ты...";
    }
}
