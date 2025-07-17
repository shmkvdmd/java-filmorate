package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private static final Integer MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmServiceImpl(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return filmDao.getFilms();
    }

    @Override
    public Film getFilm(Long id) {
        Film film = filmDao.getFilm(id);
        if (film == null) {
            log.warn(LogConstants.GET_ERROR, id);
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
        }
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        Long id = getNextId();
        film.setId(id);
        filmDao.addFilm(film);
        log.info(LogConstants.FILM_ADDED, id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long id = film.getId();
        if (id == null) {
            log.warn(LogConstants.UPDATE_ERROR, (Object) null);
            throw new ValidationException(ExceptionConstants.EMPTY_ID);
        }
        validateFilm(film);
        if (filmDao.getFilms().containsKey(id)) {
            filmDao.updateFilm(film);
            log.info(LogConstants.FILM_UPDATED, id);
            return film;
        }
        log.warn(LogConstants.UPDATE_ERROR, id);
        throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
    }

    private void validateFilm(Film film) {
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
    }

    private Long getNextId() {
        return filmDao.getFilms().keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
