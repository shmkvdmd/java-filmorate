package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {
    List<Genre> findAll();

    Genre findById(Long id);

    List<Genre> findGenresByFilmId(Long filmId);
}
