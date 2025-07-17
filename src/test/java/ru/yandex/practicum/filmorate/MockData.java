package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

public class MockData {
    private MockData() {
    }

    public static Film createFilm() {
        return Film.builder()
                .name("Test Film")
                .description("Test")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(Duration.ofMinutes(120))
                .build();
    }

    public static Film createFilm(String name, String description, LocalDate releaseDate, Duration duration) {
        return Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
    }

    public static User createUser() {
        return User.builder()
                .email("user@mail.ru")
                .login("login")
                .name("Name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
    }

    public static User createUser(String email, String login, String name, LocalDate localDate) {
        return User.builder()
                .email(email)
                .login(login)
                .name(name)
                .birthday(localDate)
                .build();
    }
}
