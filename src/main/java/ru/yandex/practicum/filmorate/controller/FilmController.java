package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmDao filmDao = new FilmDaoImpl();

    @GetMapping
    public Collection<Film> getFilms() {
        return filmDao.getFilms().values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmDao.addFilm(film);
        return filmDao.getFilm(film.getId());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmDao.updateFilm(film);
        return filmDao.getFilm(film.getId());
    }
}
