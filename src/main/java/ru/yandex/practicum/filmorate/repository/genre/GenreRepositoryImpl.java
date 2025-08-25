package ru.yandex.practicum.filmorate.repository.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@AllArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genre ORDER BY id;", new BeanPropertyRowMapper<>(Genre.class));
    }

    @Override
    public Genre findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genre WHERE id = ?",
                    new BeanPropertyRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format(ExceptionConstants.GENRE_NOT_FOUND_BY_ID, id));
        }
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)",
                    new BeanPropertyRowMapper<>(Genre.class), filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format(ExceptionConstants.GENRES_NOT_FOUND_BY_FILM_ID, filmId));
        }
    }
}
