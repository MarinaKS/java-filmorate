package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class User {
    private int id;
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends;

    public User(int id, String email, String login, String name, LocalDate birthday, Set<Integer> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = Objects.requireNonNullElseGet(friends, HashSet::new);
    }
}
