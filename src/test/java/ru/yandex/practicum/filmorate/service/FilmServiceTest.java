package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.MockData;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private FilmService filmService;
    private FilmDao filmDao;

    @BeforeEach
    public void setUp() {
        filmDao = new FilmDaoImpl();
        filmService = new FilmServiceImpl(filmDao);
    }

    @Test
    public void shouldThrowExceptionIfDescMoreThan200Chars() {
        Film film = MockData.createFilm();
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> filmService.addFilm(film));
    }

    @Test
    public void shouldThrowExceptionIfDateIncorrect() {
        Film film = MockData.createFilm();
        film.setReleaseDate(LocalDate.of(1500, 10, 10));
        assertThrows(ValidationException.class, () -> filmService.addFilm(film));
    }

    @Test
    public void shouldThrowExceptionIfDurationNegative() {
        Film film = MockData.createFilm();
        film.setDuration(Duration.ofMinutes(-10));
        assertThrows(ValidationException.class, () -> filmService.addFilm(film));
    }

    @Test
    public void shouldAcceptValidFilm() {
        Film film = MockData.createFilm();
        Film addedFilm = filmService.addFilm(film);
        assertEquals(1L, addedFilm.getId());
        assertEquals(film.getName(), addedFilm.getName());
        assertTrue(filmDao.getFilms().containsValue(addedFilm));
    }
}
