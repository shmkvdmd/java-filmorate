package ru.yandex.practicum.filmorate.service.mpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public List<Mpa> findAll() {
        return mpaRepository.findAll();
    }

    @Override
    public Mpa findOneById(Long id) {
        return mpaRepository.findOneById(id);
    }

    @Override
    public Mpa findMpaByFilmId(Long filmId) {
        return mpaRepository.findMpaByFilmId(filmId);
    }
}
