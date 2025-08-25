package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private static final Integer MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public List<Film> getFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Film getFilm(Long id) {
        Film film = filmRepository.findOneById(id);
        if (film == null) {
            log.warn(LogConstants.GET_ERROR, id);
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
        }
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        Film saved = filmRepository.saveFilm(film);
        log.info(LogConstants.FILM_ADDED, saved.getId());
        return saved;
    }

    @Override
    public Film updateFilm(Film film) {
        Long id = film.getId();
        if (id == null) {
            log.warn(LogConstants.UPDATE_ERROR, (Object) null);
            throw new ValidationException(ExceptionConstants.EMPTY_ID);
        }
        validateFilm(film);
        Film updated = filmRepository.updateFilm(film);
        log.info(LogConstants.FILM_UPDATED, id);
        return updated;
    }

    private void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.warn(LogConstants.FILM_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.EMPTY_FILM_NAME);
        }
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
        filmRepository.addLike(filmId, userId);
        log.info(LogConstants.FILM_LIKE, userId, filmId);
        return getFilm(filmId);
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        validateLike(filmId, userId);
        filmRepository.removeLike(filmId, userId);
        log.info(LogConstants.FILM_DELETE_LIKE, userId, filmId);
        return getFilm(filmId);
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        if (count < 0) {
            log.warn(LogConstants.NEGATIVE_REQUEST_PARAM);
            throw new ValidationException(ExceptionConstants.NEGATIVE_FILM_COUNT);
        }
        log.info(LogConstants.POPULAR_FILMS_REQUEST, count);
        return filmRepository.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(filmRepository.getLikesCount(f2.getId()),
                        filmRepository.getLikesCount(f1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateLike(Long filmId, Long userId) {
        if (userId <= 0 || filmId <= 0) {
            log.warn(LogConstants.NEGATIVE_REQUEST_PARAM);
            throw new ValidationException(ExceptionConstants.NEGATIVE_ID);
        }
        if (userRepository.findOneById(userId) == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, userId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, userId));
        }
        if (filmRepository.findOneById(filmId) == null) {
            log.warn(LogConstants.FILM_NOT_FOUND_BY_ID, filmId);
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, filmId));
        }
    }
}
