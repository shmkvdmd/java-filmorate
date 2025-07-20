package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.MockData;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private FilmService filmService;
    private FilmDao filmDao;
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        filmDao = new FilmDaoImpl();
        userDao = new UserDaoImpl();
        filmService = new FilmServiceImpl(filmDao, userDao);
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

    @Test
    public void shouldAddLikeToFilm() {
        Film film = filmService.addFilm(MockData.createFilm());
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        Film updatedFilm = filmService.addLike(film.getId(), 1L);
        assertTrue(updatedFilm.getUserIdLikes().contains(1L));
        assertEquals(1, updatedFilm.getUserIdLikes().size());
    }

    @Test
    public void shouldThrowExceptionIfFilmNotFoundWhenAddLike() {
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        assertThrows(NotFoundException.class, () -> filmService.addLike(999L, 1L));
    }

    @Test
    public void shouldThrowExceptionIfUserNotFoundWhenAddLike() {
        Film film = filmService.addFilm(MockData.createFilm());
        assertThrows(NotFoundException.class, () -> filmService.addLike(film.getId(), 999L));
    }

    @Test
    public void shouldDeleteLikeFromFilm() {
        Film film = filmService.addFilm(MockData.createFilm());
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        filmService.addLike(film.getId(), 1L);
        Film updatedFilm = filmService.deleteLike(film.getId(), 1L);
        assertFalse(updatedFilm.getUserIdLikes().contains(1L));
        assertEquals(0, updatedFilm.getUserIdLikes().size());
    }

    @Test
    public void shouldThrowExceptionIfFilmNotFoundWhenDeletingLike() {
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        assertThrows(NotFoundException.class, () -> filmService.deleteLike(999L, 1L));
    }

    @Test
    public void shouldReturnPopularFilms() {
        Film film1 = filmService.addFilm(MockData.createFilm());
        filmService.addFilm(MockData.createFilm());
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        filmService.addLike(film1.getId(), 1L);
        List<Film> popularFilms = filmService.getPopularFilms(1L);
        assertEquals(1, popularFilms.size());
        assertTrue(popularFilms.contains(film1));
    }

    @Test
    public void shouldThrowExceptionIfCountNegative() {
        assertThrows(ValidationException.class, () -> filmService.getPopularFilms(-1L));
    }
}
