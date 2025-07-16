package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilmDaoImpl implements FilmDao {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Integer MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilm(Long id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn(LogConstants.GET_ERROR, id);
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
        }
        return film;
    }

    @Override
    public void addFilm(Film film) {
        if (isCorrectFilm(film)) {
            Long id = getNextId();
            film.setId(id);
            films.put(id, film);
            log.info(LogConstants.FILM_ADDED, id);
        }
    }

    @Override
    public void updateFilm(Film film) {
        Long id = film.getId();
        if (id == null) {
            log.warn(LogConstants.UPDATE_ERROR, (Object) null);
            throw new ValidationException(ExceptionConstants.EMPTY_ID);
        }
        if (films.containsKey(id) && isCorrectFilm(film)) {
            films.put(id, film);
            log.info(LogConstants.FILM_UPDATED, id);
            return;
        }
        log.warn(LogConstants.UPDATE_ERROR, id);
        throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
    }

    private boolean isCorrectFilm(Film film) {
        if (film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            log.warn(LogConstants.FILM_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.DESCRIPTION_LENGTH_LIMIT);
        }
        if (film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            log.warn(LogConstants.FILM_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.WRONG_RELEASE_DATE);
        }
        if (film.getDuration().isNegative()) {
            log.warn(LogConstants.FILM_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.WRONG_FILM_DURATION);
        }
        return true;
    }

    private Long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
