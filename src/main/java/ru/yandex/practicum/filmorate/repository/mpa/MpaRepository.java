package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRepository {
    List<Mpa> findAll();

    Mpa findOneById(Long id);

    Mpa findMpaByFilmId(Long filmId);
}
