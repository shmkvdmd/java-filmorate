package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepositoryImpl;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepositoryImpl;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryImpl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.MockData.createFilm;
import static ru.yandex.practicum.filmorate.MockData.createUser;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmRepositoryImpl.class, MpaRepositoryImpl.class, GenreRepositoryImpl.class, UserRepositoryImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmRepositoryImplTest {
    private final FilmRepositoryImpl filmRepository;
    private final UserRepositoryImpl userRepository;

    @Test
    void shouldSaveAndFindFilm() {
        Film saved = filmRepository.saveFilm(createFilm());
        assertNotNull(saved.getId());
        Film found = filmRepository.findOneById(saved.getId());
        assertEquals("Test Film", found.getName());
        assertNotNull(found.getMpa());
        assertEquals(1L, found.getMpa().getId());
    }

    @Test
    void shouldSaveFilmWithGenres() {
        Film film = createFilm();
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(1L, null));
        genres.add(new Genre(2L, null));
        film.setGenres(genres);

        Film saved = filmRepository.saveFilm(film);
        Set<Genre> filmGenres = filmRepository.getGenres(saved.getId());
        assertEquals(2, filmGenres.size());
        assertTrue(filmGenres.stream().anyMatch(g -> g.getId().equals(1L)));
        assertTrue(filmGenres.stream().anyMatch(g -> g.getId().equals(2L)));
    }

    @Test
    void shouldAddAndRemoveLike() {
        Film saved = filmRepository.saveFilm(createFilm());
        User user = userRepository.saveUser(createUser());
        filmRepository.addLike(saved.getId(), user.getId());
        assertEquals(1, filmRepository.getLikesCount(saved.getId()));
        filmRepository.removeLike(saved.getId(), user.getId());
        assertEquals(0, filmRepository.getLikesCount(saved.getId()));
    }

    @Test
    void shouldUpdateFilm() {
        Film saved = filmRepository.saveFilm(createFilm());
        saved.setName("Updated film");
        saved.setMpa(new Mpa(2L, null));
        Film updated = filmRepository.updateFilm(saved);
        assertEquals("Updated film", updated.getName());
        assertEquals(2L, updated.getMpa().getId());
    }

    @Test
    void shouldFindAllFilms() {
        filmRepository.saveFilm(createFilm());
        filmRepository.saveFilm(createFilm());
        List<Film> films = filmRepository.findAll();
        assertFalse(films.isEmpty());
        assertEquals(2, films.size());
    }
}
