package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Set;

public interface FilmService {
    Map<Long, Film> getFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    Set<Film> getPopularFilms(Long count);
}
