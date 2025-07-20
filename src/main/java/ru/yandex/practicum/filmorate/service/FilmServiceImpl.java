package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private static final Integer MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

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
        film.setUserIdLikes(new HashSet<>());
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
            film.setUserIdLikes(filmDao.getFilm(id).getUserIdLikes());
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

    @Override
    public Film addLike(Long filmId, Long userId) {
        validateLike(filmId, userId);
        Film film = filmDao.getFilm(filmId);
        film.getUserIdLikes().add(userId);
        log.info(LogConstants.FILM_LIKE, userId, filmId);
        return film;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        validateLike(filmId, userId);
        Film film = filmDao.getFilm(filmId);
        film.getUserIdLikes().remove(userId);
        log.info(LogConstants.FILM_DELETE_LIKE, userId, filmId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        if (count < 0) {
            log.warn(LogConstants.NEGATIVE_REQUEST_PARAM);
            throw new ValidationException(ExceptionConstants.NEGATIVE_FILM_COUNT);
        }
        log.info(LogConstants.POPULAR_FILMS_REQUEST, count);
        return filmDao.getFilms().values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUserIdLikes().size(), f1.getUserIdLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateLike(Long filmId, Long userId) {
        if (userId <= 0 || filmId <= 0) {
            log.warn(LogConstants.NEGATIVE_REQUEST_PARAM);
            throw new ValidationException(ExceptionConstants.NEGATIVE_ID);
        }
        if (userDao.getUser(userId) == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, userId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, userId));
        }
        if (filmDao.getFilm(filmId) == null) {
            log.warn(LogConstants.FILM_NOT_FOUND_BY_ID, filmId);
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, filmId));
        }
    }

    private Long getNextId() {
        return filmDao.getFilms().keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
