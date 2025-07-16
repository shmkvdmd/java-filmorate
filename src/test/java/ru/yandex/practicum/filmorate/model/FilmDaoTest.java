package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmDaoTest {
    private FilmDao filmDao;

    @BeforeEach
    public void setUp() {
        filmDao = new FilmDaoImpl();
    }

    @Test
    public void shouldThrowExceptionIfDescMoreThan200Chars() {
        String longDescription = "a".repeat(201);
        Film film = Film.builder()
                .name("Test Film")
                .description(longDescription)
                .releaseDate(LocalDate.now())
                .duration(Duration.ofMinutes(120))
                .build();
        assertThrows(ValidationException.class, () -> filmDao.addFilm(film));
    }

    @Test
    public void shouldThrowExceptionIfDateIncorrect() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(Duration.ofMinutes(120))
                .build();
        assertThrows(ValidationException.class, () -> filmDao.addFilm(film));
    }

    @Test
    public void shouldThrowExceptionIfDurationNegative() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test")
                .releaseDate(LocalDate.now())
                .duration(Duration.ofMinutes(-1))
                .build();
        assertThrows(ValidationException.class, () -> filmDao.addFilm(film));
    }

    @Test
    public void shouldAcceptValidFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(Duration.ofMinutes(120))
                .build();
        filmDao.addFilm(film);
        assert filmDao.getFilms().containsValue(film);
    }
}
