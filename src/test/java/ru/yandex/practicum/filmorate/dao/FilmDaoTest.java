package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.MockData;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

public class FilmDaoTest {
    private FilmDao filmDao;

    @BeforeEach
    public void setUp() {
        filmDao = new FilmDaoImpl();
    }

    @Test
    public void shouldAddAndRetrieveFilm() {
        Film film = MockData.createFilm();
        film.setId(1L);
        filmDao.addFilm(film);
        assertTrue(filmDao.getFilms().containsValue(film));
        assertEquals(film, filmDao.getFilm(1L));
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = MockData.createFilm();
        film.setId(1L);
        filmDao.addFilm(film);
        Film updatedFilm = MockData.createFilm();
        updatedFilm.setId(1L);
        updatedFilm.setName("Updated Film");
        filmDao.updateFilm(updatedFilm);
        assertEquals(updatedFilm, filmDao.getFilm(1L));
    }
}
