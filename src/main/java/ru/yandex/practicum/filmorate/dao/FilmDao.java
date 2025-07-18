package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmDao {
    Map<Long, Film> getFilms();

    Film getFilm(Long id);

    void addFilm(Film film);

    void updateFilm(Film film);
}
