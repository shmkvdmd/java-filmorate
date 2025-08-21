package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@AllArgsConstructor
public class MpaRepositoryImpl implements MpaRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM mpa ORDER BY id", new BeanPropertyRowMapper<>(Mpa.class));
    }

    @Override
    public Mpa findOneById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id = ?",
                    new BeanPropertyRowMapper<>(Mpa.class), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format(ExceptionConstants.RATING_NOT_FOUND_BY_ID, id));
        }
    }

    @Override
    public Mpa findMpaByFilmId(Long filmId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM mpa WHERE id IN (SELECT mpa_id FROM film WHERE film_id = ?)",
                    new BeanPropertyRowMapper<>(Mpa.class), filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format(ExceptionConstants.RATING_NOT_FOUND_BY_FILM_ID, filmId));
        }
    }
}
